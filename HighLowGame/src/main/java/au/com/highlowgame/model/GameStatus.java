package au.com.highlowgame.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import au.com.highlowgame.util.AppUtil;
import au.com.highlowgame.util.SSUtil;

public enum GameStatus {

	CREATED("game_status_created", "game_status_created_description"),
	ACTIVE("game_status_active", "game_status_active_description"),
	FINISHED("game_status_finished", "game_status_finished_description");

	public final String name;
	public final String description;

	GameStatus(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getTranslatedName() {
		return AppUtil.translate(name);
	}

	public String getTranslatedDescription() {
		return AppUtil.translate(description);
	}

	public static List<GameStatus> getAll() {
		return java.util.Arrays.asList(GameStatus.class.getEnumConstants());
	}

	public static List<GameStatus> getAllExcept(GameStatus... statuses) {
		if (statuses == null || statuses.length == 0)
			return getAll();
		LinkedList<GameStatus> list = new LinkedList<>();
		list.addAll(getAll());
		list.removeAll(Arrays.asList(statuses));
		return list;
	}

	public static GameStatus getForTranslatedName(String name) {

		if (SSUtil.empty(name))
			return null;

		for (GameStatus type : getAll()) {
			if (type.getTranslatedName().trim().equalsIgnoreCase(name.trim()))
				return type;
		}

		return null;
	}

	public String getEnumConstant() {
		return name();
	}

}
