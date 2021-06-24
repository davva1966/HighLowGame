package au.com.highlowgame.report;

import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalUnit;
import java.util.List;

import au.com.speedinvoice.util.SpeedInvoiceUtil;

public enum ReportPeriod {

	MONTHLY("report_period_monthly", ChronoUnit.MONTHS),
	QUARTERLY("report_period_quarterly", IsoFields.QUARTER_YEARS),
	YEARLY("report_period_yearly", ChronoUnit.YEARS);

	public final String name;
	TemporalUnit temporalUnit;

	ReportPeriod(String name, TemporalUnit temporalUnit) {
		this.name = name;
		this.temporalUnit = temporalUnit;
	}

	public String getTranslatedName() {
		return SpeedInvoiceUtil.translate(name);
	}
	
	public TemporalUnit getTemporalUnit() {
		return temporalUnit;
	}


	public static List<ReportPeriod> getAll() {
		return java.util.Arrays.asList(ReportPeriod.class.getEnumConstants());

	}

	public String getEnumConstant() {
		return name();
	}

}
