package au.com.highlowgame.model.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import au.com.highlowgame.model.ChangePassword;
import au.com.highlowgame.model.ResetPassword;
import au.com.highlowgame.service.SecurityService;
import au.com.highlowgame.user.UserContextService;

@Component
public class ChangePasswordValidator extends AbstractCompositeValidator {

	@Autowired
	SecurityService securityService;

	public ChangePasswordValidator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ChangePassword.class.equals(clazz) || ResetPassword.class.equals(clazz) || String.class.equals(clazz);
	}

	public void extraValidate(Object obj, Errors e) {
		if (obj instanceof ChangePassword) {
			ChangePassword changePassword = (ChangePassword) obj;

			if (securityService.passwordMatch(changePassword.getPassword(), UserContextService.getCurrentPlayer().getPassword()) == false)
				e.rejectValue("password", "message_com_ss_highlowgame_password_invalidcurrentpassword");
			if (changePassword.getNewPassword().equals(changePassword.getNewPasswordConfirmation()) == false) {
				e.rejectValue("newPassword", "message_com_ss_highlowgame_password_passwordconfirmationmatching");
				e.rejectValue("newPasswordConfirmation", "message_com_ss_highlowgame_password_passwordconfirmationmatching");
			}

		}

		if (obj instanceof ResetPassword) {
			ResetPassword resetPasswordBean = (ResetPassword) obj;

			if (resetPasswordBean.getNewPassword().equals(resetPasswordBean.getNewPasswordConfirmation()) == false) {
				e.rejectValue("newPassword", "message_com_ss_highlowgame_password_passwordconfirmationmatching");
				e.rejectValue("newPasswordConfirmation", "message_com_ss_highlowgame_password_passwordconfirmationmatching");
			}

		}

	}

	public void validateCreate(Object obj, Errors e) {
	}

	public void validateUpdate(Object obj, Errors e) {
	}

}
