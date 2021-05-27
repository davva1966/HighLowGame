package au.com.highlowgame.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class TranslationServiceImpl implements TranslationService {

	private MessageSource messageSource;

	public TranslationServiceImpl() {
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String translate(String s) {
		return translate(s, (Object) null);
	}

	public String translate(String s, Object... args) {
		return translate(s, null, args);
	}

	public String translate(String s, Locale locale) {
		return translate(s, locale, (Object) null);

	}

	public String translate(Throwable ex) {
		return translate(ex, null);

	}

	public String translate(Throwable ex, Locale locale) {
		return translate(ex.getLocalizedMessage(), locale, (Object) null);

	}
	
	public String translate(Throwable ex, Locale locale, Object...args) {
		return translate(ex.getLocalizedMessage(), locale, args);

	}

	public String translate(String s, Locale locale, Object... args) {
		if (messageSource == null)
			return s;
		if (locale == null)
			locale = LocaleContextHolder.getLocale();
		if (locale == null)
			return s;
		return messageSource.getMessage(s, args, s, locale);
	}

}
