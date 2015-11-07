package cz.mkozeny.dekodar.session.sale;

import javax.ejb.Local;

@Local
public interface SalePdfReportAction {

	public abstract void doPdfReport() throws Exception;

}