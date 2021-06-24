package au.com.highlowgame.report;

import java.util.List;

import net.sf.dynamicreports.report.constant.PageType;

public enum ReportPageSize {

	LETTER(PageType.LETTER),
	NOTE(PageType.NOTE),
	LEGAL(PageType.LEGAL),
	A0(PageType.A0),
	A1(PageType.A1),
	A2(PageType.A2),
	A3(PageType.A3),
	A4(PageType.A4),
	A5(PageType.A5);

	public final PageType pageType;

	ReportPageSize(PageType pageType) {
		this.pageType = pageType;
	}

	public PageType getPageType() {
		return pageType;
	}

	public static List<ReportPageSize> getAll() {
		return java.util.Arrays.asList(ReportPageSize.class.getEnumConstants());

	}
	
	public String getEnumConstant() {
		return name();
	}

}
