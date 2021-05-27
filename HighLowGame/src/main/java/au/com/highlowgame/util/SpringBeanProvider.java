package au.com.highlowgame.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanProvider {

	@Autowired
	private ApplicationContext ctx;

	@SuppressWarnings("rawtypes")
	public <T> T getNewBeanOfType(final Class<T> clazz) throws UnsupportedOperationException, BeansException {
		Map beansOfType = ctx.getBeansOfType(clazz);
		final int size = beansOfType.size();
		switch (size) {
		case 0:
			throw new UnsupportedOperationException("No bean found of type" + clazz);
		case 1:
			String name = (String) beansOfType.keySet().iterator().next();
			return clazz.cast(ctx.getBean(name, clazz));
		default:
			throw new UnsupportedOperationException("Ambigious beans found of type" + clazz);
		}
	}

}
