package cz.mkozeny.dekodar.session.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.entity.Product;

@Stateful
@Name("productSearchAction")
@Scope(ScopeType.SESSION)
public class ProductSearchActionBean implements ProductSearchAction,
		Serializable {

	@DataModel
	List<Product> products;

	private String id = "";
	private String model = "";
	
	@Out(required = false)
	@In(required = false)
	Integer productListPageIndex = 1;

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	@PersistenceContext
	private EntityManager em;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Factory(value = "products")
	@Observer("updateProducts")
	public void searchProducts() {
		productListPageIndex = 1;

		String query = "select p from Product p";
		ArrayList<String> whereConditions = new ArrayList<String>();

		if (id.length() > 0)
			whereConditions.add(" p.id = '" + id + "%' ");

		if (model.length() > 0)
			whereConditions.add(" p.model like '" + model + "%'");

		int counter = 0;
		for (String condition : whereConditions) {
			if (counter == 0)
				query = query + " where ";
			query = query + condition;
			if (counter + 1 < whereConditions.size())
				query = query + " and ";
			counter++;
		}

		log.info("query products: " + query);
		products = em.createQuery(query).setMaxResults(1500).getResultList();
	}

	@Observer("productUpdated")
	public void productUpdates(Product selectedProduct) {
		if (selectedProduct != null) {
			try {
				int index = ((List<Product>) products).indexOf(selectedProduct);
				products.set(index, selectedProduct);
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			;

		}
	}

	@Remove
	@Destroy
	public void destroy() {

	}

}
