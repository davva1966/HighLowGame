package au.com.highlowgame.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.context.i18n.LocaleContextHolder;

public class SSUtil {

	public static final String NEWLINE = System.getProperty("line.separator");

	public static String shorten(String s, int maxlen) {
		if (s == null)
			return s;
		String shortString = s.trim();
		if (shortString.length() <= maxlen)
			return shortString;

		return shortString.substring(0, maxlen - 3) + "...";

	}

	public static String shortenHtml(String s, int maxlen) {
		if (s == null)
			return s;
		String shortString = s.trim();
		if (shortString.length() <= maxlen)
			return shortString;

		shortString = shortString.substring(0, maxlen - 3);

		try {
			// No line breaks, return shortened value
			int lastCompleteBreakPos = shortString.toLowerCase().lastIndexOf("<");
			if (lastCompleteBreakPos == -1)
				return shortString + "...";

			// Do not cut of string in the middle of line breaks (<br/>)
			int idxLatest = -1;
			int idxPrevious = -1;
			for (int i = shortString.length(); i > 0; i--) {
				char c = shortString.charAt(i - 1);
				if (c == '<') {
					idxPrevious = idxLatest;
					idxLatest = i;
					if (idxPrevious - idxLatest > 5 || idxPrevious == -1 && shortString.length() - idxLatest > 7)
						return shortString.substring(0, idxPrevious - 1).trim() + "...";
				}
			}
		} catch (Exception e) {
		}

		return shortString + "...";

	}

	public static boolean isJsonRequest(HttpServletRequest request) {

		String referer = request.getHeader("referer");
		String tenant = request.getHeader("tenant");
		String accept = request.getHeader("accept");
		String contentType = request.getHeader("content-type");

		if (tenant != null)
			return true;
		if (referer != null)
			return false;

		boolean isJSON = false;
		if (accept != null && accept.trim().length() > 0)
			isJSON = accept.toLowerCase().contains("application/json");

		if (isJSON)
			return true;

		if (contentType != null && contentType.trim().length() > 0)
			isJSON = contentType.toLowerCase().contains("application/json");

		return isJSON;

	}

	public static <T> List<T> asSortedList(Collection<T> collection, Comparator<? super T> comparator) {
		if (collection == null || collection.size() == 0)
			return new ArrayList<T>();
		List<T> list = new ArrayList<T>(collection);
		java.util.Collections.sort(list, comparator);
		return list;
	}

	public static <T> Set<T> asSortedSet(Set<T> set, Comparator<? super T> comparator) {
		if (set == null || set.size() == 0)
			return new HashSet<T>();

		Set<T> sortedSet = new TreeSet<T>(comparator);
		sortedSet.addAll(set);
		return sortedSet;
	}

	public static String[] formatSeparatedToArray(String string, String separator, boolean trimValues) {

		if (empty(string))
			return new String[0];

		StringTokenizer tokenizer = new StringTokenizer(string, separator);

		List<String> elements = new ArrayList<String>();

		while (tokenizer.hasMoreTokens()) {
			if (trimValues)
				elements.add(tokenizer.nextToken().trim());
			else
				elements.add(tokenizer.nextToken());
		}

		return elements.toArray(new String[0]);

	}

	public static Map<String, String> formatSeparatedToMap(String string, String separator1, String separator2, boolean trimValues) {

		if (empty(string))
			return new HashMap<String, String>();

		StringTokenizer tokenizer = new StringTokenizer(string, separator1);

		Map<String, String> map = new HashMap<String, String>();

		while (tokenizer.hasMoreTokens()) {
			String[] keyAndValue = formatSeparatedToArray(tokenizer.nextToken(), separator2, trimValues);
			if (keyAndValue.length == 0)
				continue;
			if (keyAndValue.length == 1)
				map.put(keyAndValue[0], "");
			map.put(keyAndValue[0], keyAndValue[1]);
		}

		return map;

	}

	public static String formatArrayToSeparated(String[] array, String separator) {

		if (array == null || array.length == 0)
			return "";

		StringBuilder formattedString = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			if (i > 0)
				formattedString.append(separator);
			formattedString.append(array[i]);
		}

