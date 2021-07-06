package au.com.highlowgame.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import au.com.highlowgame.util.AppUtil;
import au.com.highlowgame.util.SSUtil;

public enum Answer {

	HIGHER("answer_higher", "answer_higher_description"),
	LOWER("answer_lower", "answer_lower_description"),
	RIGHT("answer_right", "answer_right_description");

	public final String name;
	public final String description;

	Answer(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getTranslatedName() {
		return AppUtil.translate(name);
	}

	public String getTranslatedDescription() {
		return AppUtil.translate(description);
	}

	public static List<Answer> getAll() {
		return java.util.Arrays.asList(Answer.class.getEnumConstants());
	}

	public static List<Answer> getAllExcept(Answer... statuses) {
		if (statuses == null || statuses.length == 0)
			return getAll();
		LinkedList<Answer> list = new LinkedList<>();
		list.addAll(getAll());
		list.removeAll(Arrays.asList(statuses));
		return list;
	}

	public static Answer getForTranslatedName(String name) {

		if (SSUtil.empty(name))
			return null;

		for (Answer type : getAll()) {
			if (type.getTranslatedName().trim().equalsIgnoreCase(name.trim()))
				return type;
		}

		return null;
	}

	public String getEnumConstant() {
		return name();
	}

}
