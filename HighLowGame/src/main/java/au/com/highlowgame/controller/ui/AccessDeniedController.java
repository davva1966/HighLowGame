package au.com.highlowgame.controller.ui;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.highlowgame.util.Message;

@Controller
@RequestMapping("/accessDenied")
public class AccessDeniedController {

	@RequestMapping(produces = "text/html")
	public String showAccessDenied(Model uiModel) {
		return "accessDenied";
	}

	@RequestMapping(headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<String> showAccessDeniedJson() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		return new ResponseEntity<String>(Message.NOT_AUTHORIZED.toJson(), headers, HttpStatus.UNAUTHORIZED);
		
	}
}
