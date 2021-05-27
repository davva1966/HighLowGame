package au.com.highlowgame.model.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import au.com.highlowgame.model.Player;

@Component
public class PlayerValidator extends AbstractCompositeValidator {

	public PlayerValidator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Player.class.equals(clazz);
	}

	public void validateCreate(Object obj, Errors e) {
		Player player = (Player) obj;
		if (player.getEmail() != null)
			validationService.rejectIfNotUnique(player, "email", e);

	}

	public void validateUpdate(Object obj, Errors e) {
		validateCreate(obj, e);

	}

}
