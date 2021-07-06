package au.com.highlowgame.model.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipantAnswer;
import au.com.highlowgame.model.GameType;
import au.com.highlowgame.model.Question;
import au.com.highlowgame.util.SSUtil;

@Component
public class GameValidator extends AbstractCompositeValidator {

	public GameValidator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Game.class.equals(clazz) || Question.class.equals(clazz) || GameParticipantAnswer.class.equals(clazz);
	}

	public void validateCreate(Object obj, Errors e) {
		if (obj instanceof Game) {
			Game game = (Game) obj;
			if (SSUtil.empty(game.getDescription()))
				e.rejectValue("description", "message_com_ss_highlowgame_descriptioncannotbeempty");
			if (game.getStartingPoints() < 10)
				e.rejectValue("startingPoints", "message_com_ss_highlowgame_invalidstartingpoints");
			if (game.getGameType() == GameType.ALLPLAYERS && game.getQuestionsPerPlayer() == 0)
				e.rejectValue("questionsPerPlayer", "message_com_ss_highlowgame_questionsperplayermustbeentered");
			else if (game.getGameType() == GameType.LEADER && game.getGameLeader() == null)
				e.rejectValue("gameLeader", "message_com_ss_highlowgame_gameleadermustbeselected");
		} else if (obj instanceof Question) {
			Question question = (Question) obj;
			if (SSUtil.empty(question.getQuestion()))
				e.rejectValue("question", "message_com_ss_highlowgame_questioncannotbeempty");
			if (question.getCorrectAnswer() == null)
				e.rejectValue("correctAnswer", "message_com_ss_highlowgame_correctanswercannotbeempty");
		} else if (obj instanceof GameParticipantAnswer) {
			GameParticipantAnswer answer = (GameParticipantAnswer) obj;
			if (answer.isCurrentPlayerAnsweringFirst()) {
				if (answer.getAnswer() == null)
					e.rejectValue("answer", "message_com_ss_highlowgame_answercannotbeempty");
			} else {
				if (answer.getHighLowAnswer() == null)
					e.rejectValue("highLowAnswer", "message_com_ss_highlowgame_highlowanswercannotbeempty");
				if (answer.getPointsBet() > answer.getPointsBefore() / 2)
					e.rejectValue("correctAnswer", "message_com_ss_highlowgame_maxhalfcanbebet");
			}
		}

	}

	public void validateUpdate(Object obj, Errors e) {
		validateCreate(obj, e);

	}

}
