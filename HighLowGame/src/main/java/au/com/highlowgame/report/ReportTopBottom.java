package au.com.highlowgame.report;

import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;

public enum ReportTopBottom {

	TOP5("report_top5", 5, false),
	TOP10("report_top10", 10, false),
	TOP20("report_top20", 20, false),
	TOP50("report_top50", 50, false),
	TOP100("report_top100", 100, false),
	BOTTOM5("report_bottom5", 5, true),
	BOTTOM10("report_bottom10", 10, true),
	BOTTOM20("report_bottom20", 20, true),
	BOTTOM50("report_bottom50", 50, true),
	BOTTOM100("report_bottom100", 100, true);

	public final String name;
	public final int number;
	public final boolean isBottom;

	ReportTopBottom(String name, int number, boolean isBottom) {
		this.name = name;
		this.number = number;
		this.isBottom = isBottom;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}

	public static List<ReportTopBottom> getAll() {
		return java.util.Arrays.asList(ReportTopBottom.class.getEnumConstants());

	}

}
