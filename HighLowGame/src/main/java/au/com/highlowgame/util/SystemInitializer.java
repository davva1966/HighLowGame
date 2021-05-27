package au.com.highlowgame.util;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import au.com.highlowgame.model.Player;

public class SystemInitializer implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = Logger.getLogger(this.getClass());
	private boolean executed = false;

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
		} catch (Exception e) {
			logger.error("SystemInitializer error. Error: " + e.toString(), e);
		}

		executed = true;

	}

}
