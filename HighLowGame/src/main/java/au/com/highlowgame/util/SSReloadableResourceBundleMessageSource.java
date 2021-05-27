package au.com.highlowgame.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class SSReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

	protected String baseFileName = "WEB-INF/i18n/application";

	public String getBaseFileName() {
		return baseFileName;
	}

	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	public String getActualLanguage(Locale locale) {

		String lang = "en";

		List<String> filenames = calculateFilenamesForLocale(baseFileName, locale);
		for (String filename : filenames) {
			PropertiesHolder propHolder = getProperties(filename);
			if (propHolder.getProperties() != null) {
				int start = filename.lastIndexOf(File.separator);
				if (start < 0)
					start = filename.lastIndexOf("/");
				if (start < 0)
					start = 0;
				int idx1 = filename.indexOf("_", start);
				if (idx1 > 0) {
					lang = filename.substring(idx1 + 1);
					break;
				}
			}
		}

		return lang;

	}

	public Set<String> getAllSupportedLanguages() {
		Set<String> supportedLanguages = new HashSet<String>();
		for (Locale locale : Locale.getAvailableLocales()) {
			String lang = getActualLanguage(locale);
			supportedLanguages.add(lang);
		}
		return supportedLanguages;
	}

}
