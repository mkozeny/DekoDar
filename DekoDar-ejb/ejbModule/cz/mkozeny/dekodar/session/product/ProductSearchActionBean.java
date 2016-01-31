package cz.mkozeny.dekodar.session.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.jacorb.transaction.Sleeper;
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
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeRowKey;

import cz.mkozeny.dekodar.entity.Product;
import cz.mkozeny.dekodar.entity.ProductCategory;

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

	private Collection<ProductCategory> productCategories;

	private List<ProductCategory> allProductCategories = new ArrayList<ProductCategory>();

	private ProductCategory selectedProductCategory;

	private ProductCategory productCategory;

	private List<ProductCategory> selectedNodeChildren = new ArrayList<ProductCategory>();

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

		if (selectedNodeChildren.size() > 0) {
			Iterator<ProductCategory> selectedCategories = selectedNodeChildren
					.iterator();
			String cond = "p.productCategory.id IN (";
			while (selectedCategories.hasNext()) {
				Long token = selectedCategories.next().getId();
				cond = cond + token;
				if (selectedCategories.hasNext())
					cond = cond + ", ";

			}
			whereConditions.add(cond + ")");
		}

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

	public Collection<ProductCategory> getProductCategories() {
		if (productCategories == null) {
			fetchProductCategories();
		}
		return productCategories;
	}

	private void fetchProductCategories() {
		ProductCategory rootProductCategory = (ProductCategory) em.createQuery(
				"select p from ProductCategory p where p.type='ROOT'")
				.getSingleResult();
		this.allProductCategories.clear();
		this.allProductCategories.add(rootProductCategory);
		processNodeChildren(rootProductCategory, allProductCategories);
		productCategories = Collections
				.singletonList(rootProductCategory);
	}

	public void processSelection(NodeSelectedEvent event) {
		HtmlTree tree = (HtmlTree) event.getComponent();
		ProductCategory menuNode = (ProductCategory) tree.getRowData();
		productCategory = selectedProductCategory = menuNode;
		selectedNodeChildren.clear();
		selectedNodeChildren.add(selectedProductCategory);
		processNodeChildren(selectedProductCategory, selectedNodeChildren);
		System.out.println(selectedNodeChildren);
		// productSearchAction.searchProducts();
	}

	private void processNodeChildren(ProductCategory parent,
			List<ProductCategory> nodeChildren) {
		for (ProductCategory category : parent.getSubcategories()) {
			nodeChildren.add(category);
			processNodeChildren(category, nodeChildren);
		}
	}

	public Boolean adviseNodeOpened(UITree tree) {
		Object key = tree.getRowKey();
		TreeRowKey treeRowKey = (TreeRowKey) key;
		if (treeRowKey == null || treeRowKey.depth() <= 1) {
			return Boolean.TRUE;
		}
		return false;
	}

	@Observer("productUpdated")
	public void productUpdates(Product selectedProduct) {
		if (selectedProduct != null) {
			try {
				int index = ((List<Product>) products).indexOf(selectedProduct);
				products.set(index, selectedProduct);
			} catch (Exception ex) {
				// ex.printStackTrace();
			};
		}
	}

	public void saveProductCategory() {
		if (productCategory != null
				&& productCategory.getId() != null) {
			em.merge(productCategory);
		} else {
			em.persist(productCategory);
			selectedProductCategory.getSubcategories().add(productCategory);
			em.merge(selectedProductCategory);
		}
		fetchProductCategories();
		productCategory = null;
		selectedProductCategory = null;
	}

	public void removeProductCategory() {
		if (selectedProductCategory != null
				&& selectedProductCategory.getId() != null) {
			Query productsOfProductCategoryQuery = em
					.createQuery("select p from Product p where p. productCategory = :selectedCategory");
			productsOfProductCategoryQuery.setParameter("selectedCategory",
					selectedProductCategory);
			List<Product> productsOfProductCategory = productsOfProductCategoryQuery
					.getResultList();
			for (Product product : productsOfProductCategory) {
				product.setProductCategory(null);
				em.merge(product);
			}
			em.remove(em.merge(selectedProductCategory));
			fetchProductCategories();
			productCategory = null;
			selectedProductCategory = null;
		}
	}
	
	public void newProductCategory() {
		productCategory = new ProductCategory();
		productCategory.setType(ProductCategory.Type.CHILD);
	}

	public List<ProductCategory> getAllProductCategories() {
		return allProductCategories;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public ProductCategory getSelectedProductCategory() {
		return selectedProductCategory;
	}

	@Remove
	@Destroy
	public void destroy() {

	}

	public void unsetProductCategory() {
		selectedNodeChildren.clear();
		
	}

}
