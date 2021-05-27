package au.com.highlowgame.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.Formatter;

public class FormattingHelper {

	private static FormattingHelper instance;

	static {
		instance = new FormattingHelper();
	}

	public FormattingHelper() {
		super();
	}

	public static FormattingHelper instance() {
		return instance;

	}

	public Formatter<Number> getCurrencyFormatter() {
		return new SpeedSolutionsCurrencyFormatter();
	}

	public String formatCurrency(Locale locale, BigDecimal amount, String currencyCode) {

		if (amount == null)
			return "";

		SpeedSolutionsCurrencyFormatter formatter = (SpeedSolutionsCurrencyFormatter) getCurrencyFormatter();
		return formatter.print(amount, locale, currencyCode);

	}

	public String formatDateToIso(Date date) {
		return formatDateToIso(date, false);

	}

	public String formatDateToIso(Date date, boolean includeTime) {
		if (date == null)
			return "";

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df;
		if (includeTime)
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		else
			df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(tz);
		return df.format(date);

	}

	public String formatLogDate(Date date) {
		return formatDate(date, "M-");
	}

	public String formatLogDate(Date date, boolean includeTime) {
		if (includeTime)
			return formatDate(date, "MM");
		else
			return formatDate(date, "M-");
	}

	public String formatDate(Date date, String style) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle(style, LocaleContextHolder.getLocale()));
		return formatter.print(date.getTime());
	}

	public String lineBreaksForHTML(String input) {
		Scanner scanner = new Scanner(input);
		List<String> lines = new ArrayList<String>();

		do {
			lines.add(scanner.nextLine());
		} while (scanner.hasNextLine());
		scanner.close();

		return StringUtils.join(lines, "<br/>");
	}

}
