package cz.mkozeny.dekodar.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.Length;

@Entity
public class Vat implements Serializable {

	@Id
	@Length(max = 5)
	String vatCode;

	Double koef;

	String description;

	public void setKoef(Double koef) {
		this.koef = koef;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public Double getKoef() {
		return koef;
	}

	public String getDescription() {
		return description;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		Vat vat = (Vat) obj;
		return vat.getVatCode().equals(this.getVatCode());
	}

	public int hashCode() {

		return vatCode.hashCode();
	}

}