		return formattedString.toString();
	}

	public static String formatListToSeparated(List<String> list, String separator) {

		if (list == null || list.size() == 0)
			return "";

		String[] arr = new String[list.size()];
		arr = list.toArray(arr);

		return formatArrayToSeparated(arr, separator);
	}

	public static String formatMapToSeparated(Map<String, String> map, String separator1, String separator2) {

		if (map == null || map.size() == 0)
			return "";

		StringBuilder formattedString = new StringBuilder();

		int idx = 0;
		for (String key : map.keySet()) {
			if (idx > 0)
				formattedString.append(separator1);
			formattedString.append(key);
			formattedString.append(separator2);
			formattedString.append(map.get(key));
			idx++;
		}

		return formattedString.toString();
	}

	public static String getFileFormatName(InputStream stream) {
		try {
			// Create an image input stream on the image
			ImageInputStream iis = ImageIO.createImageInputStream(stream);

			// Find all image readers that recognize the image format
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				// No readers found
				return null;
			}

			// Use the first reader
			ImageReader reader = (ImageReader) iter.next();

			// Close stream
			iis.close();

			// Return the format name
			return reader.getFormatName();
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		return null;
	}

	public static String getFileExtension(String fileName) {
		String ext = null;
		int i = fileName.lastIndexOf('.');

		if (i > 0 && i < fileName.length() - 1)
			ext = fileName.substring(i + 1).toLowerCase();

		if (ext == null)
			return "";
		return ext;
	}

	public static BigDecimal convertDurationToDecimal(String duration) throws Exception {

		duration = duration.trim();
		if (duration.startsWith(":"))
			duration = "0" + duration;
		if (duration.endsWith(":"))
			duration = duration + "0";

		String[] parts = duration.split(":");
		if (parts.length > 2)
			throw new Exception("Invalid format. Must be \"hh:mm\"");

		int hours = 0;
		int minutes = 0;
		if (parts.length == 1) {
			try {
				hours = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				throw new Exception("Invalid format. Must be \"hh:mm\"");
			}
			return new BigDecimal(hours);

		}

		try {
			hours = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			throw new Exception("Invalid format. Must be \"hh:mm\"");
		}
		try {
			if (parts[1].length() != 2)
				throw new Exception("Invalid format. Must be \"hh:mm\"");
			minutes = Integer.parseInt(parts[1]);
			if (minutes > 59)
				throw new Exception("Minutes greater than 59 not allowed");
		} catch (NumberFormatException e) {
			throw new Exception("Invalid format. Must be \"hh:mm\"");
		}

		if (minutes == 0)
			return new BigDecimal(hours);

		BigDecimal minutesBD = new BigDecimal(minutes);
		BigDecimal hourFraction = minutesBD.divide(new BigDecimal(60), 3, RoundingMode.HALF_EVEN);
		return new BigDecimal(hours).add(hourFraction);

	}

	public static String convertDecimalToDuration(BigDecimal decimal) {

		int hours = decimal.intValue();
		BigDecimal fraction = null;
		if (hours > 0)
			fraction = decimal.subtract(new BigDecimal(hours));
		else
			fraction = decimal;

		BigDecimal minutesBD = BigDecimal.ZERO;
		if (fraction.compareTo(BigDecimal.ZERO) > 0) {
			minutesBD = new BigDecimal(60).multiply(fraction);
			minutesBD = minutesBD.setScale(0, RoundingMode.HALF_EVEN);
		}

		int minutes = minutesBD.intValue();

		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(hours));
		if (minutes > 0) {
			sb.append(":");
			if (minutes < 10)
				sb.append("0");
			sb.append(Integer.toString(minutes));
		}

		return sb.toString();

	}

	public static String formatDate(Date date, Locale locale, String timeZoneID) {

		DateFormat df = null;
		if (locale != null) {
			df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		} else {
			locale = LocaleContextHolder.getLocale();
			if (locale != null)
				df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			else
				df = DateFormat.getDateInstance(DateFormat.SHORT);
		}

		if (notEmpty(timeZoneID))
			df.setTimeZone(TimeZone.getTimeZone(timeZoneID));
		else
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

		return df.format(date);
	}

	public static String formatAmount(BigDecimal amount) {
		return formatAmount(amount, null, false);
	}

	public static String formatNumberForWeb(BigDecimal amount) {
		return formatAmount(amount, null, true);
	}

	public static String formatAmount(BigDecimal amount, Locale locale, boolean forWeb) {
		return getNumberFormatter(locale, forWeb).format(amount);

	}

	public static BigDecimal convertToBigDecimalForWeb(String number) {
		if (number == null || number.trim().length() == 0)
			return null;
		number = number.trim();
		try {
			return (BigDecimal) getNumberFormatter(null, true).parse(number);
		} catch (ParseException e) {
			throw new RuntimeException("Error parsing: " + number + ". Not a valid number");
		}

	}

	protected static DecimalFormat getNumberFormatter(Locale locale, boolean forWeb) {
		if (locale == null)
			locale = LocaleContextHolder.getLocale();
		if (locale == null)
			locale = Locale.getDefault();

		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
		symbols.setGroupingSeparator(' ');
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
		formatter.setDecimalFormatSymbols(symbols);
		formatter.setParseBigDecimal(true);
		formatter.setMinimumFractionDigits(2);
		if (forWeb)
			formatter.setGroupingUsed(false);

		return formatter;

	}

	public static String formatPercent(BigDecimal percent) {
		return formatPercent(percent, false);
	}

	public static String formatPercent(BigDecimal percent, boolean shortFormat) {
		String s = formatNumberForWeb(percent);
		if (!shortFormat)
			s = s + "%";
		return s;

	}

	public static int countLines(String text) {
		if (text == null || text.length() == 0)
			return 0;
		Matcher m = Pattern.compile(NEWLINE).matcher(text);
		int lines = 1;
		while (m.find()) {
			lines++;
		}
		return lines;
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	public static boolean validateGlobalTenantCode(DataSource ds, String tenantCode) {

		Connection con = null;
		try {
			con = ds.getConnection();
			PreparedStatement stmt = con.prepareStatement("select tenant_code from global_tenant where tenant_code = ?");
			stmt.setString(1, tenantCode);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() == false)
				return true;

			return false;
		} catch (Exception e) {
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e2) {
				}
			}
		}

	}

	public static boolean validateGlobalTenantEmail(DataSource ds, String tenantCode, String email) {

		Connection con = null;
		try {
			con = ds.getConnection();
			PreparedStatement stmt = con.prepareStatement("select tenant_code from global_tenant where email = ?");
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() == false)
				return true;

			if (tenantCode.trim().equalsIgnoreCase(rs.getString(1)))
				return true;

			return false;
		} catch (Exception e) {
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e2) {
				}
			}
		}

	}

	public static boolean empty(String value) {
		return empty(value, false);
	}

	public static boolean empty(String value, boolean trim) {
		return !notEmpty(value, trim);
	}

	@SuppressWarnings("rawtypes")
	public static boolean empty(Collection value) {
		return !notEmpty(value);
	}

	public static boolean empty(Boolean value) {
		return !notEmpty(value);
	}

	public static boolean notEmpty(String value) {
		return notEmpty(value, false);
	}

	public static boolean notEmpty(String value, boolean trim) {
		if (value == null)
			return false;

		// Remove various spaces
		value = value.replace("\u2002", "");
		value = value.replace("\u2003", "");
		value = value.replace("\u2004", "");
		value = value.replace("\u2005", "");
		value = value.replace("\u2006", "");
		value = value.replace("\u2007", "");
		value = value.replace("\u2008", "");
		value = value.replace("\u2009", "");
		value = value.replace("\u200A", "");
		value = value.replace("\u00A0", "");
		value = value.replace("\u3000", "");

		if (trim)
			value = value.trim();
		return value.length() > 0;
	}

	public static boolean notEmpty(Boolean value) {
		return value != null && value;
	}

	@SuppressWarnings("rawtypes")
	public static boolean notEmpty(Collection value) {
		return value != null && !value.isEmpty();
	}

	public static String capitalizeString(String string) {
		if (string.length() == 0)
			return string;

		String firstLetter = string.substring(0, 1);
		String theRest = string.substring(1, string.length());
		return firstLetter.toUpperCase() + theRest;

	}

	public static String getClientAddressFor(HttpServletRequest httpServletRequest) {
		String ip = httpServletRequest.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getRemoteAddr();
		}

		return ip;
	}

	public static String toString(Long number) {
		if (number == null)
			return "";
		return Long.toString(number);
	}

	public static Date minusYears(Date date, int yearToSubtract, String timeZoneId) {
		Calendar cal = Calendar.getInstance();
		if (notEmpty(timeZoneId))
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		else
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(date.getTime());
		cal.add(Calendar.YEAR, 0 - yearToSubtract);
		return cal.getTime();

	}

	public static Date startOfYear(Date date, String timeZoneId) {
		Calendar cal = Calendar.getInstance();
		if (notEmpty(timeZoneId))
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		else
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();

	}

	public static Date startOfDay(Date date, String timeZoneId) {
		Calendar cal = Calendar.getInstance();
		if (notEmpty(timeZoneId))
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		else
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();

	}

	public static Date endOfDay(Date date, String timeZoneId) {
		Calendar cal = Calendar.getInstance();
		if (notEmpty(timeZoneId))
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		else
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List toList(Object o) {
		List l = new ArrayList();
		l.add(o);
		return l;
	}

	public static String[] separateEmails(String emailString) {

		if (empty(emailString))
			return new String[0];

		List<String> elements1 = new ArrayList<String>();
		List<String> elements2 = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer(emailString, ";");
		while (tokenizer.hasMoreTokens()) {
			elements1.add(tokenizer.nextToken().trim());
		}

		for (String segment : elements1) {
			tokenizer = new StringTokenizer(segment, ",");
			while (tokenizer.hasMoreTokens()) {
				elements2.add(tokenizer.nextToken().trim());
			}
		}

		for (String segment : elements2) {
			tokenizer = new StringTokenizer(segment, " ");
			while (tokenizer.hasMoreTokens()) {
				emails.add(tokenizer.nextToken().trim());
			}
		}

		return emails.toArray(new String[0]);

	}

	public static boolean stringsEqual(String s1, String s2, boolean ignoreCase) {
		if (empty(s1) && empty(s2))
			return true;

		if (empty(s1) && !empty(s2))
			return false;

		if (!empty(s1) && empty(s2))
			return false;

		if (ignoreCase)
			return s1.equalsIgnoreCase(s2);

		return s1.equals(s2);
	}

	public static boolean integerEqual(Integer s1, Integer s2) {
		if (s1 == null && s2 == null)
			return true;

		if (s1 == null && s2 != null)
			return false;

		if (s1 != null && s2 == null)
			return false;

		return s1.compareTo(s2) == 0;
	}

	public static String cleanEmail(String email) {
		if (email == null)
			return email;

		while (email.contains("@@")) {
			email = email.replace("@@", "@");
		}
		while (email.contains("..")) {
			email = email.replace("..", ".");
		}
		while (email.contains(" ")) {
			email = email.replace(" ", "");
		}
		while (email.contains("/n")) {
			email = email.replace("/n", "");
		}
		while (email.contains(";")) {
			email = email.replace(";", "");
		}
		while (email.contains(",")) {
			email = email.replace(",", "");
		}
		while (email.contains("\u00A0")) {
			email = email.replace("\u00A0", "");
		}
		while (email.contains("\u0020")) {
			email = email.replace("\u0020", "");
		}

		// email = email.trim();
		email = trimWhitespace(email);

		// Remove "." from beginning and end
		while (email.startsWith(".")) {
			email = email.substring(1);
		}
		while (email.endsWith(".")) {
			email = email.substring(0, email.length() - 1);
		}

		return email;

	}

	public static List<String> extractEmails(String s) {
		List<String> emails = new ArrayList<String>();
		String regex = "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
		Matcher m = Pattern.compile(regex).matcher(s);
		while (m.find()) {
			emails.add(m.group());
		}

		return emails;
	}

	public static String trimWhitespace(String str) {
		if (empty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && (Character.isWhitespace(sb.charAt(0)) || sb.charAt(0) == '\u0020' || sb.charAt(0) == '\u00A0')) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && (Character.isWhitespace(sb.charAt(sb.length() - 1)) || sb.charAt(sb.length() - 1) == '\u0020' || sb.charAt(sb.length() - 1) == '\u00A0')) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	final static int[] illegalChars = { 34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47, 170 };

	static {
		Arrays.sort(illegalChars);
	}

	public static String sanitizeFileName(String name) {
		StringBuilder cleanName = new StringBuilder();
		int len = name.codePointCount(0, name.length());
		for (int i = 0; i < len; i++) {
			int c = name.codePointAt(i);
			if (Arrays.binarySearch(illegalChars, c) < 0) {
				cleanName.appendCodePoint(c);
			}
		}
		return cleanName.toString();
	}

}
