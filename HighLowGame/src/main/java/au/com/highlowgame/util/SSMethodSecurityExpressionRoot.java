package au.com.highlowgame.util;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class SSMethodSecurityExpressionRoot extends SSSecurityExpressionRoot implements MethodSecurityExpressionOperations {
	private Object filterObject;
	private Object returnObject;
	private Object target;

	public SSMethodSecurityExpressionRoot(Authentication a) {
		super(a);
	}

	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	public Object getFilterObject() {
		return filterObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public Object getReturnObject() {
		return returnObject;
	}

	/**
	 * Sets the "this" property for use in expressions. Typically this will be
	 * the "this" property of the {@code JoinPoint} representing the method
	 * invocation which is being protected.
	 * 
	 * @param target
	 *            the target object on which the method in is being invoked.
	 */
	void setThis(Object target) {
		this.target = target;
	}

	public Object getThis() {
		return target;
	}
}
