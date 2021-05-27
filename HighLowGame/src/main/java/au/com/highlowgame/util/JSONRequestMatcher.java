package au.com.highlowgame.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

import au.com.highlowgame.util.SSUtil;


public class JSONRequestMatcher implements RequestMatcher {

	@Override
	public boolean matches(HttpServletRequest request) {
		return SSUtil.isJsonRequest(request);
	}

}
