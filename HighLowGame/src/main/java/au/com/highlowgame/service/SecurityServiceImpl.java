package au.com.highlowgame.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import au.com.highlowgame.model.Player;
import au.com.highlowgame.user.UserContextService;
import au.com.highlowgame.util.PasswordGenerator;

public class SecurityServiceImpl implements SecurityService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private TranslationService translationService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	EmailService emailService;

	@Override
	public void updatePassword(String clearTextPassword) {
		updatePassword(null, clearTextPassword);
	}

	@Override
	public void updatePassword(Player player, String clearTextPassword) {
		if (player == null)
			player = UserContextService.getCurrentPlayer();
		if (player != null) {
			String encodedPassword = encodePassword(clearTextPassword);
			player.setPassword(encodedPassword);
			player.merge();
			logger.info("Password was changed for player: " + player);
		}

	}

	@Override
	public boolean isCurrentPlayer(Player currentPlayer) {
		Player loggedInPlayer = UserContextService.getCurrentPlayer();
		return (loggedInPlayer.equals(currentPlayer));
	}

	@Override
	public String encodePassword(String clearTextPassword) {
		return passwordEncoder.encode(clearTextPassword);
	}

	@Override
	public boolean passwordMatch(String clearTextPassword, String encodedPassword) {
		return passwordEncoder.matches(clearTextPassword, encodedPassword);
	}

	@Override
	public void sendNewPassword(Player player) {
		String newPassword = PasswordGenerator.getPassword(4);
		player.setPassword(encodePassword(newPassword));
		player.merge();
		String subject = translationService.translate("player_forgot_password_subject", "SpeedSolutions Backoffice");
		String body = translationService.translate("player_forgot_password_body", newPassword);

		try {
			emailService.sendMail(player.getEmail(), null, subject, body);
		} catch (Exception e) {
			logger.warn("Send new password email failed. Error: " + e.toString());
		}

	}

}
