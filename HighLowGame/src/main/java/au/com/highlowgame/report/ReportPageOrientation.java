package au.com.highlowgame.report;

import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;
import net.sf.dynamicreports.report.constant.PageOrientation;

public enum ReportPageOrientation {

	PORTRAIT(PageOrientation.PORTRAIT, "report_page_orientation_portrait"),
	LANDSCAPE(PageOrientation.LANDSCAPE, "report_page_orientation_landscape");

	public final PageOrientation pageOrientation;
	public final String name;

	ReportPageOrientation(PageOrientation pageOrientation, String name) {
		this.pageOrientation = pageOrientation;
		this.name = name;
	}

	public PageOrientation getPageOrientation() {
		return pageOrientation;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}

	public static List<ReportPageOrientation> getAll() {
		return java.util.Arrays.asList(ReportPageOrientation.class.getEnumConstants());

	}

	public String getEnumConstant() {
		return name();
	}

}
