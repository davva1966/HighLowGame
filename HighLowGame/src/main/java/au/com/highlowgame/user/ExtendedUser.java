package au.com.highlowgame.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class ExtendedUser extends User {

	private static final long serialVersionUID = 1L;

	private Set<String> permissions;

	public ExtendedUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public boolean hasPermission(String permission) {
		return getAuthoritySet().contains(permission);
	}

	private Set<String> getAuthoritySet() {
		if (permissions == null) {
			permissions = new HashSet<String>();
			Collection<? extends GrantedAuthority> playerAuthorities = getAuthorities();

			permissions = AuthorityUtils.authorityListToSet(playerAuthorities);
		}

		return permissions;
	}

}
