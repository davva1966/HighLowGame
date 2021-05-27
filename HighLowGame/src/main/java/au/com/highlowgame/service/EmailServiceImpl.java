package au.com.highlowgame.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import au.com.highlowgame.util.AWSEmailAsyncResponseHandler;
import au.com.highlowgame.util.SSUtil;
import au.com.highlowgame.util.SpringBeanProvider;
import au.com.highlowgame.util.TextEmailMessage;

public class EmailServiceImpl implements EmailService {

	@Autowired
	AWSMailSender mailSender;

	@Autowired
	SpringBeanProvider beanProvider;

	protected String defaultEmailSender;

	public String getDefaultEmailSender() {
		return defaultEmailSender;
	}

	public void setDefaultEmailSender(String defaultEmailSender) {
		this.defaultEmailSender = defaultEmailSender;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
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

		TextEmailMessage message = TextEmailMessage.create(finalSubject, finalText);

		message.getTo().addAll(cleanedEmails);
		message.setReplyTo(replyTo);
		message.setFromFriendlyName("SpeedInvoice");

		AWSEmailAsyncResponseHandler asyncHandler = beanProvider.getNewBeanOfType(AWSEmailAsyncResponseHandler.class);
		asyncHandler.setMessage(message);

		mailSender.sendAsync(message, asyncHandler);

	}

}
