package au.com.highlowgame.util;

import org.apache.commons.beanutils.PropertyUtils;

import au.com.highlowgame.util.SSUtil;

public class TagUtil {

	public static boolean instanceOf(Object o, String className) {
		if (o == null || className == null)
			return false;

		try {
			return Class.forName(className, false, Thread.currentThread().getContextClassLoader()).isInstance(o);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean hasProperty(Object o, String propertyName) {
		if (o == null || propertyName == null)
			return false;

		try {
			return PropertyUtils.getPropertyDescriptor(o, propertyName) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static String propertyColor(Object o, String propertyName) {
		if (o == null || SSUtil.empty(propertyName))
			return null;

		return null;
	}

}