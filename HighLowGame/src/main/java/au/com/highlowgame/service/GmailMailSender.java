package au.com.highlowgame.service;

import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import au.com.highlowgame.util.SSGmailTextEmailMessage;
import au.com.highlowgame.util.SSUtil;

/**
 * Class to send emails using Gmail
 */
public class GmailMailSender {

	public static final Logger LOGGER = Logger.getLogger(GmailMailSender.class.getName());

	protected String username;
	protected String password;
	protected String defaultEmailSender;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	protected String getDecodedPassword() {
		return new String(Base64.getDecoder().decode(getPassword()));
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefaultEmailSender() {
		return defaultEmailSender;
	}

	public void setDefaultEmailSender(String defaultEmailSender) {
		this.defaultEmailSender = defaultEmailSender;
	}

	public void send(SSGmailTextEmailMessage m) throws MessagingException {
		sendTextEmail(m);
	}

	private void sendTextEmail(SSGmailTextEmailMessage m) throws MessagingException {

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getUsername(), getDecodedPassword());
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(getDefaultEmailSender()));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SSUtil.formatListToSeparated(m.getTo(), ",")));
		message.setSubject(m.getSubject());
		message.setText(m.getText());

		Transport.send(message);

	}

}
