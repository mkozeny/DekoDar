package cz.mkozeny.dekodar.session.sale;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.*;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.jsf.ListDataModel;
import org.jboss.seam.log.Log;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import cz.mkozeny.dekodar.Customizer;
import cz.mkozeny.dekodar.entity.ApplicationLog.Severity;
import cz.mkozeny.dekodar.entity.Sale;
import cz.mkozeny.dekodar.entity.SaleLine;
import cz.mkozeny.dekodar.entity.User;
import cz.mkozeny.dekodar.session.applicationlog.ApplicationLogAction;

@Stateless
@Name("saleReportAction")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SalePdfReportActionBean extends PdfPageEventHelper implements
		SalePdfReportAction {

	@Logger
	private Log log;

	ResourceBundle res = ResourceBundle.getBundle("cz.mkozeny.dekodar.Res");

	@In(required = false)
	Sale sale;

	@In(required = false)
	ListDataModel sales;

	@In
	User signedUser;

	@PersistenceContext
	private EntityManager entityManager;

	@In
	Customizer customizer;

	@In
	ApplicationLogAction applicationLogAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.mkozeny.dekodar.session.sales.SalePdfReportAction#doPdfReport()
	 */
	public void doPdfReport() throws Exception {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		HttpServletResponse response = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		OutputStream out = response.getOutputStream();
		String name = "pokladni_doklad_" + sale.getId();
		
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\""
						+ encodeFileName(name, request,
								response.getCharacterEncoding()) + "\";");
		createPdf(out);

		out.flush();
		response.getOutputStream().close();
		response.setStatus(HttpServletResponse.SC_OK);
		facesContext.responseComplete();

	}

	@SuppressWarnings("unchecked")
	private void createPdf(OutputStream out) throws ClassNotFoundException,
			DocumentException, FileNotFoundException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, out);
		writer.setPageEvent(new SalePdfReportActionBean());

		try {
			BaseFont roman = BaseFont.createFont(BaseFont.TIMES_ROMAN,
					BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			Font normal = new Font(roman, 9, Font.NORMAL);
			Font small = new Font(roman, 7, Font.NORMAL);
			Font bold = new Font(roman, 9, Font.BOLD);
			Font header = new Font(roman, 14, Font.BOLD);

			document.addTitle("Pokladní doklad");
			document.addAuthor("Dekodar");
			document.setMargins(45, 45, 45, 120);
			document.setPageSize(PageSize.A4);
			document.open();

			ArrayList<Sale> saleForRender = new ArrayList<Sale>();
			if (sale != null) {

				saleForRender.add(sale);
			} else {
				saleForRender = (ArrayList<Sale>) sales.getWrappedData();
			}

			for (Sale s : saleForRender) {
				s = entityManager.find(Sale.class, s.getId());

				document.newPage();
				PdfPTable table = new PdfPTable(2);
				table.setWidthPercentage(100);
				float[] widths = { 70, 30 };
				table.setWidths(widths);
				table.setSpacingAfter(20);
				
				PdfPCell cell = new PdfPCell(new Paragraph("POKLADNÍ DOKLAD Č.: " + sale.getId(), bold));
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setBorderWidth(0);

				table.addCell(cell);

				PdfContentByte cb = writer.getDirectContent();
				Barcode128 code128 = new Barcode128();
				code128.setCode(s.getId());
				Image image128 = code128.createImageWithBarcode(cb, null, null);

				cell = new PdfPCell(new Phrase(new Chunk(image128, 0, 0)));
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setBorderWidth(0);

				table.addCell(cell);

				document.add(table);

				table = new PdfPTable(3);
				table.setWidthPercentage(100);
				float[] newWidths = { 50, 25, 25 };
				table.setSpacingAfter(20);
				table.setWidths(newWidths);

				if (s.getNote() != null && s.getNote().length() > 0)
					cell = new PdfPCell(new Paragraph(s.getNote(), normal));

				else
					cell = new PdfPCell();

				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("Datum vystavení:", normal));
				cell.setBorderWidthRight(0);
				cell.setBorderWidthBottom(0);
				cell.setPaddingLeft(20.0f);
				cell.setPaddingTop(25.0f);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(sale.getDateCreated() != null?
						getDateFormat().format(sale.getDateCreated()):"", normal));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthBottom(0);
				cell.setPaddingTop(25.0f);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);

				table.addCell(cell);
				
				cell = new PdfPCell();
				cell.setBorderWidth(0f);
				
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("DUZP:", normal));
				cell.setBorderWidthRight(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingLeft(20.0f);
				cell.setPaddingBottom(25.0f);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(sale.getVatDate() != null?
						getDateFormat().format(sale.getVatDate()):
							(sale.getDateCreated()!= null?
							getDateFormat().format(sale.getDateCreated()):""), normal));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingBottom(25.0f);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);

				table.addCell(cell);

				document.add(table);

				table = new PdfPTable(9);
				table.setWidthPercentage(100);
				// 2,3,2,2
				float[] newerWidths = { 14, 15, 8, 9, 8, 9, 10, 11, 11 };
				table.setSpacingAfter(20);
				table.setWidths(newerWidths);

				table.addCell(new PdfPCell(new Paragraph("Kód zboží", normal)));

				table.addCell(new PdfPCell(new Paragraph("Popis", normal)));

				table.addCell(new PdfPCell(new Paragraph("Dodané množství", normal)));

				table.addCell(new PdfPCell(new Paragraph("Cena/jedn.\r\n bez DPH", normal)));

				table.addCell(new PdfPCell(new Paragraph("Sleva %\r\n zákazn.", normal)));

				table.addCell(new PdfPCell(new Paragraph("Cena/jedn.\r\n po slevě", normal)));

				table.addCell(new PdfPCell(new Paragraph("Sazba DPH", normal)));
				
				table.addCell(new PdfPCell(new Paragraph("Cena celkem\r\n bez DPH", normal)));

				table.addCell(new PdfPCell(new Paragraph("Cena celkem\r\n s DPH", normal)));
				
				for (SaleLine line : s.getSaleLines()) {
					cell = new PdfPCell(new Paragraph(line.getProduct().getId()!=null?
							line.getProduct().getId().toString():"", normal));
					cell.setBorderWidth(0);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph((line.getDescription() != null
							&& line.getDescription().trim().length() > 0)?
							line.getDescription():
								((line.getProduct() != null
										&& line.getProduct().getModel() != null)?
										line.getProduct().getModel():""), normal));
					cell.setBorderWidth(0);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getQuantity().toString(), normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getPrice()!=null?
							getPriceFormat().format(line.getPrice().getAmount()):"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getDiscount()!=null?
							line.getDiscount().toString():"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getLinePrice()!=null?
							getPriceFormat().format(line.getDiscount()!=null?
									line.getPrice().getAmount().multiply(
									BigDecimal.ONE.subtract(line.getDiscount()
											.divide(new BigDecimal(100)))):
												line.getPrice().getAmount()):"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getPrice()!=null?
							line.getPrice().getVat().getVatCode() + "%":"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);
					
					cell = new PdfPCell(new Paragraph(line.getLinePrice()!=null?
							getPriceFormat().format(
							line.getLinePrice().getPriceExclVat()):"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(line.getLinePrice()!=null?
							getPriceFormat().format(
							line.getLinePrice().getPriceInclVat()):"", normal));
					cell.setBorderWidth(0);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

					table.addCell(cell);
				}

				document.add(table);

				table = new PdfPTable(3);
				table.setWidthPercentage(100);
				float[] newestWidths = { 45, 43, 12 };
				table.setSpacingAfter(20);
				table.setWidths(newestWidths);

				// celkova sleva

				cell = new PdfPCell();
				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("S L E V A  C E L K E M", bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthBottom(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(getPriceFormat().format(
						sale.getTotalDiscountExclVatValue().negate()), bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthBottom(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell);

				// konec celkove slevy

				cell = new PdfPCell();
				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("C E L K E M  bez  D P H", bold));
				cell.setBorderWidth(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(getPriceFormat().format(sale.getTotalPriceExclVatValue()), bold));
				cell.setBorderWidth(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("D P H", bold));
				cell.setBorderWidth(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(getPriceFormat().format(sale.getTotalVatValue()), bold));
				cell.setBorderWidth(0);
				cell.setPaddingTop(10);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell);

				// halerove zaokrouhleni

				cell = new PdfPCell();
				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("Haléřové zaokrouhlení", bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(10);
				cell.setPaddingBottom(10);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);

				BigDecimal roundedPrice = new BigDecimal(Math.round(sale
						.getTotalPriceInclVatValue().doubleValue()));
				cell = new PdfPCell(new Paragraph(getPriceFormat().format(
						roundedPrice.subtract(sale.getTotalPriceInclVatValue())), bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(10);
				cell.setPaddingBottom(10);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell);

				// halerove zaokrouhleni konec

				cell = new PdfPCell();
				cell.setBorderWidth(0);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("Č Á S T K A  K  P L A C E N Í", bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthTop(0);
				cell.setPaddingTop(10);
				cell.setPaddingBottom(10);
				cell.setBorderWidthBottom(2);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(getPriceFormat().format(
						new BigDecimal(Math.round(sale
								.getTotalPriceInclVatValue().doubleValue()))), bold));
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setBorderWidthTop(0);
				cell.setBorderWidthBottom(2);
				cell.setPaddingTop(10);
				cell.setPaddingBottom(10);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell);

				document.add(table);

				applicationLogAction.createLogForSale(Severity.INFO,
						"Vygenerován PDF report pokladního dokladu", s.getId());

			}
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}

		document.close();
	}
	
	private DecimalFormat getPriceFormat() {
		return new DecimalFormat(customizer.getPricePattern());
	}

	private Format getDateFormat() {
		return new SimpleDateFormat(customizer.getDatePattern());
	}

	private Format getDateTimeFormat() {
		return new SimpleDateFormat(customizer.getDateTimePattern());
	}

	public void onEndPage(PdfWriter writer, Document document) {
		try {
			BaseFont roman = BaseFont.createFont(BaseFont.TIMES_ROMAN,
					BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			Font normal = new Font(roman, 9, Font.NORMAL);
			Font small = new Font(roman, 7, Font.NORMAL);
			Font bold = new Font(roman, 9, Font.BOLD);
			Font header = new Font(roman, 14, Font.BOLD);

			Rectangle page = document.getPageSize();
			PdfPTable foot = new PdfPTable(4);

			List<Paragraph> paragraphs = new ArrayList<Paragraph>();
			
			PdfPCell cell = new PdfPCell();
			cell.setColspan(4);
			cell.setBorderWidthRight(0);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthTop(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			foot.addCell(cell);

			String s = res.getString("dekodar.companyName");
			paragraphs.add(new Paragraph(s, normal));
			s = res.getString("dekodar.street");
			paragraphs.add(new Paragraph(s, normal));
			s = res.getString("dekodar.zipCode") + " "
					+ res.getString("dekodar.city");
			paragraphs.add(new Paragraph(s, normal));

			cell = new PdfPCell();
			cell.setBorderWidth(0);
			for (Paragraph paragraph : paragraphs)
				cell.addElement(paragraph);

			foot.addCell(cell);

			paragraphs = new ArrayList<Paragraph>();
			s = "Bankovní spojení:";
			paragraphs.add(new Paragraph(s, normal));
			s = "Č. účtu: " + res.getString("dekodar.account");
			paragraphs.add(new Paragraph(s, normal));
			s = res.getString("dekodar.bank");
			paragraphs.add(new Paragraph(s, normal));

			cell = new PdfPCell();
			cell.setBorderWidth(0);
			for (Paragraph paragraph : paragraphs)
				cell.addElement(paragraph);

			foot.addCell(cell);

			paragraphs = new ArrayList<Paragraph>();
			s = "Tel: " + res.getString("dekodar.phone");
			paragraphs.add(new Paragraph(s, normal));
			s = "Fax: " + res.getString("dekodar.fax");
			paragraphs.add(new Paragraph(s, normal));
			s = "Email: " + res.getString("dekodar.email");
			paragraphs.add(new Paragraph(s, normal));

			cell = new PdfPCell();
			cell.setBorderWidth(0);
			for (Paragraph paragraph : paragraphs)
				cell.addElement(paragraph);

			foot.addCell(cell);

			paragraphs = new ArrayList<Paragraph>();
			s = "IČ: " + res.getString("dekodar.taxNr");
			paragraphs.add(new Paragraph(s, normal));
			s = "DIČ: " + res.getString("dekodar.vatNr");
			paragraphs.add(new Paragraph(s, normal));

			cell = new PdfPCell();
			cell.setBorderWidth(0);
			for (Paragraph paragraph : paragraphs)
				cell.addElement(paragraph);

			foot.addCell(cell);

			foot.setTotalWidth(page.getWidth() - document.leftMargin()
					- document.rightMargin());
			foot.writeSelectedRows(0, -1, document.leftMargin(),
					document.bottomMargin(), writer.getDirectContent());

		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	private static String encodeFileName(String fileName,
			HttpServletRequest request, String responseEncoding)
			throws UnsupportedEncodingException {
		// TODO dodelat rozpoznani MSIE
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			System.out.println(headerName + " : "
					+ request.getHeader(headerName));
		}

		return new String(fileName.getBytes("Windows-1250"), responseEncoding);

	}

}
