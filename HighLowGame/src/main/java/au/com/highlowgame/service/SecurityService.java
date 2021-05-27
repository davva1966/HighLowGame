package au.com.highlowgame.service;

import org.springframework.stereotype.Service;

import au.com.highlowgame.model.Player;

@Service("securityService")
public interface SecurityService {

	void updatePassword(String clearTextPassword);

	void updatePassword(Player player, String clearTextPassword);

	String encodePassword(String clearTextPassword);

	boolean isCurrentPlayer(Player currentPlayer);

	boolean passwordMatch(String clearTextPassword, String encodedPassword);
	
	void sendNewPassword(Player player);

}
