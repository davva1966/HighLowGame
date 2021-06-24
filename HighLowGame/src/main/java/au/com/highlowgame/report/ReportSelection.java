package au.com.highlowgame.report;

import java.util.List;

import au.com.speedinvoice.model.Preference;
import flexjson.JSONDeserializer;

public interface ReportSelection {

	public Report getReport();

	public GraphSize getGraphSize();

	public List<GraphType> getGraphTypes();

	public ReportPageSize getReportPageSize();

	public ReportPageOrientation getReportPageOrientation();

	public boolean getShowValuesInGraphs();

	public boolean printSelections();

	public List<String> getSelectionStrings();

	public void saveToPreferences();

	public String toJson();

	@SuppressWarnings("rawtypes")
	public static ReportSelection fromPreferences(Report report, Class clazz) {
		try {
			Preference preference = Preference.findPreference(report.getPreferenceKey());
			if (preference == null)
				return null;

			return ReportSelection.fromJson(preference.getPreferenceValue(), clazz);
		} catch (Exception e) {
		}

		return null;
	}

	public static ReportSelection fromJson(String json) {
		return fromJson(json, null);
	}

	@SuppressWarnings("rawtypes")
	public static ReportSelection fromJson(String json, Class clazz) {
		ReportSelection reportSelection = null;
		if (clazz == null)
			reportSelection = new JSONDeserializer<ReportSelection>().deserialize(json);
		else
			reportSelection = new JSONDeserializer<ReportSelection>().use(null, clazz).deserialize(json);
		return reportSelection;
	}

}
