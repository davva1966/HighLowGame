package au.com.highlowgame.util;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.util.WebUtils;

import au.com.highlowgame.model.DomainEntity;
import au.com.highlowgame.model.validate.AbstractCompositeValidator;
import au.com.highlowgame.service.DatabaseService;
import au.com.highlowgame.service.TranslationService;

public class AppUtil {

	public static String translate(String code) {
		return translate(code, null, (Object) null);
	}

	public static String translate(String code, Object... args) {
		return translate(code, null, args);
	}

	public static String translate(String code, Locale locale) {
		return translate(code, locale, (Object) null);
	}

	public static String translate(String code, Locale locale, Object... args) {
		TranslationService translationService = (TranslationService) ApplicationContextProvider.getApplicationContext().getBean("translationService");
		return translationService.translate(code, locale, args);
	}

	public static DatabaseService getDatabaseService() {
		return (DatabaseService) ApplicationContextProvider.getApplicationContext().getBean("databaseService");
	}

	public static void validateEntity(DomainEntity entity, Errors errors) {

		Validator validator = getValidatorFor(entity);
		if (validator != null) {
			validator.validate(entity, errors);
		} else {
			LocalValidatorFactoryBean constraintValidator = ApplicationContextProvider.getApplicationContext().getBean(LocalValidatorFactoryBean.class);
			constraintValidator.validate(entity, errors);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Validator getValidatorFor(DomainEntity entity) {

		AbstractCompositeValidator entityValidator = null;

		String entityClassName = entity.getClass().getName();

		int idx = entityClassName.lastIndexOf(".");
		String packageName = entityClassName.substring(0, idx);
		String clazzName = entityClassName.substring(idx);

		packageName = packageName + ".validate";
		clazzName = clazzName + "Validator";
		String validatorClassName = packageName + clazzName;

		LocalValidatorFactoryBean constraintValidator = ApplicationContextProvider.getApplicationContext().getBean(LocalValidatorFactoryBean.class);

		Class validatorClass;
		try {
			validatorClass = Class.forName(validatorClassName);
			if (validatorClass != null) {
				entityValidator = (AbstractCompositeValidator) ApplicationContextProvider.getApplicationContext().getBean(validatorClass);
				entityValidator.setEmbeddedValidator(constraintValidator);
				return entityValidator;
			}
		} catch (Exception e) {
			// logger.warn("No validator class found for " +
			// validatorClassName);
		}

		return null;

	}

	public static boolean getUseSSL() {
		boolean forceSSL = false;
		try {
			String forceSSLString = System.getenv("force_ssl");
			if (SSUtil.empty(forceSSLString))
				forceSSLString = System.getProperty("force_ssl");
			if (SSUtil.notEmpty(forceSSLString))
				forceSSL = Boolean.parseBoolean(forceSSLString);
		} catch (Exception e) {
		}

		return forceSSL;
	}

	public static Integer getTablePageSize(HttpServletRequest request) {
		Integer size = 30;
		Cookie cookie = WebUtils.getCookie(request, SSPageSizeFilter.PAGE_SIZE_COOKIE);
		if (cookie != null && cookie.getValue().trim().length() > 0) {
			String sizeStr = cookie.getValue();
			try {
				size = Integer.parseInt(sizeStr);
			} catch (Exception e) {
			}
		}

		return size;

	}

}
