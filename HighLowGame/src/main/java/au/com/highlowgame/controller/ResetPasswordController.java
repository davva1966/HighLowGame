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

import au.com.highlowgame.model.Player;
import au.com.highlowgame.model.ResetPassword;
import au.com.highlowgame.model.validate.ChangePasswordValidator;
import au.com.highlowgame.service.SecurityService;
import au.com.highlowgame.service.ValidationService;
import au.com.highlowgame.util.ApplicationContextProvider;

@RequestMapping("/resetPassword")
@Controller
public class ResetPasswordController {

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
		populateForm(uiModel, new ResetPassword());
		return "resetPassword";
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String change(@ModelAttribute("resetPasswordBean") @Valid ResetPassword resetPasswordBean, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateForm(uiModel, resetPasswordBean);
			return "resetPassword";
		}
		uiModel.asMap().clear();

		securityService.updatePassword(resetPasswordBean.getPlayer(), resetPasswordBean.getNewPassword());

		return "passwordChanged";
	}
	
	void populateForm(Model uiModel, ResetPassword resetPasswordBean) {
		uiModel.addAttribute("resetPasswordBean", resetPasswordBean);
		uiModel.addAttribute("players", Player.findAllPlayers());
	}

}
