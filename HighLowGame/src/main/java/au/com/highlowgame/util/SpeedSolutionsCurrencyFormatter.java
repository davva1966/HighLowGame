package au.com.highlowgame.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import au.com.highlowgame.util.SSUtil;

public class SpeedSolutionsCurrencyFormatter extends SpeedSolutionsAbstractNumberFormatter {

	public SpeedSolutionsCurrencyFormatter() {
		super();
	}

	public String print(Number number, Locale locale, String currencyCode) {
		NumberFormat nf = getNumberFormat(locale, currencyCode);
		if (nf == null)
			return "";

		return nf.format(number);
	}

	protected NumberFormat getNumberFormat(Locale locale) {
		return getNumberFormat(locale, null);
	}

	protected NumberFormat getNumberFormat(Locale locale, String currencyCode) {

		if (SSUtil.empty(currencyCode) || locale == null)
			return null;

		Currency currency = Currency.getInstance(currencyCode.trim().toUpperCase());
		if (currency == null)
			return null;

		DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
		format.setParseBigDecimal(true);
		format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		format.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		format.setCurrency(currency);
		if (this.roundingMode != null && roundingModeOnDecimalFormat) {
			format.setRoundingMode(this.roundingMode);
		}

		return format;
	}

}
