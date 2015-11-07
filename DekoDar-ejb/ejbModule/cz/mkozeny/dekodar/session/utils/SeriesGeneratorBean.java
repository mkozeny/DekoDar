package cz.mkozeny.dekodar.session.utils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import cz.mkozeny.dekodar.entity.AccountingPeriod;

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

}
