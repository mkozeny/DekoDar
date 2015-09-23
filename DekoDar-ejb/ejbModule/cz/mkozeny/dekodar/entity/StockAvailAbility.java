package cz.mkozeny.dekodar.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.NotNull;

@Entity
public class StockAvailAbility {

	@Id
	@GeneratedValue
	Long id;

	@NotNull
	Long availAbility;

	Long minimumAvailAbility;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAvailAbility() {
		return availAbility;
	}

	public void setAvailAbility(Long availAbility) {
		this.availAbility = availAbility;
	}

	public Long getMinimumAvailAbility() {
		return minimumAvailAbility;
	}

	public void setMinimumAvailAbility(Long minimumAvailAbility) {
		this.minimumAvailAbility = minimumAvailAbility;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		StockAvailAbility stockAvailaAbility = (StockAvailAbility) obj;
		return stockAvailaAbility.getId().equals(this.getId());
	}

	public int hashCode() {

		return this.getId().hashCode();
	}

}
