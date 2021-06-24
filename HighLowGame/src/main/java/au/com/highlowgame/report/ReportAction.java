package au.com.highlowgame.report;

import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;

public enum ReportAction {

	DOWNLOAD_PDF("report_action_download_as_pdf"),
	DOWNLOAD_IMAGE("report_action_download_as_image"),
	DOWNLOAD_EXCEL("report_action_download_as_excel"),
	DOWNLOAD_WORD("report_action_download_as_word"),
	DOWNLOAD_POWERPOINT("report_action_download_as_powerpoint"),
	DOWNLOAD_OPENOFFICE_SPREADSHEET("report_action_download_as_open_office_spreadsheet"),
	DOWNLOAD_OPENOFFICE_DOCUMENT("report_action_download_as_open_office_document"),
	DOWNLOAD_RICH_TEXT("report_action_download_as_rich_text_document"),
	DOWNLOAD_CSV("report_action_download_as_csv");

	public final String name;

	ReportAction(String name) {
		this.name = name;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}

	public static List<ReportAction> getAllActions() {
		return java.util.Arrays.asList(ReportAction.class.getEnumConstants());

	}

}
