package au.com.highlowgame.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import au.com.highlowgame.model.DomainEntity;
import au.com.highlowgame.model.validate.AbstractCompositeValidator;
import au.com.highlowgame.util.ApplicationContextProvider;
import au.com.highlowgame.util.SSUtil;

public class ValidationServiceImpl implements ValidationService {

	@Autowired
	private DatabaseService databaseService;

	@Autowired
	private Validator constraintValidator;
	
	@Override
	public void rejectIfNotUnique(DomainEntity entity, String property, Errors e) {
		rejectIfNotUnique(entity, property, property, e);
	}

	@Override
	public void rejectIfNotUnique(DomainEntity entity, String property, String messageField, Errors e) {
		String[] properties = new String[1];
		properties[0] = property;
		rejectIfNotUnique(entity, property, messageField, properties, e);
	}

	@Override
	public void rejectIfNotUnique(DomainEntity entity, String identifyingProperty, String[] uniqueProperties, Errors e) {
		rejectIfNotUnique(entity, identifyingProperty, null, uniqueProperties, e);
	}

	@Override
	public void rejectIfNotUnique(DomainEntity entity, String identifyingProperty, String messageField, String[] uniqueProperties, Errors e) {

		Map<String, Object> propertyValueMap = new HashMap<String, Object>();
		for (int i = 0; i < uniqueProperties.length; i++) {
			propertyValueMap.put(uniqueProperties[i], getValue(entity, uniqueProperties[i]));
		}

		if (databaseService.verifyUnique(entity, propertyValueMap) == false) {
			if (SSUtil.empty(messageField))
				messageField = identifyingProperty;
			e.rejectValue(identifyingProperty, buildMessageKey(messageField, ALREADYEXIST), buildArgs(propertyValueMap.get(identifyingProperty)), null);
		}

	}

	@Override
	public boolean verifyUnique(DomainEntity entity, String[] uniqueProperties) {
		Map<String, Object> propertyValueMap = new HashMap<String, Object>();
		for (int i = 0; i < uniqueProperties.length; i++) {
			propertyValueMap.put(uniqueProperties[i], getValue(entity, uniqueProperties[i]));
		}

		return databaseService.verifyUnique(entity, propertyValueMap);

	}

	public void rejectIfEmptyOrWhitespace(String property, Errors e) {
		ValidationUtils.rejectIfEmptyOrWhitespace(e, property, "message_com_ss_highlowgame_required");
	}

	protected Object getValue(DomainEntity entity, String property) {
		BeanWrapper entityWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);
		entityWrapper.setConversionService(new DefaultFormattingConversionService());
		return entityWrapper.getPropertyValue(property);
	}

	protected Object[] buildArgs(Object value) {
		Object[] args = new Object[1];
		args[0] = value;
		return args;
	}

	protected Object[] buildArgs(Object value1, Object value2) {
		Object[] args = new Object[2];
		args[0] = value1;
		args[1] = value2;
		return args;
	}

	protected Object[] buildArgs(Object value1, Object value2, Object value3) {
		Object[] args = new Object[3];
		args[0] = value1;
		args[1] = value2;
		args[2] = value3;
		return args;
	}

	protected String buildMessageKey(String property, String messageId) {
		return ("message_com_ss_highlowgame_" + property + "_" + messageId).toLowerCase();
	}

	@Override
	public ValidationResult validate(Object object) {
		Map<String, String> map = new HashMap<String, String>();
		MapBindingResult errors = new MapBindingResult(map, object.getClass().getName());

		Validator validator = getValidatorFor(object);
		if (validator != null)
			validator.validate(object, errors);
		else
			constraintValidator.validate(object, errors);

		ValidationResult result = new ValidationResult();
		if (errors.hasFieldErrors()) {
			for (FieldError error : errors.getFieldErrors()) {
				result.addMessage(error.getField() + " - " + error.getDefaultMessage());
			}
		} else if (errors.hasErrors()) {
			for (ObjectError error : errors.getAllErrors()) {
				result.addMessage(error.getDefaultMessage());
			}
		}

		return result;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Validator getValidatorFor(Object object) {

		AbstractCompositeValidator entityValidator = null;

		String entityClassName = object.getClass().getName();
		String validatorClassName = entityClassName + "Validator";
		Class validatorClass;
		try {
			validatorClass = Class.forName(validatorClassName);
			if (validatorClass != null) {
				entityValidator = (AbstractCompositeValidator) ApplicationContextProvider.getApplicationContext().getBean(validatorClass);
				entityValidator.setEmbeddedValidator(constraintValidator);
				return entityValidator;
			}
		} catch (Exception e) {
		}

		return null;

	}

}
