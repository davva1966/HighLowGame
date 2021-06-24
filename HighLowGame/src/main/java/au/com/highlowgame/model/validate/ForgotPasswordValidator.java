package au.com.highlowgame.model.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import au.com.highlowgame.service.SecurityService;

@Component
public class ForgotPasswordValidator extends AbstractCompositeValidator {

	@Autowired
	SecurityService securityService;

	public ForgotPasswordValidator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ForgotPasswordBean.class.equals(clazz) || String.class.equals(clazz);
	}

	public void extraValidate(Object obj, Errors e) {
		if (obj instanceof ForgotPasswordBean) {
			ForgotPasswordBean forgotCredentialsBean = (ForgotPasswordBean) obj;

			if (e.hasFieldErrors("email"))
				return;
			if (forgotCredentialsBean.getPlayer() == null)
				e.rejectValue("email", "message_com_ss_highlowgame_forgotcredentials_playerforemailnotfound");

		}

	}

	public void validateCreate(Object obj, Errors e) {
	}

	public void validateUpdate(Object obj, Errors e) {
	}

}
