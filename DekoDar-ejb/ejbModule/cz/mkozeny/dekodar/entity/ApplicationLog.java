package cz.mkozeny.dekodar.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.Length;

@Entity
public class ApplicationLog {

	@Id
	@GeneratedValue
	private Long id;

	public enum Severity {
		DEBUG, INFO, WARNING, ERROR
	}

	@Enumerated(EnumType.STRING)
	private Severity severity;

	@Length(max = 35)
	private String username;

	private Date dateTime;

	@Length(max = 150)
	private String message;

	@Length(max = 35)
	private Long productId;

	@Length(max = 60)
	private Long saleId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ApplicationLog.Severity[] getSeverities() {
		return Severity.values();
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

}
