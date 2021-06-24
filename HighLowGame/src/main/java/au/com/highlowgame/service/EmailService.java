package au.com.highlowgame.service;

import org.springframework.stereotype.Service;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipant;

@Service("emailService")
public interface EmailService {

	void sendMail(String to, String replyTo, String subject, String text) throws Exception;
	
	void sendGameInvitation(Game game, GameParticipant participant) throws Exception;

}
