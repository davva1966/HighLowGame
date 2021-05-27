package au.com.highlowgame.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SSHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			if (modelAndView.getModel() != null) {
				modelAndView.getModel().put("ss_utility", SSHandlerUtil.instance());
				// Add regex for field validation use
				// modelAndView.getModel().put("regexDuration", SSRegexUtil.validationRegexFor(SSRegexUtil.REGEX_DURATION));
				modelAndView.getModel().put("regexInteger", SSRegexUtil.validationRegexFor(SSRegexUtil.REGEX_INTEGER));
				// modelAndView.getModel().put("regexIntegerNeg", SSRegexUtil.validationRegexFor(SSRegexUtil.REGEX_INTEGER, true));
				modelAndView.getModel().put("regexDecimal", SSRegexUtil.validationRegexFor(SSRegexUtil.REGEX_DECIMAL));
				modelAndView.getModel().put("regexDecimalNeg", SSRegexUtil.validationRegexFor(SSRegexUtil.REGEX_DECIMAL, true));
			}
		}
	}

}