package au.com.highlowgame.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.com.highlowgame.model.ChangePassword;
import au.com.highlowgame.model.validate.ChangePasswordValidator;
import au.com.highlowgame.service.SecurityService;
import au.com.highlowgame.service.ValidationService;
import au.com.highlowgame.util.ApplicationContextProvider;

@RequestMapping("/changePassword")
@Controller
public class ChangePasswordController {

	@Autowired
	SecurityService securityService;

	@Autowired
	ValidationService validationService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		ChangePasswordValidator validator = ApplicationContextProvider.getApplicationContext().getBean(ChangePasswordValidator.class);
		validator.setEmbeddedValidator(binder.getValidator());
		binder.setValidator(validator);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(Model uiModel) {
		populateForm(uiModel, new ChangePassword());
		return "changePassword";
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String change(@ModelAttribute("changePassword") @Valid ChangePassword changePassword, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateForm(uiModel, changePassword);
			return "changePassword";
		}
		uiModel.asMap().clear();

		securityService.updatePassword(changePassword.getNewPassword());

		return "passwordChanged";
	}

	void populateForm(Model uiModel, ChangePassword changePassword) {
		uiModel.addAttribute("changePassword", changePassword);
	}

}
