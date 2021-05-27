package au.com.highlowgame.util;

/**
 * Exception raised if anything fails on the email sending layer.
 * All exception in this package inherit from this one.
 */
public class EmailException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public EmailException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message message describing the cause of the exception.
     */
    public EmailException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message message describing the cause of the exception.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */
    public EmailException(Throwable cause) {
        super(cause);
    }    
}
