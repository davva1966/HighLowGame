package au.com.highlowgame.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipant;
import au.com.highlowgame.util.SSGmailTextEmailMessage;
import au.com.highlowgame.util.SSUtil;
import au.com.highlowgame.util.SpringBeanProvider;

public class EmailServiceImpl implements EmailService {

	@Autowired
	GmailMailSender mailSender;

	@Autowired
	SpringBeanProvider beanProvider;

	@Autowired
	TranslationService translationService;

	@Override
	public void sendMail(final String to, final String replyTo, String subject, String text) throws Exception {

		final String finalSubject = subject;
		final String finalText = text;

		// Clean email
		String[] emailAddresses = SSUtil.separateEmails(to);
		List<String> cleanedEmails = new ArrayList<String>();
		for (String email : emailAddresses) {
			String cleanedEmail = SSUtil.cleanEmail(email);
			List<String> emails = SSUtil.extractEmails(cleanedEmail);
			if (emails != null && emails.size() > 0)
				cleanedEmails.add(emails.get(0));
			else
				cleanedEmails.add(email);
		}

		SSGmailTextEmailMessage message = new SSGmailTextEmailMessage(finalSubject, finalText);

		message.getTo().addAll(cleanedEmails);
		message.setReplyTo(replyTo);
		message.setFromFriendlyName("HighLowGame");

		mailSender.send(message);

	}

	@Override
	public void sendGameInvitation(Game game, GameParticipant participant) throws Exception {

		String joinLink = game.getJoinLink();
		String declineLink = game.getDeclineLink(participant);

		final String finalSubject = translationService.translate("game_invitation_email_subject");
		final String finalText = translationService.translate("game_invitation_email_text", joinLink, declineLink);

		// Clean email
		String cleanedEmail = SSUtil.cleanEmail(participant.getPlayer().getEmail());

		SSGmailTextEmailMessage message = new SSGmailTextEmailMessage(finalSubject, finalText);

		message.getTo().add(cleanedEmail);
		message.setFromFriendlyName("HighLowGame");

		mailSender.send(message);

	}

}
