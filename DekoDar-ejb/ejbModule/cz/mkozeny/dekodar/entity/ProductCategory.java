package cz.mkozeny.dekodar.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class ProductCategory {

	@Id
	@GeneratedValue
	private Long id;
	
	public enum Type {
		ROOT,
		CHILD;
	}
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Type type;
	
	@Length(max = 100)
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER)
	private Collection<ProductCategory> subcategories = new ArrayList<ProductCategory>();
	
//	@OneToMany
//	private Collection<Product> products = new ArrayList<Product>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<ProductCategory> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(Collection<ProductCategory> subcategories) {
		this.subcategories = subcategories;
	}

//	public Collection<Product> getProducts() {
//		return products;
//	}
//
//	public void setProducts(Collection<Product> products) {
//		this.products = products;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductCategory other = (ProductCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String toString() {
        return this.name;
    }
}
