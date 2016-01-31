package cz.mkozeny.dekodar.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class AccountingPeriod {

	@Id
	@GeneratedValue
	Integer id;

	Date validFrom;

	Date validTo;

	@Length(max = 30)
	String description;
	
	@Length(max=2)
	String idSeriePrefix;

	@ManyToOne
	@NotNull
	Vat loVat;

	@ManyToOne
	@NotNull
	Vat hiVat;

	@ManyToOne
	@NotNull
	Vat noVat;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getIdSeriePrefix() {
		return idSeriePrefix;
	}
	
	public void setIdSeriePrefix(String idSeriePrefix) {
		this.idSeriePrefix = idSeriePrefix;
	}

	public Vat getLoVat() {
		return loVat;
	}

	public void setLoVat(Vat loVat) {
		this.loVat = loVat;
	}

	public Vat getHiVat() {
		return hiVat;
	}

	public void setHiVat(Vat hiVat) {
		this.hiVat = hiVat;
	}

	public Vat getNoVat() {
		return noVat;
	}

	public void setNoVat(Vat noVat) {
		this.noVat = noVat;
	}

	public Vat[] getAllVats() {
		if (hiVat == null || loVat == null || noVat == null)
			return new Vat[3];
		Vat[] result = new Vat[3];
		result[0] = hiVat;
		result[1] = loVat;
		result[2] = noVat;

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		AccountingPeriod aPeriod = (AccountingPeriod) obj;
		return aPeriod.getId().equals(this.getId());
	}

	public int hashCode() {
		if (id == null)
			return -1;
		return id.hashCode();
	}

}
