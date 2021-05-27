package au.com.highlowgame.util;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;

/**
 * Class containing a text only email message to be sent using Amazon SES.
 */
public class AWSTextEmailMessage extends TextEmailMessage<Message> {

	/**
	 * Constructor.
	 */
	public AWSTextEmailMessage() {
		super();
	}

	/**
	 * Constructor with email subject.
	 * 
	 * @param subject
	 *            subject to be set on email.
	 */
	public AWSTextEmailMessage(String subject) {
		super(subject);
	}

	/**
	 * Constructor with email subject and content text.
	 * 
	 * @param subject
	 *            subject to be set on email.
	 * @param text
	 *            textual email content.
	 */
	public AWSTextEmailMessage(String subject, String text) {
		super(subject, text);
	}

	/**
	 * Builds email content to be sent using an email sender.
	 * 
	 * @param message
	 *            instance where content must be set.
	 * @throws EmailException
	 *             if setting mail content fails.
	 */
	@Override
	public void buildContent(Message message) throws EmailException {
		Destination destination = new Destination(getTo());
		if (getBCC() != null && !getBCC().isEmpty()) {
			destination.setBccAddresses(getBCC());
		}
		if (getCC() != null && !getCC().isEmpty()) {
			destination.setCcAddresses(getCC());
		}

		if (getSubject() != null) {
			Content subject = new Content(getSubject());
			// set utf-8 enconding to support all languages
			subject.setCharset("UTF-8");
			message.setSubject(subject);
		}

		if (getText() != null) {
			Body body = new Body();
			Content content = new Content(getText());
			// set utf-8 enconding to support all languages
			content.setCharset("UTF-8");

			body.setText(content);
			message.setBody(body);
		}
	}
}
