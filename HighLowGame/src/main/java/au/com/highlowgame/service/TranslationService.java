package au.com.highlowgame.service;

import java.util.Locale;

import org.springframework.stereotype.Service;

@Service("translationService")
public interface TranslationService {

	public String translate(String s);

	public String translate(String s, Object... args);

	public String translate(String s, Locale locale);

	public String translate(String s, Locale locale, Object... args);

	public String translate(Throwable ex);

	public String translate(Throwable ex, Locale locale);

	public String translate(Throwable ex, Locale locale, Object... args);

}
