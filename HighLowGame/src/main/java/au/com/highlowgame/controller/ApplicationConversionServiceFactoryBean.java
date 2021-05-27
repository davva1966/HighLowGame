package au.com.highlowgame.controller;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipant;
import au.com.highlowgame.model.GameParticipantAnswer;
import au.com.highlowgame.model.Player;
import au.com.highlowgame.model.Question;

@Configurable
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	public Converter<Game, String> getGameToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<au.com.highlowgame.model.Game, java.lang.String>() {
			public String convert(Game game) {
				return game.getId();
			}
		};
	}

	public Converter<GameParticipant, String> getGameParticipantToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<au.com.highlowgame.model.GameParticipant, java.lang.String>() {
			public String convert(GameParticipant gameParticipant) {
				return gameParticipant.getId();
			}
		};
	}

	public Converter<GameParticipantAnswer, String> getGameParticipantAnswerToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<au.com.highlowgame.model.GameParticipantAnswer, java.lang.String>() {
			public String convert(GameParticipantAnswer gameParticipantAnswer) {
				return gameParticipantAnswer.getId();
			}
		};
	}

	public Converter<Player, String> getPlayerToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<au.com.highlowgame.model.Player, java.lang.String>() {
			public String convert(Player player) {
				return player.getId();
			}
		};
	}

	public Converter<Question, String> getQuestionToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<au.com.highlowgame.model.Question, java.lang.String>() {
			public String convert(Question question) {
				return question.getId();
			}
		};
	}

	public Converter<String, Game> getGameIdToGameConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, au.com.highlowgame.model.Game>() {
			public au.com.highlowgame.model.Game convert(java.lang.String id) {
				return Game.find(id);
			}
		};
	}

	public Converter<String, GameParticipant> getGameParticipantIdToGameParticipantConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, au.com.highlowgame.model.GameParticipant>() {
			public au.com.highlowgame.model.GameParticipant convert(java.lang.String id) {
				return GameParticipant.find(id);
			}
		};
	}

	public Converter<String, GameParticipantAnswer> getGameParticipantAnswerIdToGameParticipantAnswerConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, au.com.highlowgame.model.GameParticipantAnswer>() {
			public au.com.highlowgame.model.GameParticipantAnswer convert(java.lang.String id) {
				return GameParticipantAnswer.find(id);
			}
		};
	}

	public Converter<String, Player> getPlayerIdToPlayerConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, au.com.highlowgame.model.Player>() {
			public au.com.highlowgame.model.Player convert(java.lang.String id) {
				return Player.find(id);
			}
		};
	}

	public Converter<String, Question> getQuestionIdToQuestionConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, au.com.highlowgame.model.Question>() {
			public au.com.highlowgame.model.Question convert(java.lang.String id) {
				return Question.find(id);
			}
		};
	}

	public void installLabelConverters(FormatterRegistry registry) {
		registry.addConverter(getGameToStringConverter());
		registry.addConverter(getGameParticipantToStringConverter());
		registry.addConverter(getGameParticipantAnswerToStringConverter());
		registry.addConverter(getPlayerToStringConverter());
		registry.addConverter(getQuestionToStringConverter());

		registry.addConverter(getGameIdToGameConverter());
		registry.addConverter(getGameParticipantIdToGameParticipantConverter());
		registry.addConverter(getGameParticipantAnswerIdToGameParticipantAnswerConverter());
		registry.addConverter(getPlayerIdToPlayerConverter());
		registry.addConverter(getQuestionIdToQuestionConverter());

	}

	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		installLabelConverters(getObject());
	}
}
