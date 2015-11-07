package cz.mkozeny.dekodar.session.sale;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.validator.Length;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.entity.Sale;
import cz.mkozeny.dekodar.entity.Sale.PaymentType;
import cz.mkozeny.dekodar.entity.Sale.SaleStateType;

@Stateful
@Name("saleSearchAction")
@Scope(ScopeType.SESSION)
public class SaleSearchActionBean implements SaleSearchAction, Serializable {

	/*
	 * 
	 */
	private static final long serialVersionUID = 8627061770806680454L;

	@DataModel
	List<Sale> sales;

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	@PersistenceContext
	private EntityManager em;

	private String id;

	private Date dateFrom;

	private Date dateTo;

	private PaymentType paymentType;

	private String username;

	private SaleStateType saleStateType;

	// TODO user
	// jak ten co zalozil, tak ten co zavrel

	@Out(required = false)
	@In(required = false)
	Integer saleListPageIndex = 1;

	// Date dueDate;

	// Date vatDate;

	// @Embedded
	// Price price;

	// @ManyToOne
	// User user;

	// sleva na urovni dokladu
	// BigDecimal discount;

	@Length(max = 60)
	// String description;
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentType getPaymentType() {
		return this.paymentType;
	}

	public PaymentType[] getPaymentTypes() {

		return PaymentType.values();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String s) {
		this.username = s;
	}

	@Observer("refreshSales")
	public void searchSales() {
		saleListPageIndex = 1;

		String queryStr = "select s from Sale s ";

		ArrayList<String> whereConditions = new ArrayList<String>();

		if (id != null && id.length() > 0)
			whereConditions.add(" s.id =" + id + "");

		if (username != null && username.length() > 0)
			whereConditions.add(" s.user.username like '" + username + "%'");

		if (saleStateType != null) {
			whereConditions.add(" s.state = '" + saleStateType + "'");
		}

		if (dateFrom != null)
			whereConditions.add(" s.dateCreated >= :dateFrom");

		if (dateTo != null)
			whereConditions.add(" s.dateCreated < :dateTo");

		if (paymentType != null)
			whereConditions.add(" s.paymentType = '" + paymentType.toString()
					+ "'");

				int counter = 0;
		for (String condition : whereConditions) {
			if (counter == 0)
				queryStr = queryStr + " where ";
			queryStr = queryStr + condition;
			if (counter + 1 < whereConditions.size())
				queryStr = queryStr + " and ";
			counter++;
		}

		//queryStr = queryStr + " order by s.dateCreated desc";

		Query query = em.createQuery(queryStr);
		if (dateFrom != null)
			query.setParameter("dateFrom", dateFrom);
		if (dateTo != null)
			query.setParameter("dateTo", dateTo);

		//query.setMaxResults(1500);

		log.info("query sales: " + queryStr);
		sales = query.getResultList();
	}

	public SaleStateType getSaleStateType() {
		return saleStateType;
	}

	public void setSaleStateType(SaleStateType saleStateType) {
		this.saleStateType = saleStateType;
	}

	@Observer("saleUpdated")
	public void saleUpdated(Sale selectedSale) {
		if (selectedSale != null) {
			try {
				int index = ((List<Sale>) sales).indexOf(selectedSale);
				sales.set(index, selectedSale);
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			;

		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Remove
	@Destroy
	public void destroy() {

	}

}
