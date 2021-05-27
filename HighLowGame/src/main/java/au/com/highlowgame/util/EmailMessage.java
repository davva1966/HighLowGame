package au.com.highlowgame.util;

import java.util.LinkedList;
import java.util.List;


/**
 * Abstract base class defining basic Email message data.
 */
public abstract class EmailMessage<E> {

	/**
	 * List of addresses to send an email to.
	 */
	private List<String> mTo;

	/**
	 * List of Carbon Copy addresses to send a copy of an email.
	 */
	private List<String> mCc;

	/**
	 * List of Blind Carbon Copy addresses to send a copy of an email. Other recipients of the email won't be able to see addresses in this list.
	 */
	private List<String> mBcc;

	private String fromFriendlyName;

	private String replyTo;

	/**
	 * Subject of an email.
	 */
	private String mSubject;

	/**
	 * Constructor. Creates an email with no content, subject and no recipients.
	 */
	public EmailMessage() {
		mTo = new LinkedList<String>();
		mCc = new LinkedList<String>();
		mBcc = new LinkedList<String>();
		mSubject = "";
	}

	/**
	 * Constructor. Creates an email with subject, no content and no recipients.
	 * 
	 * @param subject
	 *            subject to be set on the email.
	 */
	public EmailMessage(String subject) {
		mTo = new LinkedList<String>();
		mCc = new LinkedList<String>();
		mBcc = new LinkedList<String>();

		if (subject != null) {
			mSubject = subject;
		} else {
			mSubject = "";
		}
	}

	public List<String> getTo() {
		return mTo;
	}

	/**
	 * Indicates if To is supported to send an email to multiple recipients.
	 * 
	 * @return true if To feature is supported, false otherwise.
	 */
	public boolean isToSupported() {
		return true;
	}

	/**
	 * Returns list of Carbon Copy addresses to send a copy of this email.
	 * 
	 * @return list of Carbon Copy addresses to send a copy of this email.
	 * @throws NotSupportedException
	 *             if this feature is not supported.
	 */
	public List<String> getCC() throws NotSupportedException {
		if (!isCCSupported()) {
			throw new NotSupportedException();
		}
		return mCc;
	}

	/**
	 * Indicates if CC is supported for this kind of email to send Carbon Copies of this email. Some email types do not support this feature.
	 * 
	 * @return true if CC feature is supported, false otherwise.
	 */
	public boolean isCCSupported() {
		return true;
	}

	/**
	 * Returns list of Blind Carbon Copy addresses to send a copy of this email. Other recipients of this email won't be able to see addresses in this list.
	 * 
	 * @return list of Blind Carbon Copy addresses to send a copy of this email.
	 * @throws NotSupportedException
	 *             if this feature is not supported.
	 */
	public List<String> getBCC() throws NotSupportedException {
		if (!isBCCSupported()) {
			throw new NotSupportedException();
		}
		return mBcc;
	}

	/**
	 * Indicates if BCC is supported for this kind of email to send Blind Carbon.Copies of this email. Some email types do not support this feature.
	 * 
	 * @return true if BCC feature is supported, false otherwise.
	 */
	public boolean isBCCSupported() {
		return true;
	}

	public String getFromFriendlyName() {
		return fromFriendlyName;
	}

	public void setFromFriendlyName(String fromFriendlyName) {
		this.fromFriendlyName = fromFriendlyName;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * Returns subject of this email.
	 * 
	 * @return subject of this email.
	 */
	public String getSubject() {
		return mSubject;
	}

	/**
	 * Sets subject of this email.
	 * 
	 * @param subject
	 *            subject of this email.
	 */
	public void setSubject(String subject) {
		mSubject = subject;
	}

	/**
	 * Method to be overridden so that email content can be built. Depending on type of content of email (text, attachments, HTML) and email provider (JavaMail, Apache Mail or Amazon SES) this method will be implemented in different ways.
	 * 
	 * @param content
	 *            Structure containing email content data to be sent.
	 * @throws EmailException
	 *             if content cannot be properly built.
	 */
	public abstract void buildContent(E content) throws EmailException;
}
