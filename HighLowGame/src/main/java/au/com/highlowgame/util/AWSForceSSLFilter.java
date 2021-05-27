package au.com.highlowgame.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import au.com.highlowgame.util.SSUtil;

public class AWSForceSSLFilter extends GenericFilterBean {

	protected boolean forceSSL = false;

	public AWSForceSSLFilter() {
		super();
	}

	public AWSForceSSLFilter(String forceSSLString) {
		super();
		if (SSUtil.notEmpty(forceSSLString)) {
			try {
				forceSSL = Boolean.parseBoolean(forceSSLString);
			} catch (Exception e) {
			}
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (forceSSL) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			String checkHeader = httpRequest.getHeader("X-Forwarded-Proto");

			boolean performRedirect = false;

			if (SSUtil.notEmpty(checkHeader))
				performRedirect = SSUtil.notEmpty(checkHeader) && checkHeader.equalsIgnoreCase("http");
			else
				performRedirect = httpRequest.isSecure() == false;

			// if the scheme is not https
			if (performRedirect) {
				// generate full URL to https
				StringBuilder newUrl = new StringBuilder("https://");
				newUrl.append(request.getServerName());
				if (httpRequest.getRequestURI() != null) {
					newUrl.append(httpRequest.getRequestURI());
				}
				if (httpRequest.getQueryString() != null) {
					newUrl.append("?").append(httpRequest.getQueryString());
				}

				httpResponse.sendRedirect(newUrl.toString());
				return;
			}
		}

		// already a secure connection or no force needed, no redirect to https required.
		if (chain != null) {
			chain.doFilter(request, response);
		}

	}

}
