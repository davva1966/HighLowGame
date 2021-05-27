package au.com.highlowgame.util;

public abstract class TextEmailMessage<E> extends EmailMessage<E> {

	private String mText;

	public TextEmailMessage() {
		super();
		mText = "";
	}

	public TextEmailMessage(String subject) {
		super(subject);
		mText = "";
	}

	public TextEmailMessage(String subject, String text) {
		super(subject);
		mText = text;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	@SuppressWarnings("rawtypes")
	public static TextEmailMessage create() {
		return create("");
	}

	@SuppressWarnings("rawtypes")
	public static TextEmailMessage create(String subject) {
		return create(subject, "");
	}

	@SuppressWarnings("rawtypes")
	public static TextEmailMessage create(String subject, String text) {
		return new AWSTextEmailMessage(subject, text);

	}
}
