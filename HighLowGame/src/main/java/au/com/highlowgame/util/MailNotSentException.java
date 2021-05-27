package au.com.highlowgame.util;

/**
 * Raised if an email couldn't be sent for some reason (wrong configuration,
 * network or SMTP server failure, etc).
 */
public class MailNotSentException extends EmailException {
    
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public MailNotSentException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message message describing the cause of the exception.
     */    
    public MailNotSentException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message message describing the cause of the exception.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */    
    public MailNotSentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */    
    public MailNotSentException(Throwable cause) {
        super(cause);
    }        
}
