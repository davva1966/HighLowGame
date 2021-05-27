package au.com.highlowgame.user;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.StringUtils;

public class SSTokenBasedRememeberMeServices extends TokenBasedRememberMeServices {

	public SSTokenBasedRememeberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {

		String username = retrieveUserName(successfulAuthentication);
		String password = retrievePassword(successfulAuthentication);

		// If unable to find username and password, just abort as
		// TokenBasedRememberMeServices is unable to construct a valid token in
		// this case.

		if (!StringUtils.hasLength(username)) {
			logger.debug("Unable to retrieve username");
			return;
		}

		if (!StringUtils.hasLength(password)) {
			UserDetails user = getUserDetailsService().loadUserByUsername(username);
			password = user.getPassword();

			if (!StringUtils.hasLength(password)) {
				logger.debug("Unable to obtain password for user: " + username);
				return;
			}
		}

		int tokenLifetime = calculateLoginLifetime(request, successfulAuthentication);
		long expiryTime = System.currentTimeMillis();
		// SEC-949
		expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);

		String signatureValue = makeTokenSignature(expiryTime, username, password);

		setCookie(new String[] { username, Long.toString(expiryTime), signatureValue }, tokenLifetime, request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Added remember-me cookie for user '" + username + "', expiry: '" + new Date(expiryTime) + "'");
		}
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

		if (cookieTokens.length != 3) {
			throw new InvalidCookieException("Cookie token did not contain 3" + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
		}

		String[] newCookieTokens = new String[2];
		System.arraycopy(cookieTokens, 0, newCookieTokens, 0, 2);

		return super.processAutoLoginCookie(newCookieTokens, request, response);

	}

}
