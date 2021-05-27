package au.com.highlowgame.util;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

public class SSWebAppInitialiser implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		// Copy environment variables to system properties
		Map<String, String> envVarMap = System.getenv();
		if (envVarMap != null) {
			for (String key : envVarMap.keySet()) {
				System.setProperty(key, envVarMap.get(key));
			}
		}

	}

}
