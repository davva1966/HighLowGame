package au.com.highlowgame.report;

import java.util.ArrayList;
import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;

public enum Report {

	SALES("report_title_sales_report"),
	PAYMENT("report_title_payment_report"),
	CUSTOMER("report_title_customer_report"),
	ITEM("report_title_item_report"),
	ITEM_TYPE("report_title_item_type_report"),
	ITEM_PROFIT("report_title_item_profit_report"),
	VAT("report_title_vat_report");

	public static final String PREFERENCE_PREFIX = "ReportSelection_";

	public final String name;

	Report(String name) {
		this.name = name;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}

	public String getPreferenceKey() {
		return PREFERENCE_PREFIX + name();
	}

	public ReportSelection getSelectionBean() {
		switch (this) {
		case SALES:
			return new SalesReportSelectionBean(this);
		case PAYMENT:
			return new PaymentReportSelectionBean(this);
		case CUSTOMER:
			return new CustomerReportSelectionBean(this);
		case ITEM:
			return new ItemReportSelectionBean(this);
		case ITEM_TYPE:
			return new ItemTypeReportSelectionBean(this);
		case ITEM_PROFIT:
			return new ItemProfitReportSelectionBean(this);
		case VAT:
			return new VatReportSelectionBean(this);
		default:
			return new SalesReportSelectionBean(this);
		}
	}

	public List<GraphType> getDisallowedGraphTypes() {
		List<GraphType> disallowedGraphTypes = new ArrayList<GraphType>();
		switch (this) {
		case PAYMENT:
			disallowedGraphTypes.add(GraphType.BAR_GRAPH_1);
			disallowedGraphTypes.add(GraphType.BAR_GRAPH_2);
			disallowedGraphTypes.add(GraphType.LINE_GRAPH);
			disallowedGraphTypes.add(GraphType.PIE_CHART);
			disallowedGraphTypes.add(GraphType.THREED_BAR_GRAPH_1);
			disallowedGraphTypes.add(GraphType.THREED_BAR_GRAPH_2);
			disallowedGraphTypes.add(GraphType.THREED_PIE_CHART);
			break;
		case CUSTOMER:
			disallowedGraphTypes.add(GraphType.LINE_GRAPH);
			break;
		case ITEM:
			disallowedGraphTypes.add(GraphType.LINE_GRAPH);
			break;
		case ITEM_TYPE:
			disallowedGraphTypes.add(GraphType.LINE_GRAPH);
			break;
		case ITEM_PROFIT:
			disallowedGraphTypes.add(GraphType.LINE_GRAPH);
			break;
		default:
			break;
		}

		return disallowedGraphTypes;
	}

	public static List<Report> getAllReports() {
		return java.util.Arrays.asList(Report.class.getEnumConstants());

	}

	public String getEnumConstant() {
		return name();
	}

}
