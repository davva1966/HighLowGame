package au.com.highlowgame.model.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameType;
import au.com.highlowgame.util.SSUtil;

@Component
public class GameValidator extends AbstractCompositeValidator {

	public GameValidator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Game.class.equals(clazz);
	}

	public void validateCreate(Object obj, Errors e) {
		Game game = (Game) obj;
		if (SSUtil.empty(game.getDescription()))
			e.rejectValue("description", "message_com_ss_speedinvoice_game_descriptioncannotbeempty");
		if (game.getStartingPoints() < 10)
			e.rejectValue("startingPoints", "message_com_ss_speedinvoice_game_invalidstartingpoints");
		if (game.getGameType() == GameType.ALLPLAYERS && game.getQuestionsPerPlayer() == 0)
			e.rejectValue("questionsPerPlayer", "message_com_ss_speedinvoice_game_questionsperplayermustbeentered");
		else if (game.getGameType() == GameType.LEADER && game.getGameLeader() == null)
			e.rejectValue("gameLeader", "message_com_ss_speedinvoice_game_gameleadermustbeselected");

	}

	public void validateUpdate(Object obj, Errors e) {
		validateCreate(obj, e);

	}

}
