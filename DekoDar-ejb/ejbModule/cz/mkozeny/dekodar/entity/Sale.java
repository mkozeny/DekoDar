package cz.mkozeny.dekodar.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("SALE")
public class Sale {

	public static enum SaleStateType {
		OPENED("Sale_opened"), CLOSED("Sale_closed");

		SaleStateType(String keyName) {
			this.keyName = keyName;
		}

		private String keyName;

		public String getKeyName() {
			return keyName;
		}
	}

	public static enum PaymentType {
		CASH("payment_cash"), CREDIT_CARD("payment_credit_card");

		PaymentType(String keyName) {
			this.keyName = keyName;
		}

		private String keyName;

		public String getKeyName() {
			return keyName;
		}
	}

	@Id
	@Length(max = 10)
	String id;

	@NotNull
	Date dateCreated;

	@ManyToOne
	@NotNull
	User user;

	// sleva na urovni dokladu
	BigDecimal discount;

	@Length(max = 60)
	String description;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OrderBy("lineNumber")
	@JoinColumn(name = "sale_id")
	Collection<SaleLine> saleLines = new ArrayList<SaleLine>();

	@Lob
	private String note;

	@Enumerated(EnumType.STRING)
	@NotNull
	PaymentType paymentType;

	protected BigDecimal totalPriceExclVatValue = BigDecimal.ZERO;

	protected BigDecimal totalVatValue = BigDecimal.ZERO;

	protected BigDecimal totalPriceInclVatValue = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@NotNull
	SaleStateType state;

	@ManyToOne
	@NotNull
	AccountingPeriod accountingPeriod;

	public void recalculateTotalPrices() {
		totalPriceExclVatValue = BigDecimal.ZERO;
		totalVatValue = BigDecimal.ZERO;

		for (SaleLine saleLine : saleLines) {
			totalPriceExclVatValue = totalPriceExclVatValue.add(saleLine
					.getLinePrice().getPriceExclVat());
			totalVatValue = totalVatValue.add(saleLine.getLinePrice()
					.getVatAmount());
		}

		if (discount != null) {
			totalPriceExclVatValue = totalPriceExclVatValue
					.multiply(BigDecimal.ONE.subtract(discount
							.divide(new BigDecimal(100))));
			totalVatValue = totalVatValue.multiply(BigDecimal.ONE
					.subtract(discount.divide(new BigDecimal(100))));
		}

	}

	public BigDecimal getNoRateVatBase() {

		return countPrice(getAccountingPeriod().getNoVat().getVatCode());
	}

	public BigDecimal getLowRateVatBase() {

		return countPrice(getAccountingPeriod().getLoVat().getVatCode());
	}

	public BigDecimal getHighRateVatBase() {
		return countPrice(getAccountingPeriod().getHiVat().getVatCode());
	}

	public BigDecimal getLowRateVatValue() {
		Vat loVat = getAccountingPeriod().getLoVat();
		return countPrice(loVat.getVatCode()).multiply(
				new BigDecimal(loVat.getKoef()));
	}

	public BigDecimal getHighRateVatValue() {
		Vat hiVat = getAccountingPeriod().getHiVat();
		return countPrice(hiVat.getVatCode()).multiply(
				new BigDecimal(hiVat.getKoef()));
	}

	protected BigDecimal countPrice(String vatCode) {
		BigDecimal result = BigDecimal.ZERO;
		for (SaleLine saleLine : this.getSaleLines()) {
			if (saleLine.getPrice().getVat().getVatCode().equals(vatCode))
				result = result.add(saleLine.getLinePrice().getPriceExclVat());
		}

		return result;
	}

	public SaleStateType[] getSalesStates() {
		return SaleStateType.values();
	}

	public SaleStateType getState() {
		return state;
	}

	public void setState(SaleStateType state) {
		this.state = state;
	}

	@Transient
	public BigDecimal getTotalPriceInclVatValue() {
		return totalPriceInclVatValue;
	}

	public BigDecimal getTotalPriceExclVatValue() {
		return totalPriceExclVatValue;
	}

	public void setTotalPriceExclVatValue(BigDecimal totalPriceExcVatValue) {
		this.totalPriceExclVatValue = totalPriceExcVatValue;
	}

	@Transient
	public PaymentType[] getPaymentTypes() {
		return PaymentType.values();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<SaleLine> getSaleLines() {
		return saleLines;
	}

	public void setSaleLines(Collection<SaleLine> saleLines) {
		this.saleLines = saleLines;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getTotalVatValue() {
		return totalVatValue;
	}

	public void setTotalVatValue(BigDecimal totalVatValue) {
		this.totalVatValue = totalVatValue;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		Sale sale = (Sale) obj;
		return sale.getId().equals(this.getId());
	}

	public int hashCode() {
		if (id == null)
			return -1;
		return id.hashCode();
	}

	public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

}
