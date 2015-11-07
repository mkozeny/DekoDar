package cz.mkozeny.dekodar.session.utils;

import javax.ejb.Local;

import cz.mkozeny.dekodar.entity.AccountingPeriod;

@Local
public interface SeriesGenerator {

	public AccountingPeriod getDefaultAccountingPeriod();
}