package au.com.highlowgame.util;

import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public class SSWebSecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {

	private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
		SSSecurityExpressionRoot root = new SSSecurityExpressionRoot(authentication, fi);
		root.setDefaultRolePrefix(null);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}
	
}