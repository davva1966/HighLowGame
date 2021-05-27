package au.com.highlowgame.util;

/**
 * This class defines the credentials used by Amazon SES to send emails.
 */
public class AWSEmailSenderCredentials {
    /**
     * AWS access key.
     */
    private String mAccessKey;
    
    /**
     * AWS secret key.
     */
    private String mSecretKey;
    
    public AWSEmailSenderCredentials() {
        super();
    }
    
    public AWSEmailSenderCredentials(String accessKey, String secretKey) {
        mAccessKey = accessKey;
        mSecretKey = secretKey;
    }
    
    /**
     * Sets AWS access key.
     * @param accessKey AWS access key.
     */
    public void setAccessKey(String accessKey) {
        mAccessKey = accessKey;
    }
    
    /**
     * Returns AWS access key.
     * @return AWS access key.
     */
    public String getAccessKey() {
        return mAccessKey;
    }
    
    /**
     * Indicates if AWS access key has already been provided or not.
     * @return true if access key is available, false otherwise.
     */
    public boolean isAccessKeyAvailable() {
        return mAccessKey != null;
    }
    
    /**
     * Sets AWS secret key.
     * @param secretKey AWS secret key.
     */
	public void setSecretKey(String secretKey) {
        mSecretKey = secretKey;
    }
    
    /**
     * Returns AWS secret key.
     * @return AWS secret key.
     */
    public String getSecretKey() {
        return mSecretKey;
    }
    
    /**
     * Indicates if secret key has already been provided or not.
     * @return true if secret key is available, false otherwise.
     */
    public boolean isSecretKeyAvailable() {
        return mSecretKey != null;
    }
    
    /**
     * Indicates if credentials are ready to be used if both access key and
     * secret key are available.
     * @return true if ready, false otherwise.
     */
    public boolean isReady() {
        return mAccessKey != null && mSecretKey != null;
    }    
}
