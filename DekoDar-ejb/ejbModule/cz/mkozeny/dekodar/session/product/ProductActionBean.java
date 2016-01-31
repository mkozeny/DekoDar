package cz.mkozeny.dekodar.session.product;

import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.entity.Product;
import cz.mkozeny.dekodar.entity.StockAvailAbility;
import cz.mkozeny.dekodar.session.applicationlog.ApplicationLogAction;
import cz.mkozeny.dekodar.session.utils.SeriesGenerator;

@Stateful
@Name("productAction")
public class ProductActionBean implements ProductAction {

	@In
	ApplicationLogAction applicationLogAction;

	@Out(required = false)
	@In(required = false)
	Product product;

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	@PersistenceContext
	private EntityManager em;
	
	@In(create = true)
	SeriesGenerator seriesGenerator;
	
	@Begin(join=true)
    public void selectProductById(String pnc) {
    	selectProduct(em.find(Product.class, pnc));
    }

	@Begin(join = true)
	public void selectProduct(Product selectedProduct) {
		log.info("Product #0 has been selected", selectedProduct.getId());
		String id = selectedProduct.getId();
		product = (Product) em.createQuery("select p from Product p left join fetch p.availAbility where p.id='" + id + "'").getSingleResult();
		String message = "Produkt " + id + " byl vybrán";

		applicationLogAction
				.createLog(
						cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
						message);
	}

	@Begin(join = true)
	public void newProduct() {
		product = new Product();
	}

	public void addProduct() {
		product.setCreateDate(new Date());
		
		String productId = seriesGenerator.getProductPk(seriesGenerator.getDefaultAccountingPeriod());
		product.setId(productId);
		
		StockAvailAbility availAbility = product.getAvailAbility();
		availAbility.setAvailAbility(0L);
		em.persist(availAbility);
		product.setAvailAbility(availAbility);
		em.persist(product);
		String message = "Produkt " + product.getId() + " byl přidán";
		applicationLogAction
				.createLog(
						cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
						message);
		facesMessages.add(message);
		Events.instance().raiseEvent("updateProducts");
	}

	public void saveProduct() {
		em.merge(product);
		String message = "Produkt " + product.getId() + " byl uložen";
		applicationLogAction
				.createLog(
						cz.mkozeny.dekodar.entity.ApplicationLog.Severity.INFO,
						message);
		facesMessages.add(message);
		Events.instance().raiseEvent("productUpdated", product);
	}

	@Remove
	@Destroy
	public void destroy() {

	}

}
