package cz.mkozeny.dekodar.session.product;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.Product;

@Local
public interface ProductAction {
	
	public void selectProductById(Long pnc);

	public void selectProduct(Product selectedProduct);

	public void newProduct();

	public void addProduct();

	public void saveProduct();

	public void destroy();

}