package au.com.highlowgame.util;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class SSMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

	Logger LOGGER = Logger.getLogger(SSMethodSecurityExpressionHandler.class);

	public SSMethodSecurityExpressionHandler() {
		super();
	}

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
		SSMethodSecurityExpressionRoot root = new SSMethodSecurityExpressionRoot(authentication);
		root.setPermissionEvaluator(getPermissionEvaluator());

		return root;
	}

}
