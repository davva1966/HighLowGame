package au.com.highlowgame.service;

import static net.sf.dynamicreports.report.builder.DynamicReports.asc;
import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.report.BarChartCustomizer;
import au.com.highlowgame.report.ColorUtils;
import au.com.highlowgame.report.GraphType;
import au.com.highlowgame.report.LineChartCustomizer;
import au.com.highlowgame.report.ReportSelection;
import au.com.highlowgame.user.UserContextService;
import au.com.highlowgame.util.SSUtil;
import au.com.speedinvoice.SpeedInvoiceCodeException;
import au.com.speedinvoice.model.GlobalSetting;
import au.com.speedinvoice.model.Invoice;
import au.com.speedinvoice.model.security.Tenant;
import au.com.speedinvoice.report.CurrencyValueFormatter;
import au.com.speedinvoice.util.FontHelper;
import au.com.speedinvoice.util.LocaleHelper;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.chart.AbstractBaseChartBuilder;
import net.sf.dynamicreports.report.builder.chart.AbstractCategoryChartBuilder;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.chart.LineChartBuilder;
import net.sf.dynamicreports.report.builder.chart.Pie3DChartBuilder;
import net.sf.dynamicreports.report.builder.chart.PieChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.expression.DRIValueFormatter;
import net.sf.jasperreports.engine.JRDataSource;

public class DynamicReportsReportService implements ReportService {

	protected static Color[] graphColors = ColorUtils.generateVisuallyDistinctColors(25, .6f, .1f, 1);

	@Autowired
	TranslationService translationService;

	private Logger logger = Logger.getLogger(this.getClass());

	protected Map<String, StyleBuilder> styleMap = null;
	protected ReportTemplateBuilder template = null;

