package cz.mkozeny.dekodar.session.sale;

import java.math.BigDecimal;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.Product;
import cz.mkozeny.dekodar.entity.Sale;
import cz.mkozeny.dekodar.entity.SaleLine;
import cz.mkozeny.dekodar.entity.Vat;

@Local
public interface SaleAction {

	public void newSale();

	public void selectSale(Sale selectedSale);

	public void selectProduct(Product selectedProduct) throws Exception;

	public void addNewSaleLine() throws Exception;

	public void deleteSaleLine(SaleLine selectedSaleLine)
			throws StockAvailAbilityOperationException;

	public void changePriceLine(SaleLine line);

	public void changePriceTableLine(SaleLine line);

	public void changeDiscount(BigDecimal discount);

	public void destroy();

	public String getProductId();

	public void setProductId(String productId);

	public Integer getQuantity();

	public void setQuantity(Integer quantity);

	public void changeStateToClosed(Sale s);

	public void init();

	public void saveSale();

	public String showProductDetail(String pnc);

	public Vat getVat();

	public void setVat(Vat vat);

	public void open();

	public void process();

	public void cancel();

}