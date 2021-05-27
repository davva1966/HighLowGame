package au.com.highlowgame.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

public class SSPageSizeFilter implements Filter {

	public static final String PAGE_SIZE_COOKIE = "SSPAGESIZE";

	protected static CookieGenerator cookieGenerator;

	static {
		cookieGenerator = new CookieGenerator();
		cookieGenerator.setCookieName(PAGE_SIZE_COOKIE);
		cookieGenerator.setCookieMaxAge(2000000000);
	}

	static class ModifiedRequest extends HttpServletRequestWrapper {

		private String requestedSize;

		public ModifiedRequest(ServletRequest request, String requestedSize) {
			super((HttpServletRequest) request);
			this.requestedSize = requestedSize;
		}

		public String getParameter(String paramName) {
			if (paramName.equals("size")) {
				String cookiePageSize = findPageSize();
				if (cookiePageSize != null)
					return cookiePageSize;
			}

			return super.getParameter(paramName);
		}

		public String[] getParameterValues(String paramName) {
			String values[] = super.getParameterValues(paramName);
			if (paramName.equals("size")) {
				String cookiePageSize = findPageSize();
				if (cookiePageSize != null) {
					String[] newArr = new String[1];
					newArr[0] = cookiePageSize;
					return newArr;
				}
			}
			return values;
		}

		protected String findPageSize() {
			if (requestedSize != null)
				return requestedSize;
			HttpServletRequest httpRequest = (HttpServletRequest) getRequest();
			Cookie cookie = WebUtils.getCookie(httpRequest, PAGE_SIZE_COOKIE);
			if (cookie != null && cookie.getValue().trim().length() > 0)
				return cookie.getValue();

			return null;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (request.getParameter("save") != null && request.getParameter("save").equalsIgnoreCase("true")) {
			cookieGenerator.addCookie(httpResponse, request.getParameter("size"));
			chain.doFilter(new ModifiedRequest(request, request.getParameter("size")), response);
		} else {
			chain.doFilter(new ModifiedRequest(request, null), response);
		}

	}

	public void destroy() {
	}

	public void init(FilterConfig filterConfig) {
	}
}
