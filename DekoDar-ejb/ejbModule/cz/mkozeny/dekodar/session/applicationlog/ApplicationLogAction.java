package cz.mkozeny.dekodar.session.applicationlog;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.ApplicationLog;

@Local
public interface ApplicationLogAction {
	
	public void createLog(ApplicationLog.Severity severity, String message);
	
	public void createLogForProduct(ApplicationLog.Severity severity, String message, 
			String productId);
	
	public void createLogForSale(ApplicationLog.Severity severity, String message, String saleId);

}
