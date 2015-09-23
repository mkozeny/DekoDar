package cz.mkozeny.dekodar.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.Length;

@Entity
public class Role {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3883433317490555069L;
	@Id
	@Length(max=35)
	String name;
	
	@Length(max=65)
	String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@Override
	public boolean equals(Object other) {
	     if ( (this == other ) ) return true;
	     if ( (other == null ) ) return false;
		 if ( !(other instanceof Role) ) return false;
		 Role castOther = ( Role ) other;
		 return (this.getName().equals(castOther.getName()));
 }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());		
		return result;
	}
	
	
}
