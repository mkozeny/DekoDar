package cz.mkozeny.dekodar.session.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.entity.AccountingPeriod;
import cz.mkozeny.dekodar.entity.IdSerie;

@Stateless
@Name("seriesGenerator")
@AutoCreate
public class SeriesGeneratorBean implements SeriesGenerator {

	@Logger
	private Log log;

	@In
	FacesMessages facesMessages;

	@PersistenceContext
	private EntityManager em;

	public AccountingPeriod getDefaultAccountingPeriod() {
		AccountingPeriod result = (AccountingPeriod) em
				.createQuery(
						"select p from AccountingPeriod p order by p.id desc")
				.setMaxResults(1).getSingleResult();
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getProductPk(AccountingPeriod aPeriod) {
		return getPk(IdSerie.Type.PRODUCT, aPeriod);
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getSalePk(AccountingPeriod aPeriod) {
		return getPk(IdSerie.Type.SALE, aPeriod);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private String getPk(IdSerie.Type type, AccountingPeriod aPeriod) {
		NumberFormat formatter = new DecimalFormat("00000");

		IdSerie idSerie = getLastKnownId(type, aPeriod);

		if (idSerie == null) {
			idSerie = new IdSerie();
			idSerie.setType(type);
			idSerie.setLastValueStr(type.getKeyName()
					+ aPeriod.getIdSeriePrefix() 
					+ formatter.format(0));
			idSerie.setAccountingPeriod(aPeriod);
			em.persist(idSerie);
		}

		String lastId = idSerie.getLastValueStr();
		Long serviceOrderId = Long.parseLong(lastId.substring(3,
				lastId.length())) + 1;

		String newId = type.getKeyName()
				+ aPeriod.getIdSeriePrefix() 
				+ formatter.format(serviceOrderId);
		idSerie.setLastValueStr(newId);
		em.merge(idSerie);
		em.flush();
		return newId;
	}

	private IdSerie getLastKnownId(IdSerie.Type type, AccountingPeriod aPeriod) {
		IdSerie idSerie = null;
		try {
			idSerie = (IdSerie) em
					.createQuery(
							"select i from IdSerie i where i.type='" + type
									+ "' and i.accountingPeriod.id = "
									+ aPeriod.getId()).setMaxResults(1)
					.getSingleResult();

		} catch (Exception e) {
			// nothing to do
		}
		return idSerie;

	}

}
