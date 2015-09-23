package cz.mkozeny.dekodar;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import cz.mkozeny.dekodar.entity.Vat;



@Embeddable
public class Price implements Serializable{
	
	private BigDecimal amount = new BigDecimal(0);
	
	@ManyToOne
    @JoinColumn (name="vat_vatCode")

	private Vat vat;
	
	
	public Price() {
		super();
	}

	public Price(BigDecimal amount, Vat vat) {
		super();
		this.amount = amount;
		this.vat = vat;
	}
	
	
	public BigDecimal getPriceExclVat() {
		return amount;
	}
	
	
	public BigDecimal getPriceInclVat() {
		if(vat==null)
			return BigDecimal.ZERO;
		
		return amount.multiply(new BigDecimal(1 + vat.getKoef()));
	}
	
	public BigDecimal getVatAmount() {	
		if(vat == null)
			return BigDecimal.ZERO;
		return amount.multiply(new BigDecimal(vat.getKoef()));
	}
	
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
	}
	
}
