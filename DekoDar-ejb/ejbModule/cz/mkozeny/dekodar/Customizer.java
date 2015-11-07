package cz.mkozeny.dekodar;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@Name("customizer")
@AutoCreate
public class Customizer {

	// bude dobre dat pak do nejakeho res
	public static String pricePattern = "###,###,##0.00;'-'###,###,##0.00";
	// public static String pricePattern = "###,###,###.##";
	public static String dateTimePattern = "dd.MM.yyyy HH:mm:ss";
	public static String datePattern = "dd.MM.yyyy";
	public static String dbfDatePattern = "yyyyMMdd";
	public static Integer pageSize = 25;
	private static final String CUSTOMER_ORDER_STOCK_ID = "80";
	private static final String MAIN_STOCK_ID = "00";
	private static final String UPSELL_ORDER_STOCK_ID = "77";
	private static final String UPSELL_ORDER_CREDIT_NOTE_STOCK_ID = "78";

	// private static final String RAJTORA_MAIL = "martin.rajtora@lavamax.cz";
	// private static final String JANOVSKY_MAIL =
	// "jaroslav.janovsky@lavamax.cz";
	// private static final String AMPLE_MAIL = "support@ample.cz";

	public String getPricePattern() {
		return pricePattern;
	}

	public String getDateTimePattern() {
		return dateTimePattern;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public String getDbfDatePattern() {
		return dbfDatePattern;
	}

	public static String getCustomerOrderStockId() {
		return CUSTOMER_ORDER_STOCK_ID;
	}

	public static String getMainStockId() {
		return MAIN_STOCK_ID;
	}

	public static String getUSStockId() {
		return UPSELL_ORDER_STOCK_ID;
	}

	public static String getUSCreditNoteStockId() {
		return UPSELL_ORDER_CREDIT_NOTE_STOCK_ID;
	}

}
