package au.com.highlowgame.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import au.com.highlowgame.service.SecurityService;

public class ExtendedUserDetailsService extends JdbcDaoImpl {

	@Autowired
	SecurityService securityService;

	private String usersByUsernameQuery = "select email,password,0 from player where email=?";
	private String authoritiesByUsernameQuery = "select player.admin from player where player.email = ?";

	protected List<UserDetails> loadUsersByUsername(String username) {
		setUsersByUsernameQuery(usersByUsernameQuery);
		return super.loadUsersByUsername(username);
	}

	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();

		if (!isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}

		ExtendedUser extendedUser = new ExtendedUser(returnUsername, userFromUserQuery.getPassword(), !userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities);

		return extendedUser;
	}

	@Override
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		return getJdbcTemplate().query(this.authoritiesByUsernameQuery, new String[] { username }, (rs, rowNum) -> {
			boolean isAdmin = rs.getBoolean(1);
			String role = "USER";
			if (isAdmin)
				role = "ADMIN";
			return new SimpleGrantedAuthority(role);
		});

	}

}
