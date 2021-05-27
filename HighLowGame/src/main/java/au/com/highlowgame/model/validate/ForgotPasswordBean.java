package au.com.highlowgame.model.validate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import au.com.highlowgame.model.Player;
import au.com.highlowgame.util.SSUtil;

public class ForgotPasswordBean {

	@NotNull
	@Size(max = 60)
	private String email;

	public ForgotPasswordBean() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = SSUtil.cleanEmail(email);
	}

	public Player getPlayer() {
		if (getEmail() != null && getEmail().trim().length() > 0)
			return Player.findPlayerForEmail(getEmail());

		return null;
	}
}
