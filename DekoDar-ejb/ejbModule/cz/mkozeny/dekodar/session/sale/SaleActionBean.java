package cz.mkozeny.dekodar.session.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.Price;
import cz.mkozeny.dekodar.entity.Product;
import cz.mkozeny.dekodar.entity.Sale;
import cz.mkozeny.dekodar.entity.Sale.PaymentType;
import cz.mkozeny.dekodar.entity.Sale.SaleStateType;
import cz.mkozeny.dekodar.entity.SaleLine;
import cz.mkozeny.dekodar.entity.StockAvailAbility;
import cz.mkozeny.dekodar.entity.User;
import cz.mkozeny.dekodar.entity.Vat;
import cz.mkozeny.dekodar.session.applicationlog.ApplicationLogAction;
import cz.mkozeny.dekodar.session.product.ProductAction;
import cz.mkozeny.dekodar.session.utils.SeriesGenerator;

@Stateful
@Name("saleAction")
@Scope(ScopeType.CONVERSATION)
public class SaleActionBean implements SaleAction {

	@In
	ApplicationLogAction applicationLogAction;

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	@PersistenceContext
	private EntityManager em;

	@In(create = true)
	SeriesGenerator seriesGenerator;

	@In
	User signedUser;

	@In(required = false)
	@Out(required = false)
	Sale sale;

	@Out(required = false)
	@In(required = false)
	SaleLine saleLine;

	@In(create = true)
	ProductAction productAction;

	private String stateChangeDescription;

	Integer currentRow;

	Long productId;

	// String username;

	Integer quantity;

	String password;

	Vat vat;

	String username;

