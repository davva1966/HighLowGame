package au.com.highlowgame.util;

public class SSGmailTextEmailMessage extends EmailMessage {

	private String mText;

	public SSGmailTextEmailMessage() {
		super();
		mText = "";
	}

	public SSGmailTextEmailMessage(String subject) {
		super(subject);
		mText = "";
	}

	public SSGmailTextEmailMessage(String subject, String text) {
		super(subject);
		mText = text;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

}
