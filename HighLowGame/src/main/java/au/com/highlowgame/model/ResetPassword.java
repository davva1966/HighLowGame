package au.com.highlowgame.model;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPassword {

	@NotNull
	private Player player;

	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String newPassword;

	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String newPasswordConfirmation;

	@Transient
	private transient String playerId;

	public String getPlayerId() {
		if (playerId != null)
			return playerId;
		if (getPlayer() != null)
			return getPlayer().getId();
		return null;
	}

	public void setPlayerId(String id) {
		Player player = Player.find(id);
		if (player != null)
			setPlayer(player);
		else
			playerId = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getNewPassword() {
		return this.newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return this.newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

}
