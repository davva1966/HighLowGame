package au.com.highlowgame.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sessionTimeout")
public class SessionTimeoutController {

    @RequestMapping(produces = "text/html")
    public String showSessionTimeout(Model uiModel) {
		uiModel.addAttribute("sessionTimedOut", true);
		return "login";
    }
}
