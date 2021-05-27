package au.com.highlowgame.util;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import org.springframework.context.i18n.LocaleContextHolder;

public class SSRegexUtil {

	public static final int REGEX_DURATION = 1;
	public static final int REGEX_INTEGER = 2;
	public static final int REGEX_DECIMAL = 3;

	private static Map<String, REGX> cache = new HashMap<String, REGX>();

	public static class REGX implements Serializable {

		private static final long serialVersionUID = 1L;

		public String regex;

		public REGX(String regex) {
			this.regex = regex;
		}

		public String getRegex() {
			return regex;
		}

		public String getRegexEscaped() {
			return SSRegexUtil.escape(getRegex());
		}
	}

	public static REGX validationRegexForInteger() {
		return validationRegexFor(REGEX_INTEGER);
	}

	public static REGX validationRegexForDecimal() {
		return validationRegexFor(REGEX_DECIMAL);
	}

	public static REGX validationRegexFor(int type) {
		return validationRegexFor(type, false);
	}

	public static REGX validationRegexFor(int type, boolean allowNegative) {

		String cacheKey = Integer.toString(type) + allowNegative;

		if (type != REGEX_DECIMAL && cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		} else {
			cacheKey = cacheKey + getDecimalSeparator();
			if (cache.containsKey(cacheKey))
				return cache.get(cacheKey);
		}

		String regex = "";
		switch (type) {
		case REGEX_DURATION:
			regex = "^(([0]*[0-9]+:?[0-5][0-9])|\\s*)$";
			break;
		case REGEX_INTEGER:
			if (allowNegative)
				regex = "^((\\-?[0-9]*)|([0-9]*\\-?))$";
			else
				regex = "^([0-9]*)$";
			break;
		case REGEX_DECIMAL:
			if (allowNegative)
				regex = "^((\\-?[0-9]*\\" + getDecimalSeparator() + "?[0-9]+)|([0-9]*\\" + getDecimalSeparator() + "?[0-9]+\\-?)|\\s*)$";
			else
				regex = "^(([0-9]*\\" + getDecimalSeparator() + "?[0-9]+)|\\s*)$";
			break;
		default:
			regex = "";
			break;
		}

		REGX regx = new REGX(regex);

		cache.put(cacheKey, regx);

		return regx;

	}

	public static char getDecimalSeparator() {
		Locale locale = LocaleContextHolder.getLocale();
		if (locale == null)
			return '.';

		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		return dfs.getDecimalSeparator();
	}

	public static String escape(String regex) {
		return Matcher.quoteReplacement(regex);

	}
}
