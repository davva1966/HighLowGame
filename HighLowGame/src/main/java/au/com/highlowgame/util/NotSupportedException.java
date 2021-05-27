package au.com.highlowgame.util;

/**
 * Raised if a given feature is not supported.
 */
public class NotSupportedException extends EmailException {
    
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public NotSupportedException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message message describing the cause of the exception.
     */    
    public NotSupportedException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message message describing the cause of the exception.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */    
    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause originating exception that caused this exception. This can
     * be used to obtain stack traces.
     */    
    public NotSupportedException(Throwable cause) {
        super(cause);
    }        
}
