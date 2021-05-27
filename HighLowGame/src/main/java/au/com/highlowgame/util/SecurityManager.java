package au.com.highlowgame.util;

public class SecurityManager {
	public SecurityManager() {
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
		java.security.Security.setProperty("sun.net.inetaddr.ttl", "60");
	}

}
