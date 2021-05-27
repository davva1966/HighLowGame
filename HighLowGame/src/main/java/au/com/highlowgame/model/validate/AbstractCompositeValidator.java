package au.com.highlowgame.model.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import au.com.highlowgame.model.DomainEntity;
import au.com.highlowgame.service.ValidationService;

public abstract class AbstractCompositeValidator implements Validator {

	@Autowired
	protected ValidationService validationService;

	private Validator embeddedValidator;

	public AbstractCompositeValidator() {
		super();
	}

	public Validator getEmbeddedValidator() {
		return embeddedValidator;
	}

	public void setEmbeddedValidator(Validator embeddedValidator) {
		this.embeddedValidator = embeddedValidator;
	}

	public void validate(Object obj, Errors e) {
		if (getEmbeddedValidator() != null)
			getEmbeddedValidator().validate(obj, e);

		extraValidate(obj, e);
	}

	public void extraValidate(Object obj, Errors e) {
		if (obj instanceof DomainEntity) {
			DomainEntity entity = (DomainEntity) obj;
			if (entity.getId() == null) {
				validateCreate(obj, e);
			} else {
				validateUpdate(obj, e);
			}
		}

	}

	public abstract void validateCreate(Object obj, Errors e);

	public abstract void validateUpdate(Object obj, Errors e);

}
