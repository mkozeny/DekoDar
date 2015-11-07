package cz.mkozeny.dekodar.session.product;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.Product;

@Local
public interface ProductSearchAction {

	public String getId();

	public void setId(String id);

	public String getModel();

	public void setModel(String model);

	public void searchProducts();

	public void productUpdates(Product selectedProduct);
	
	public void destroy();

}