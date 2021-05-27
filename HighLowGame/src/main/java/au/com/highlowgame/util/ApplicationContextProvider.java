package au.com.highlowgame.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
	private static ApplicationContext appContext;

	@Override
	public void setApplicationContext(ApplicationContext globalAppContext) throws BeansException {
		ApplicationContextProvider.appContext = globalAppContext;
	}

	public static ApplicationContext getApplicationContext() {
		return appContext;
	}
}
