package cz.mkozeny.dekodar.session.sale;

import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(rollback = true)
public class StockAvailAbilityOperationException extends Exception {

	public StockAvailAbilityOperationException(String message) {
		super(message);
	}

}
