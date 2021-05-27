package au.com.highlowgame.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import au.com.highlowgame.model.Player;

public class UserContextService {

	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

	public static String getCurrentPlayerName() {
		try {
			return (String) SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception e) {
		}

		return null;
	}

	public static ExtendedUser getCurrentUser() {
		try {
			return (ExtendedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
		}

		return null;
	}

	public static Player getCurrentPlayer() {
		initContext();
		Player player = (Player) context.get().get("player");
		if (player != null)
			return player;
		ExtendedUser extUser = getCurrentUser();
		if (extUser == null)
			return null;
		return Player.findPlayerForEmail(extUser.getUsername());

	}

	public static void setCurrentPlayer(Player player) {
		initContext();
		context.get().put("player", player);
	}

	public static void setUsername(String username) {
		initContext();
		context.get().put("username", username);
	}

	public static String getUsername() {
		initContext();
		return (String) context.get().get("username");

	}

	private static void initContext() {
		if (context.get() == null)
			context.set(new HashMap<String, Object>());
	}
}
