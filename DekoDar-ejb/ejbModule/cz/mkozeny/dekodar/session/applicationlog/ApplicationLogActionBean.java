package cz.mkozeny.dekodar.session.applicationlog;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import cz.mkozeny.dekodar.entity.ApplicationLog;

@Stateless
@Name("applicationLogAction")
@AutoCreate
public class ApplicationLogActionBean implements ApplicationLogAction{
	
	@PersistenceContext
	EntityManager em;
	
	@In Credentials credentials;
	
	public void createLog(ApplicationLog.Severity severity, String message) {
		ApplicationLog appLog = new ApplicationLog();
		appLog.setDateTime(new Date());
		appLog.setMessage(message);
		appLog.setUsername(credentials.getUsername());
		em.persist(appLog);
	}
	
	public void createLogForProduct(ApplicationLog.Severity severity, String message, 
			Long productId) {
		ApplicationLog appLog = new ApplicationLog();
		appLog.setDateTime(new Date());
		appLog.setMessage(message);
		//appLog.setProductId(productId);
		appLog.setSeverity(severity);
		appLog.setUsername(credentials.getUsername());
		em.persist(appLog);
	}
	
	public void createLogForSale(ApplicationLog.Severity severity, String message, Long saleId)
	{
		ApplicationLog appLog = new ApplicationLog();
		appLog.setDateTime(new Date());
		appLog.setMessage(message);
		//appLog.setSaleId(saleId);		
		appLog.setSeverity(severity);
		appLog.setUsername(credentials.getUsername());
		em.persist(appLog);
	}

}
