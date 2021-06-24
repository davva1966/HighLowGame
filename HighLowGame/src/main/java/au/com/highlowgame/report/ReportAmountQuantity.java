package au.com.highlowgame.report;

import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;

public enum ReportAmountQuantity {

	AMOUNT("report_amount"),
	QUANTITY("report_quantity"),
	BOTH("report_both");

	public final String name;

	ReportAmountQuantity(String name) {
		this.name = name;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}

	public static List<ReportAmountQuantity> getAll() {
		return java.util.Arrays.asList(ReportAmountQuantity.class.getEnumConstants());

	}

	public String getEnumConstant() {
		return name();
	}

}
