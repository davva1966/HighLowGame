package au.com.highlowgame.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import au.com.highlowgame.util.AppUtil;
import au.com.highlowgame.util.SSUtil;

public enum GameType {

	LEADER("game_type_leader", "game_type_leader_description"),
	ALLPLAYERS("game_type_all_players", "game_type_all_players_description");

	public final String name;
	public final String description;

	GameType(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getTranslatedName() {
		return AppUtil.translate(name);
	}

	public String getTranslatedDescription() {
		return AppUtil.translate(description);
	}

	public static List<GameType> getAll() {
		return java.util.Arrays.asList(GameType.class.getEnumConstants());
	}

	public static List<GameType> getAllExcept(GameType... types) {
		if (types == null || types.length == 0)
			return getAll();
		LinkedList<GameType> list = new LinkedList<>();
		list.addAll(getAll());
		list.removeAll(Arrays.asList(types));
		return list;
	}

	public static GameType getForTranslatedName(String name) {

		if (SSUtil.empty(name))
			return null;

		for (GameType type : getAll()) {
			if (type.getTranslatedName().trim().equalsIgnoreCase(name.trim()))
				return type;
		}

		return null;
	}

	public String getEnumConstant() {
		return name();
	}

	public String getLabel() {
		return getTranslatedName() + " - " + getTranslatedDescription();
	}

}
