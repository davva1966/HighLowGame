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

import au.com.highlowgame.model.validate.ForgotPasswordBean;
import au.com.highlowgame.model.validate.ForgotPasswordValidator;
import au.com.highlowgame.service.SecurityService;
import au.com.highlowgame.service.TranslationService;
import au.com.highlowgame.util.ApplicationContextProvider;

@RequestMapping("/forgotPassword")
@Controller
public class ForgotPasswordController {

	@Autowired
	TranslationService translationService;

	@Autowired
	SecurityService securityService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		ForgotPasswordValidator validator = ApplicationContextProvider.getApplicationContext().getBean(ForgotPasswordValidator.class);
		validator.setEmbeddedValidator(binder.getValidator());
		binder.setValidator(validator);
	}

	@RequestMapping(produces = "text/html")
	public String forgotPassword(Model uiModel) {
		ForgotPasswordBean bean = new ForgotPasswordBean();
		populateForm(uiModel, bean);
		return "forgotPassword";
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String change(@ModelAttribute("forgotPasswordBean") @Valid ForgotPasswordBean forgotPasswordBean, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateForm(uiModel, forgotPasswordBean);
			return "forgotPassword";
		}

		uiModel.asMap().clear();

		try {
			securityService.sendNewPassword(forgotPasswordBean.getPlayer());
			uiModel.addAttribute("message", translationService.translate("player_forgot_password_emailsent"));
		} catch (Throwable e) {
			uiModel.addAttribute("message", translationService.translate("unable_to_send_email"));
		}

		return "login";

	}

	void populateForm(Model uiModel, ForgotPasswordBean forgotPasswordBean) {
		uiModel.addAttribute("forgotPasswordBean", forgotPasswordBean);

	}

}
