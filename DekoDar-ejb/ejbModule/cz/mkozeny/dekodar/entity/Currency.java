package cz.mkozeny.dekodar.entity;

import javax.persistence.*;

import org.hibernate.validator.*;

@Entity
public class Currency {

	@Id
	@Length(max = 3)
	String currencyCode;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

}
