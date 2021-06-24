package au.com.highlowgame.report;

import java.util.LinkedList;
import java.util.List;

import au.com.highlowgame.util.AppUtil;

public enum GraphType {

	BAR_GRAPH_1("report_bar_graph_1", "report_bar_graph_1_description"),
	BAR_GRAPH_2("report_bar_graph_2", "report_bar_graph_2_description"),
	THREED_BAR_GRAPH_1("report_3D_bar_graph_1", "report_3D_bar_graph_1_description"),
	THREED_BAR_GRAPH_2("report_3D_bar_graph_2", "report_3D_bar_graph_2_description"),
	LINE_GRAPH("report_line_graph", "report_line_graph_description"),
	PIE_CHART("report_pie_chart", "report_pie_chart_description"),
	THREED_PIE_CHART("report_3D_pie_chart", "report_3D_pie_chart_description");

	public final String name;
	public final String description;

	GraphType(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getTranslatedName() {
		return AppUtil.translate(name);
	}

	public String getTranslatedDescription() {
		return AppUtil.translate(description);
	}

	public static List<GraphType> getAll() {
		return java.util.Arrays.asList(GraphType.class.getEnumConstants());
	}

	public static List<GraphType> getAllAllowed(Report report) {
		LinkedList<GraphType> list = new LinkedList<>();
		list.addAll(getAll());
		list.removeAll(report.getDisallowedGraphTypes());
		return list;
	}

	public String getEnumConstant() {
		return name();
	}

}
