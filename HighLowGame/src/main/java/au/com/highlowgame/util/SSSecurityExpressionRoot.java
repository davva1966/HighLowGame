package au.com.highlowgame.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

public class SSSecurityExpressionRoot extends SecurityExpressionRoot {

	public HttpServletRequest request;

	public SSSecurityExpressionRoot(Authentication a) {
		super(a);
	}

	public SSSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a);
		this.request = fi.getRequest();
	}

	public boolean hasIpAddress(String ipAddress) {
		return (new IpAddressMatcher(ipAddress).matches(request));
	}

	public final boolean hasSSAuthority(String authority) {
		return hasRole(authority);
	}

	public final boolean hasSSAnyAuthority(String... authorities) {
		return hasAnyRole(authorities);
	}

}