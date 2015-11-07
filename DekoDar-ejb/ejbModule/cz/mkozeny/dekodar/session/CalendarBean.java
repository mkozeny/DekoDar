package cz.mkozeny.dekodar.session;

import java.util.Locale;

import org.jboss.seam.annotations.Name;

@Name("calendarBean")
public class CalendarBean {

	private Locale locale;
	private String datePattern;
	private boolean popup;
	private boolean applyButton;

	public CalendarBean() {
		locale = new Locale("cs", "CZ");
		datePattern = "dd.MM.yyyy";
		popup = true;
		applyButton = true;

	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getDatePattern() {
		return "dd.MM.yyyy HH:mm";
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public boolean isPopup() {
		return popup;
	}

	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	public boolean isApplyButton() {
		return applyButton;
	}

	public void setApplyButton(boolean applyButton) {
		this.applyButton = applyButton;
	}

}
