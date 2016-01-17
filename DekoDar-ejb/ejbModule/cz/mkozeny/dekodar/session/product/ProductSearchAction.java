package cz.mkozeny.dekodar.session.product;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;

import cz.mkozeny.dekodar.entity.Product;
import cz.mkozeny.dekodar.entity.ProductCategory;

@Local
public interface ProductSearchAction {

	public String getId();

	public void setId(String id);

	public String getModel();

	public void setModel(String model);

	public void searchProducts();
	
	public Collection<ProductCategory> getProductCategories();
	
	public void processSelection(NodeSelectedEvent event);
	
	public Boolean adviseNodeOpened(UITree tree);
	
	public void productUpdates(Product selectedProduct);
	
	public void newProductCategory();
	
	public void saveProductCategory();
	
	public void removeProductCategory();
	
	public List<ProductCategory> getAllProductCategories();
	
	public ProductCategory getProductCategory();
	
	public ProductCategory getSelectedProductCategory();
	
	public void destroy();

}