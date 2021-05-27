package au.com.highlowgame.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

public class SSOpenEntityManagerInViewFilter extends OpenEntityManagerInViewFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// String excludePatterns =
		// getFilterConfig().getInitParameter("excludePatterns");
		String url = request.getRequestURL().toString();
		return url.toLowerCase().contains("_flow");
	}

}
