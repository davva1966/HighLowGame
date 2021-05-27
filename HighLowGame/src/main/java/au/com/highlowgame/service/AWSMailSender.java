package au.com.highlowgame.service;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

import au.com.highlowgame.util.AWSEmailSenderCredentials;
import au.com.highlowgame.util.EmailMessage;
import au.com.highlowgame.util.MailNotSentException;
import au.com.highlowgame.util.SSUtil;

/**
 * Class to send emails using Amazon Web Services (AWS) SES.
 */
public class AWSMailSender {

	/**
	 * Logger for this class.
	 */
	public static final Logger LOGGER = Logger.getLogger(AWSMailSender.class.getName());

	/**
	 * AWS SES credentials.
	 */
	private AWSEmailSenderCredentials mCredentials;

	/**
	 * Email address to be used as sender.
	 */
	private String mMailFromAddress;

	/**
	 * Internal client to be used for mail sending.
	 */
	private AmazonSimpleEmailService mClient;

	/**
	 * Internal client to be used for async mail sending.
	 */
	private AmazonSimpleEmailServiceAsync mClientAsync;

	private String region;

	public AWSMailSender() {

	}

	public AWSEmailSenderCredentials getCredentials() {
		return mCredentials;
	}

	public void setCredentials(AWSEmailSenderCredentials credentials) {
		this.mCredentials = credentials;
	}

	public String getMailFromAddress() {
		return mMailFromAddress;
	}

	public void setMailFromAddress(String mailFrom) {
		this.mMailFromAddress = mailFrom;
	}

	public String getRegion() {
		if (SSUtil.empty(region))
			region = Regions.US_EAST_1.getName();
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void send(EmailMessage m) throws MailNotSentException {
		sendTextEmail(m, false, null);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendAsync(EmailMessage m, AsyncHandler asyncHandler) throws MailNotSentException {
		sendTextEmail(m, true, asyncHandler);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void sendTextEmail(EmailMessage m, boolean async, AsyncHandler asyncHandler) throws MailNotSentException {
		prepareClient();

		try {
			synchronized (this) {
				Destination destination = new Destination(m.getTo());
				if (m.getBCC() != null && !m.getBCC().isEmpty()) {
					destination.setBccAddresses(m.getBCC());
				}
				if (m.getCC() != null && !m.getCC().isEmpty()) {
					destination.setCcAddresses(m.getCC());
				}

				// if no subject, set to empty string to avoid errors
				if (m.getSubject() == null) {
					m.setSubject("");
				}

				Message message = new Message();
				m.buildContent(message);

				SendEmailRequest request = new SendEmailRequest(mMailFromAddress, destination, message);

				if (async) {
					if (asyncHandler == null)
						mClientAsync.sendEmailAsync(request);
					else
						mClientAsync.sendEmailAsync(request, asyncHandler);
				} else {
					mClient.sendEmail(new SendEmailRequest(mMailFromAddress, destination, message));
				}

			}
		} catch (Throwable t) {
			throw new MailNotSentException(t);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void sendRawEmail(EmailMessage<MimeMessage> m, boolean async, AsyncHandler asyncHandler) throws MailNotSentException {

		prepareClient();

		try {
			synchronized (this) {

				// if no subject, set to empty string to avoid errors
				if (m.getSubject() == null) {
					m.setSubject("");
				}

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				// convert message into mime multi part and write it to output stream
				Session session = Session.getInstance(new Properties());
				MimeMessage mimeMessage = new MimeMessage(session);
				if (m.getSubject() != null) {
					mimeMessage.setSubject(m.getSubject(), "utf-8");
				}

				if (SSUtil.notEmpty(mMailFromAddress)) {
					try {
						mimeMessage.setFrom(new InternetAddress(mMailFromAddress, m.getFromFriendlyName(), "utf-8"));
					} catch (Exception e) {
						mimeMessage.setFrom(new InternetAddress(mMailFromAddress));

					}
				}

				if (SSUtil.notEmpty(m.getReplyTo())) {
					try {
						InternetAddress replyTo = new InternetAddress(m.getReplyTo(), m.getFromFriendlyName(), "utf-8");
						mimeMessage.setReplyTo(new InternetAddress[] { replyTo });
					} catch (Exception e) {
						InternetAddress replyTo = new InternetAddress(m.getReplyTo());
						mimeMessage.setReplyTo(new InternetAddress[] { replyTo });
					}
				}

				// To
				if (SSUtil.notEmpty(m.getTo())) {
					InternetAddress[] toAddresses = InternetAddress.parse(SSUtil.formatListToSeparated(m.getTo(), ","));
					mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, toAddresses);
				}

				// CC
				if (SSUtil.notEmpty(m.getCC())) {
					InternetAddress[] ccAddresses = InternetAddress.parse(SSUtil.formatListToSeparated(m.getCC(), ","));
					mimeMessage.addRecipients(javax.mail.Message.RecipientType.CC, ccAddresses);
				}

				// BCC
				if (SSUtil.notEmpty(m.getBCC())) {
					InternetAddress[] bccAddresses = InternetAddress.parse(SSUtil.formatListToSeparated(m.getBCC(), ","));
					mimeMessage.addRecipients(javax.mail.Message.RecipientType.BCC, bccAddresses);
				}

				m.buildContent(mimeMessage);
				mimeMessage.writeTo(outputStream);

				RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

				SendRawEmailRequest rawRequest = new SendRawEmailRequest(rawMessage);
				rawRequest.setSource(mMailFromAddress);

				if (async) {
					if (asyncHandler == null)
						mClientAsync.sendRawEmailAsync(rawRequest);
					else
						mClientAsync.sendRawEmailAsync(rawRequest, asyncHandler);
				} else {
					mClient.sendRawEmail(rawRequest);
				}

			}
		} catch (Throwable t) {
			throw new MailNotSentException(t);
		}

	}

	/**
	 * Prepares client by setting proper credentials.
	 */
	private synchronized void prepareClient() {

		if (mClient == null || mClientAsync == null) {
			if (areValidCredentials() == false) {
				Logger.getLogger(AWSMailSender.class.getName()).log(Level.SEVERE, "Invalid AWS Email credentials");
				return;
			}

			ClientConfiguration clientConfig = new ClientConfiguration();
			clientConfig.setConnectionTimeout(60000);
			clientConfig.setSocketTimeout(60000);
			clientConfig.setMaxConnections(50);

			if (mClient == null) {
				AWSCredentials awsCredentials = new BasicAWSCredentials(mCredentials.getAccessKey(), mCredentials.getSecretKey());
				AWSStaticCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
				mClient = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(getRegion()).withClientConfiguration(clientConfig).build();
			}

			if (mClientAsync == null) {
				AWSCredentials awsCredentials = new BasicAWSCredentials(mCredentials.getAccessKey(), mCredentials.getSecretKey());
				AWSStaticCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
				mClientAsync = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(getRegion()).withClientConfiguration(clientConfig).build();

			}

		}
	}

	private boolean areValidCredentials() {
		if (mCredentials != null) {
			return mCredentials.isReady();
		} else {
			return false;
		}
	}
}
