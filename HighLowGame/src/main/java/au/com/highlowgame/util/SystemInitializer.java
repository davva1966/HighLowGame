package au.com.highlowgame.util;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import au.com.highlowgame.model.Player;

public class SystemInitializer implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = Logger.getLogger(this.getClass());
	private boolean executed = false;

	private String backendServer = "";
	private int backendPort = 0;
	private String backendDomain = "";

	public String getBackendServer() {
		return backendServer;
	}

	public void setBackendServer(String backendServer) {
		this.backendServer = backendServer;
	}

	public int getBackendPort() {
		return backendPort;
	}

	public void setBackendPort(int backendPort) {
		this.backendPort = backendPort;
	}

	public String getBackendDomain() {
		return backendDomain;
	}

	public void setBackendDomain(String backendDomain) {
		this.backendDomain = backendDomain;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (executed)
			return;
		try {
			if (Player.countPlayers() == 0) {
				Player player = new Player();
				player.setName("David");
				player.setEmail("davva1966@gmail.com");
				player.setPassword("davva");
				player.setAdmin(true);
				player.persist();
			}

			Application.setBackendServer(getBackendServer());
			Application.setBackendPort(getBackendPort());
			Application.setBackendDomain(getBackendDomain());

		} catch (Exception e) {
			logger.error("SystemInitializer error. Error: " + e.toString(), e);
		}

		executed = true;

	}

}
