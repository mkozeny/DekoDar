package cz.mkozeny.dekodar.session.utils;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.AccountingPeriod;
import cz.mkozeny.dekodar.entity.IdSerie;

@Local
public interface SeriesGenerator {

	public AccountingPeriod getDefaultAccountingPeriod();
	
	public String getProductPk(AccountingPeriod aPeriod);
	
	public String getSalePk(AccountingPeriod aPeriod);
}