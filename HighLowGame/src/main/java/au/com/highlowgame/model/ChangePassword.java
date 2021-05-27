package au.com.highlowgame.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePassword {

	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String password;

	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String newPassword;

	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String newPasswordConfirmation;

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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
