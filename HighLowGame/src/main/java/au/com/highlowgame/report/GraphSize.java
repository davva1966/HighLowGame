package au.com.highlowgame.report;

import java.util.List;

import au.com.highlowgame.util.AppUtil;

public enum GraphSize {

	SMALL("report_graph_size_small", 195, 195),
	MEDIUM("report_graph_size_medium", 400, 300),
	LARGE("report_graph_size_large", 550, 380);

	public final String name;
	public final int heightPortrait;
	public final int heightLandscape;

	GraphSize(String name, int heightPortrait, int heightLandscape) {
		this.name = name;
		this.heightPortrait = heightPortrait;
		this.heightLandscape = heightLandscape;
	}

	public String getTranslatedName() {
		return AppUtil.translate(name);
	}

	public int getHeight(ReportPageOrientation orientation) {
		switch (orientation) {
		case PORTRAIT:
			return heightPortrait;
		case LANDSCAPE:
			return heightLandscape;
		default:
			return heightLandscape;
		}
	}

	public static List<GraphSize> getAll() {
		return java.util.Arrays.asList(GraphSize.class.getEnumConstants());

	}

}
