package cz.mkozeny.dekodar.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import cz.mkozeny.dekodar.Price;

@Entity
public class Product implements Serializable {

	@Id
	private String id;

	// SC010132
	@Length(max = 35)
	private String barCode;

	@Length(max = 35)
	private String model;

	@Length(max = 35)
	private String description;

	private Date createDate;

	private Double price = new Double(0.0);
	
	@ManyToOne
	private ProductCategory productCategory;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToOne
	private StockAvailAbility availAbility = new StockAvailAbility();

	public StockAvailAbility getAvailAbility() {
		return availAbility;
	}

	public void setAvailAbility(StockAvailAbility availAbility) {
		this.availAbility = availAbility;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		Product product = (Product) obj;
		return product.getId().equals(this.getId());
	}

	public int hashCode() {
		return getId().hashCode();
	}

	@Transient
	public Integer getTotalAvailAbility() {
		return availAbility.availAbility.intValue();
	}

}