	@Override
	public JasperReportBuilder getGameReportBuilder(Game game, Connection con) throws Exception {

		JasperReportBuilder reportBuilder = report();
		reportBuilder.setDataSource(getGameReportDataSource(game));

		String periodColumnTitle = getPeriodColumnTitle(reportSelection);

		TextColumnBuilder<String> periodColumn = col.column(periodColumnTitle, "period", type.stringType());
		periodColumn.setTitleStyle(getStyle("columnTitleLeftStyle"));
		periodColumn.setStyle(getStyle("detailLeftStyle"));

		String amountColumnTitle = translationService.translate("report_invoiced_amount");
		TextColumnBuilder<BigDecimal> amountColumn = col.column(amountColumnTitle, "amount", type.bigDecimalType());
		amountColumn.setTitleStyle(getStyle("columnTitleRightStyle"));
		amountColumn.setStyle(getStyle("detailRightStyle"));
		amountColumn.setValueFormatter(new CurrencyValueFormatter(!reportSelection.getFormatAmounts()));

		String previousYearColumnTitle = translationService.translate("report_previous_year");

		TextColumnBuilder<BigDecimal> amountPreviousYearColumn = col.column(previousYearColumnTitle, "amountPreviousYear", type.bigDecimalType());
		amountPreviousYearColumn.setTitleStyle(getStyle("columnTitleRightStyle"));
		amountPreviousYearColumn.setStyle(getStyle("detailRightStyle"));
		amountPreviousYearColumn.setValueFormatter(new CurrencyValueFormatter(!reportSelection.getFormatAmounts()));

		reportBuilder.setTemplate(getTemplate());
		reportBuilder.setPageFormat(reportSelection.getReportPageSize().getPageType(), reportSelection.getReportPageOrientation().getPageOrientation());
		reportBuilder.columns(periodColumn, amountColumn);

		DRIValueFormatter<String, BigDecimal> formatter = new CurrencyValueFormatter(!reportSelection.getFormatAmounts());

		if (reportSelection.getCompareWithPreviousYear()) {
			reportBuilder.addColumn(amountPreviousYearColumn);
			reportBuilder.subtotalsAtSummary(sbt.text(translationService.translate("report_total"), periodColumn), sbt.sum(amountColumn).setValueFormatter(formatter), sbt.sum(amountPreviousYearColumn).setValueFormatter(formatter));
		} else {
			reportBuilder.subtotalsAtSummary(sbt.text(translationService.translate("report_total"), periodColumn), sbt.sum(amountColumn).setValueFormatter(formatter));
		}

		reportBuilder.sortBy(asc(periodColumn));

		reportBuilder.title(createTitleComponent(reportSelection, reportSelection.printSelections()));
		reportBuilder.pageFooter(cmp.pageXofY());

		if (reportSelection.getGraphTypes().contains(GraphType.BAR_GRAPH_1)) {
			BarChartBuilder chart = cht.barChart().setCategory(periodColumn).setShowValues(reportSelection.getShowValuesInGraphs()).addSerie(cht.serie(amountColumn));
			if (reportSelection.getCompareWithPreviousYear()) {
				chart.addSerie(cht.serie(amountPreviousYearColumn));
				chart.seriesOrderBy(amountColumnTitle, previousYearColumnTitle);
			}
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.BAR_GRAPH_2)) {
			BarChartBuilder chart = cht.barChart().setCategory(periodColumn).setShowValues(reportSelection.getShowValuesInGraphs()).setUseSeriesAsCategory(true).addSerie(cht.serie(amountColumn));
			if (reportSelection.getCompareWithPreviousYear()) {
				chart.addSerie(cht.serie(amountPreviousYearColumn));
				chart.seriesOrderBy(amountColumnTitle, previousYearColumnTitle);
			}
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.THREED_BAR_GRAPH_1)) {
			Bar3DChartBuilder chart = cht.bar3DChart().setCategory(periodColumn).setShowValues(reportSelection.getShowValuesInGraphs()).addSerie(cht.serie(amountColumn));
			if (reportSelection.getCompareWithPreviousYear()) {
				chart.addSerie(cht.serie(amountPreviousYearColumn));
				chart.seriesOrderBy(amountColumnTitle, previousYearColumnTitle);
			}
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.THREED_BAR_GRAPH_2)) {
			Bar3DChartBuilder chart = cht.bar3DChart().setCategory(periodColumn).setShowValues(reportSelection.getShowValuesInGraphs()).setUseSeriesAsCategory(true).addSerie(cht.serie(amountColumn));
			if (reportSelection.getCompareWithPreviousYear()) {
				chart.addSerie(cht.serie(amountPreviousYearColumn));
				chart.seriesOrderBy(amountColumnTitle, previousYearColumnTitle);
			}
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.LINE_GRAPH)) {
			LineChartBuilder chart = cht.lineChart().setCategory(periodColumn);
			if (reportSelection.getCompareWithPreviousYear()) {
				chart.addSerie(cht.serie(amountPreviousYearColumn));
				chart.seriesOrderBy(amountColumnTitle, previousYearColumnTitle);
			}
			chart.series(cht.serie(amountColumn)).setValueAxisFormat(cht.axisFormat().setLabel(translationService.translate("report_invoiced_amount")));
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.PIE_CHART)) {
			PieChartBuilder chart = cht.pieChart().setKey(periodColumn).setShowLabels(reportSelection.getShowValuesInGraphs()).setShowValues(reportSelection.getShowValuesInGraphs()).addSerie(cht.serie(amountColumn));
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		if (reportSelection.getGraphTypes().contains(GraphType.THREED_PIE_CHART)) {
			Pie3DChartBuilder chart = cht.pie3DChart().setKey(periodColumn).setShowLabels(reportSelection.getShowValuesInGraphs()).setShowValues(reportSelection.getShowValuesInGraphs());
			chart.addSerie(cht.serie(amountColumn));
			updateGraphBuilder(chart, graphColors, reportSelection);
			reportBuilder.addSummary(cmp.verticalGap(3), chart);
		}

		reportBuilder.setSummarySplitType(SplitType.IMMEDIATE);

		return reportBuilder;

	}

	protected JRDataSource getGameReportDataSource(Game game) throws Exception {

		DRDataSource dataSource = new DRDataSource("player", "amount", "amountPreviousYear");

		Map<Temporal, Map<String, Object>> valueMap = createPeriodValueMap(reportSelection, "amount", "amountPreviousYear");

		Date startDate = SSUtil.startOfDay(reportSelection.getFromDate(), UserContextService.getTenant().getTimeZoneID());
		Date endDate = SSUtil.endOfDay(reportSelection.getToDate(), UserContextService.getTenant().getTimeZoneID());

		ZoneId tenantZoneId = getTenantTimeZoneId();
		TemporalUnit temporalUnit = reportSelection.getReportPeriod().getTemporalUnit();

		// Load values
		List<Invoice> invoiceList = Invoice.findInvoicesForDateRange(startDate, endDate, null);

		if (invoiceList == null || invoiceList.isEmpty())
			throw new SpeedInvoiceCodeException("message_com_ss_speedinvoice_search_noresults");

		// Check that max number of records not exceeded
		int maxRecordsAllowed = 6000;
		try {
			GlobalSetting setting = GlobalSetting.getGlobalSettingFor(GlobalSetting.MAX_RECORDS_IN_SALES_REPORT);
			if (setting != null)
				maxRecordsAllowed = setting.getIntegerValue();
		} catch (Exception e) {
		}

		if (invoiceList.size() > maxRecordsAllowed)
			throw new SpeedInvoiceCodeException("message_com_ss_speedinvoice_report_tomuchdata");

		// Set used periods
		Set<Temporal> sortedPeriods = SSUtil.asSortedSet(createPeriodValueMap(reportSelection, "dummmy").keySet(), new Comparator<Temporal>() {
			public int compare(Temporal o1, Temporal o2) {
				try {
					if (isBefore(o1, o2))
						return -1;
					else
						return 1;
				} catch (Exception e) {
					return 1;
				}
			}
		});
		reportSelection.setIncludedPeriods(sortedPeriods);

		for (Invoice invoice : invoiceList) {
			Temporal invoicePeriod = getTemporal(Instant.ofEpochMilli(invoice.getInvoiceDate().getTime()).atZone(tenantZoneId), temporalUnit);

			BigDecimal amount;
			if (reportSelection.getIncludeGST())
				amount = invoice.getTotalAmount();
			else
				amount = invoice.getTotalAmountExcludingGST();

			Map<String, Object> dataMap = valueMap.get(invoicePeriod);
			if (dataMap != null)
				dataMap.put("amount", ((BigDecimal) dataMap.get("amount")).add(amount));
		}

		// Load values for previous year
		if (reportSelection.getCompareWithPreviousYear()) {
			invoiceList = Invoice.findInvoicesForDateRange(SSUtil.minusYears(startDate, 1, null), SSUtil.minusYears(endDate, 1, null), null);

			for (Invoice invoice : invoiceList) {
				Temporal invoicePeriod = getTemporal(Instant.ofEpochMilli(invoice.getInvoiceDate().getTime()).atZone(tenantZoneId), temporalUnit);
				invoicePeriod = invoicePeriod.plus(1, ChronoUnit.YEARS);

				BigDecimal amount;
				if (reportSelection.getIncludeGST())
					amount = invoice.getTotalAmount();
				else
					amount = invoice.getTotalAmountExcludingGST();

				Map<String, Object> dataMap = valueMap.get(invoicePeriod);
				if (dataMap != null)
					dataMap.put("amountPreviousYear", ((BigDecimal) dataMap.get("amountPreviousYear")).add(amount));
			}
		}

		// Load data source
		Temporal startPeriod = getStartPeriod(reportSelection);
		Temporal endPeriod = getEndPeriod(reportSelection).plus(1, temporalUnit);
		while (isBefore(startPeriod, endPeriod)) {
			dataSource.add(startPeriod.toString(), valueMap.get(startPeriod).get("amount"), valueMap.get(startPeriod).get("amountPreviousYear"));
			startPeriod = startPeriod.plus(1, temporalUnit);
		}

		return dataSource;
	}


	protected ReportTemplateBuilder getTemplate() {
		if (template == null)
			template = initTemplate();

		return template;

	}

	protected ReportTemplateBuilder initTemplate() {
		ReportTemplateBuilder reportTemplate = template();
		Locale locale = LocaleContextHolder.getLocale();
		if (locale != null)
			reportTemplate.setLocale(locale);

		StyleBuilder columnStyle = stl.style(getStyle("rootStyle")).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
		StyleBuilder columnTitleStyle = stl.style(getStyle("rootStyle")).setBorder(stl.pen1Point()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setBackgroundColor(Color.LIGHT_GRAY).bold();
		StyleBuilder groupStyle = stl.style(getStyle("boldStyle")).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		StyleBuilder subtotalStyle = stl.style(getStyle("boldStyle")).setTopBorder(stl.pen1Point());
		StyleBuilder crosstabGroupStyle = stl.style(columnTitleStyle);
		StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(new Color(170, 170, 170));
		StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(new Color(140, 140, 140));
		StyleBuilder crosstabCellStyle = stl.style(columnStyle).setBorder(stl.pen1Point());

		TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer().setHeadingStyle(0, stl.style(getStyle("boldStyle")));

		reportTemplate.setColumnStyle(columnStyle).setColumnTitleStyle(columnTitleStyle).setGroupStyle(groupStyle).setGroupTitleStyle(groupStyle).setSubtotalStyle(subtotalStyle).highlightDetailEvenRows().crosstabHighlightEvenRows().setCrosstabGroupStyle(crosstabGroupStyle)
				.setCrosstabGroupTotalStyle(crosstabGroupTotalStyle).setCrosstabGrandTotalStyle(crosstabGrandTotalStyle).setCrosstabCellStyle(crosstabCellStyle).setTableOfContentsCustomizer(tableOfContentsCustomizer);

		return reportTemplate;

	}

	protected ComponentBuilder<?, ?> createTitleComponent(ReportSelection reportSelection, boolean printSelection) {

		Tenant tenant = UserContextService.getTenant();
		if (tenant == null) {
			logger.error("Error when creating report. No tenant found");
			return cmp.horizontalList(cmp.text(reportSelection.getReport().getTranslatedName()).setStyle(getStyle("reportTitleStyle")).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
		}

		VerticalListBuilder mainList = cmp.verticalList();

		HorizontalListBuilder reportTitleRow = cmp.horizontalList(cmp.text(reportSelection.getReport().getTranslatedName()).setStyle(getStyle("reportTitleStyle")).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
				cmp.text(tenant.getCompanyName()).setStyle(getStyle("companyTitleStyle")).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));

		mainList.add(reportTitleRow);
		if (printSelection) {
			for (String selection : reportSelection.getSelectionStrings()) {
				mainList.add(cmp.horizontalList(cmp.text(selection).setStyle(getStyle("selectionTitleStyle")).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)));
			}
		}
		mainList.add(cmp.line());
		mainList.add(cmp.verticalGap(10));

		return mainList;

	}

	protected StyleBuilder getStyle(String styleName) {
		if (styleMap == null)
			styleMap = initStyleMap();

		return styleMap.get(styleName);

	}

	protected Map<String, StyleBuilder> initStyleMap() {
		Map<String, StyleBuilder> reportFontNameStyleMap = new HashMap<String, StyleBuilder>();

		StyleBuilder rootStyle = stl.style();
		rootStyle.setFontName(reportFontName);
		reportFontNameStyleMap.put("rootStyle", rootStyle);

		StyleBuilder boldStyle = stl.style(rootStyle).bold();
		reportFontNameStyleMap.put("boldStyle", boldStyle);

		StyleBuilder reportTitleStyle = stl.style(boldStyle);
		reportTitleStyle.setFontSize(18);
		reportTitleStyle.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		reportFontNameStyleMap.put("reportTitleStyle", reportTitleStyle);

		StyleBuilder companyTitleStyle = stl.style(boldStyle);
		companyTitleStyle.setFontSize(14);
		companyTitleStyle.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		reportFontNameStyleMap.put("companyTitleStyle", companyTitleStyle);

		StyleBuilder selectionTitleStyle = stl.style(rootStyle);
		selectionTitleStyle.setFontSize(12);
		selectionTitleStyle.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		reportFontNameStyleMap.put("selectionTitleStyle", selectionTitleStyle);

		StyleBuilder detailStyle = stl.style(rootStyle);
		detailStyle.setFontSize(12);
		reportFontNameStyleMap.put("detailStyle", detailStyle);

		StyleBuilder detailLeftStyle = stl.style(detailStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT).setPadding(5);
		StyleBuilder detailCenteredStyle = stl.style(detailStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setPadding(5);
		StyleBuilder detailRightStyle = stl.style(detailStyle).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setPadding(5);

		reportFontNameStyleMap.put("detailLeftStyle", detailLeftStyle);
		reportFontNameStyleMap.put("detailCenteredStyle", detailCenteredStyle);
		reportFontNameStyleMap.put("detailRightStyle", detailRightStyle);

		StyleBuilder boldLeftStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT).setPadding(5);
		StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setPadding(5);
		StyleBuilder boldRightStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setPadding(5);

		reportFontNameStyleMap.put("boldLeftStyle", boldLeftStyle);
		reportFontNameStyleMap.put("boldCenteredStyle", boldCenteredStyle);
		reportFontNameStyleMap.put("boldRightStyle", boldRightStyle);

		StyleBuilder columnTitleLeftStyle = stl.style(boldLeftStyle).setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY).setFontSize(12).setPadding(5);
		StyleBuilder columnTitleCenteredStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY).setFontSize(12).setPadding(5);
		StyleBuilder columnTitleRightStyle = stl.style(boldRightStyle).setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY).setFontSize(12).setPadding(5);

		reportFontNameStyleMap.put("columnTitleLeftStyle", columnTitleLeftStyle);
		reportFontNameStyleMap.put("columnTitleCenteredStyle", columnTitleCenteredStyle);
		reportFontNameStyleMap.put("columnTitleRightStyle", columnTitleRightStyle);

		return reportFontNameStyleMap;
	}


	protected void close(AutoCloseable closable) {
		try {
			if (closable != null)
				closable.close();
		} catch (Exception e2) {
		}
	}



	@SuppressWarnings("rawtypes")
	protected void updateGraphBuilder(AbstractBaseChartBuilder chart, Color[] seriesColors, ReportSelection reportSelection) {
		if (seriesColors != null)
			chart.seriesColors(seriesColors);
		chart.setStyle(getStyle("detailStyle"));
		chart.setFixedHeight(reportSelection.getGraphSize().getHeight(reportSelection.getReportPageOrientation()));
		if (chart instanceof LineChartBuilder)
			chart.addCustomizer(new LineChartCustomizer());
		else if (chart instanceof AbstractCategoryChartBuilder)
			chart.addCustomizer(new BarChartCustomizer());

	}

	protected static class ColumnHolder<T> {

		public ColumnHolder(String name, TextColumnBuilder<T> column) {
			this.name = name;
			this.column = column;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TextColumnBuilder<T> getColumn() {
			return column;
		}

		public void setColumn(TextColumnBuilder<T> column) {
			this.column = column;
		}

		protected String name;
		protected TextColumnBuilder<T> column;
	}

	protected Color[] getColors(int numberOfColors) {
		return ColorUtils.generateVisuallyDistinctColors(numberOfColors, .6f, .1f, 1);
	}

}
