package au.com.highlowgame.user;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationProvider extends DaoAuthenticationProvider {

	public AuthenticationProvider() {
		super();
	}

	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
		Authentication auth = super.createSuccessAuthentication(principal, authentication, user);
		UserContextService.setUsername(user.getUsername());
		return auth;

	}

}
