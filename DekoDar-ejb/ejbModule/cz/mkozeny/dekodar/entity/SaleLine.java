package cz.mkozeny.dekodar.entity;

import java.math.BigDecimal;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

import cz.mkozeny.dekodar.Price;

@Entity
public class SaleLine {

	@Id
	@GeneratedValue
	Long id;

	Integer quantity;

	Integer lineNumber;

	// Sleva na urovni radku dokladu
	BigDecimal discount;

	@Length(max = 60)
	String description;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "price")) })
	@AssociationOverrides({ @AssociationOverride(name = "vat", joinColumns = @JoinColumn(name = "price_Vat_code")) })
	Price price;

	@ManyToOne
	Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Price getLinePrice() { // pocitat slevu

		Price result = new Price(price.getAmount().multiply(
				new BigDecimal(quantity)), price.getVat());

		if (discount != null)
			return new Price(price
					.getAmount()
					.multiply(new BigDecimal(quantity))
					.multiply(
							BigDecimal.ONE.subtract(discount
									.divide(new BigDecimal(100)))),
					price.getVat());

		return result;
	}

	public BigDecimal getLineVatValue() { // pocitat slevu

		return getLinePrice().getPriceInclVat().subtract(
				getLinePrice().getPriceExclVat());
	}

	public Price getLineDiscountValue() { // pocitat slevu

		if (discount != null)
			return new Price(price.getAmount()
					.multiply(discount.divide(new BigDecimal(100)))
					.multiply(new BigDecimal(quantity)), price.getVat());
		else
			return new Price(BigDecimal.ZERO, price.getVat());
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		SaleLine saleLine = (SaleLine) obj;
		return saleLine.getId().equals(this.getId());
	}

	public int hashCode() {
		if (id == null)
			return -1;
		return id.hashCode();
	}

}
