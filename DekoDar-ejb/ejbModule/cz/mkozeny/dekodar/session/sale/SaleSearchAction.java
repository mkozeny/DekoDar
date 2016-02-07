package cz.mkozeny.dekodar.session.sale;

import java.util.Date;

import cz.mkozeny.dekodar.entity.Sale;
import cz.mkozeny.dekodar.entity.Sale.PaymentType;
import cz.mkozeny.dekodar.entity.Sale.SaleStateType;

public interface SaleSearchAction {

	public void searchSales();

	public void destroy();

	public String getId();

	public void setId(String id);

	public Date getDateFrom();

	public void setDateFrom(Date dateFrom);

	public Date getDateTo();

	public void setDateTo(Date dateTo);

	public void setPaymentType(PaymentType paymentType);

	public PaymentType getPaymentType();

	public PaymentType[] getPaymentTypes();
	
	public SaleStateType[] getSaleStateTypes();

	public String getUsername();

	public void setUsername(String s);

	public void saleUpdated(Sale selectedSale);

	public SaleStateType getSaleStateType();

	public void setSaleStateType(SaleStateType saleStateType);

}