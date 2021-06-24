package au.com.highlowgame.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
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
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();

		getJdbcTemplate().query(this.authoritiesByUsernameQuery, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				authList.add(new SimpleGrantedAuthority("USER"));
				if (rs.getBoolean(1))
					authList.add(new SimpleGrantedAuthority("ADMIN"));
			}
		}, username);

		return authList;
	}

}