	@Begin
	public void newSale() {
		sale = new Sale();
		sale.setUser(signedUser);
		Date today = new Date();
		sale.setDateCreated(today);
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_YEAR, 14);
		sale.setPaymentType(PaymentType.CASH);
		sale.setAccountingPeriod(seriesGenerator.getDefaultAccountingPeriod());
		sale.setState(SaleStateType.OPENED);
	}

	@Create
	public void init() {

		quantity = 1;
		vat = em.find(Vat.class, "20");

	}

	public String showProductDetail(String id) {
		// overime, zda-li PNC existuje
		String query = "select p from Product p where p.id = " + id;
		ArrayList<Product> pom = (ArrayList<Product>) em.createQuery(query)
				.setMaxResults(1).getResultList();
		if (pom.size() == 0) {
			String message = "Produkt " + id + " nebyl nalezen.";
			facesMessages.add(Severity.ERROR, message);
			return message;
		}
		productAction.selectProduct(pom.get(0));
		return "OK";
	}

	public void changeStateToClosed(Sale s) {
		changeState(s, SaleStateType.CLOSED);
	}

	@Begin
	public void selectSale(Sale selectedSale) {
		sale = em.find(Sale.class, selectedSale.getId());
		// fetch lines
		sale.getSaleLines().size();

		String message = "Doklad " + sale.getId() + " byl vybrán";
		log.info(message);
		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				message, sale.getId());

	}

	public void refreshPaymentType() {
		Sale pom = em.find(Sale.class, sale.getId());
		sale.setPaymentType(pom.getPaymentType());
		String message = "Byl volan refreshPayment pro doklad: " + sale.getId();
		log.info(message);
		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				message, sale.getId());
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addNewSaleLine() throws Exception {

		log.info("Product #0 has been selected for Sale", productId);

		SaleLine line = new SaleLine();
		Product prod = null;
		try {
			// prod = em.find(Product.class, productId);
			String queryStr = "Select p from Product p where id =" + productId;
			Query query = em.createQuery(queryStr);
			query.setMaxResults(1);
			ArrayList<Product> pom = (ArrayList<Product>) query.getResultList();
			if (pom.size() > 0) {
				prod = pom.get(0);
				productId = prod.getId();
			}

		} catch (Exception e) {
			facesMessages.add("Chyba při hledání produktu.");
			log.error("Chyba pri hledani produktu", e);
			return;
		}

		if (prod == null) {
			String message = "Produkt " + productId + " nebyl nalezen.";
			facesMessages.add(Severity.ERROR, message);
			return;
		}

		// overeni dostupnosti na sklade 00
		// Stock mainStock = em.find(Stock.class, "00");
		StockAvailAbility sa = prod.getAvailAbility();
		String message = "Nedostatečný zůstatek na skladě: " + prod.getId();
		if (sa == null) {
			facesMessages.add(Severity.WARN, message);
		} else {
			if (sa.getAvailAbility() < quantity)
				facesMessages.add(Severity.WARN, message);
		}

		line.setProduct(prod);
		line.setPrice(new Price(new BigDecimal(prod.getPrice()), vat));
		// line.getPrice().setAmount(new BigDecimal(prod.getPrice()));
		line.setQuantity(quantity);
		line.setLineNumber(sale.getSaleLines().size() + 1);
		// line.getPrice().setVat(vat);

		em.persist(line);
		sale.getSaleLines().add(line);
		sale.recalculateTotalPrices();

		if (sale.getId() == null) {

			addSale();
		} else {
			ClassValidator<Sale> sc = new ClassValidator(Sale.class);
			for (InvalidValue v : sc.getInvalidValues(sale)) {
				log.info(v.getMessage() + "jmeno" + v.getPropertyName());
			}

			for (SaleLine s : sale.getSaleLines()) {
				ClassValidator<SaleLine> sl = new ClassValidator(SaleLine.class);
				for (InvalidValue v : sl.getInvalidValues(saleLine)) {
					log.info(v.getMessage() + "jmeno" + v.getPropertyName());
				}
			}

			em.merge(sale);
		}

		sa.setAvailAbility(sa.getAvailAbility() - quantity);
		em.merge(sa);

		// upravit do vychoziho stavu
		productId = null;
		quantity = 1;

		message = "Řádek dokladu byl přidán uživatelem: "
				+ signedUser.getUsername() + ".";
		log.info("Add invoiceLine (#{product.id}) to invoice");
		facesMessages.add(message);

		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				"Řádek dokladu byl přidán v počtu " + line.getQuantity()
						+ "ks. Uživatelem " + signedUser.getUsername() + ".",
				sale.getId());

	}

	public void selectProduct(Product selectedProduct) throws Exception {
		this.productId = selectedProduct.getId();
	}

	public void deleteSaleLine(SaleLine selectedSaleLine)
			throws StockAvailAbilityOperationException {

		StockAvailAbility sa = em.find(Product.class, selectedSaleLine.getProduct().getId())
				.getAvailAbility();
		sa.setAvailAbility(sa.getAvailAbility()
				+ selectedSaleLine.getQuantity());
		em.merge(sa);

		sale.getSaleLines().remove(selectedSaleLine);

		em.remove(em.merge(selectedSaleLine));

		sale.recalculateTotalPrices();
		fixOrderLineNumbers();
		String message = "Řádek dokladu byl smazán v počtu "
				+ selectedSaleLine.getQuantity() + "ks. uživatelem:"
				+ signedUser.getUsername() + ".";

		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				message, sale.getId());
		facesMessages.add(message);
		Events.instance().raiseEvent("stockOperationListUpdated");
	}

	public void changePriceLine(SaleLine line) {
		// saleLine.setPrice(line.getPrice());
		// saleLine.setQuantity(line.getQuantity());

		if (line.getDiscount() != null)
			saleLine.getPrice().setAmount(
					line.getPrice()
							.getPriceExclVat()
							.multiply(
									BigDecimal.ONE.subtract(line.getDiscount()
											.divide(new BigDecimal(100)))));
		sale.recalculateTotalPrices();
	}

	public void changePriceTableLine(SaleLine line) {
		/*
		 * if (line.getDiscount() != null) line.getPrice().setAmount(
		 * line.getPrice().getPriceExclVat().multiply(
		 * BigDecimal.ONE.subtract(line.getDiscount().divide( new
		 * BigDecimal(100)))));
		 */
		sale.recalculateTotalPrices();
	}

	// TODO nema tady bejt hlidani bezpecnosti ????
	public void changeDiscount(BigDecimal discount) {
		sale.setDiscount(discount);
		sale.recalculateTotalPrices();

	}

	private void fixOrderLineNumbers() {

		Integer counter = 1;
		for (SaleLine saleLine : sale.getSaleLines()) {

			if (counter != saleLine.getLineNumber()) {
				saleLine.setLineNumber(counter);

			}

			counter++;
		}

	}

	public void saveSale() {

		fixOrderLineNumbers();
		sale.recalculateTotalPrices();

		em.merge(sale);

		log.info("save Sale #0", sale.getId());
		String message = "Doklad " + sale.getId() + " byl uložen uživatelem "
				+ signedUser.getUsername() + ".";

		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				message, sale.getId());

		facesMessages.add(message);

		Events.instance().raiseEvent("saleUpdated", sale);

	}

	private void addSale() throws Exception {

		em.persist(sale);
		changeState(SaleStateType.OPENED);

		log.info("Sale #{sale.id} has been saved");
		// kvuli propisovani do DB
		String message = "Účetní doklad " + sale.getId()
				+ " byl vložen uživatelem " + signedUser.getUsername() + ".";
		facesMessages.add(message);

		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				message, sale.getId());
		Events.instance().raiseEvent("refreshSales", sale);
	}

	@Remove
	@Destroy
	public void destroy() {

	}

	public void open() {
		changeState(SaleStateType.OPENED);
	}

	public void process() {

		sale.recalculateTotalPrices();
		fixOrderLineNumbers();

		for (SaleLine saleLine : sale.getSaleLines()) {
			if (saleLine.getLinePrice().getAmount().compareTo(BigDecimal.ZERO) < 0
					|| saleLine.getQuantity() < 0) {
				facesMessages.add(Severity.WARN,
						"Doklad nesmí mít zápornou cenu nebo počet ks.");
				return;
			}
		}

		applicationLogAction.createLogForSale(
				cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
				"Assign user " + signedUser.getUsername(), sale.getId());
		sale.setUser(signedUser);
		sale = em.merge(sale);

		changeState(SaleStateType.PROCESSED);
	}

	public void cancel() {
		StockAvailAbility sa = em.find(Product.class, productId)
				.getAvailAbility();

		// musime naskladnit polozky zpet
		for (SaleLine sl : sale.getSaleLines()) {
			sa.setAvailAbility(sa.getAvailAbility()
					+ sl.getQuantity().longValue());
		}
		em.merge(sa);
		changeState(SaleStateType.CANCELLED);
	}

	private void changeState(SaleStateType destState) {
		changeState(sale, destState);
	}

	private void changeState(Sale selectedSale, SaleStateType destState) {
		if (selectedSale.getState() != destState) {
			Sale pom = em.find(Sale.class, selectedSale.getId());
			pom.setState(destState);
			selectedSale = em.merge(pom);
		}

		sale = selectedSale;
		Events.instance().raiseEvent("saleUpdated", selectedSale);

	}

	public Integer getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(Integer currentRow) {
		this.currentRow = currentRow;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
	}

}
