package au.com.highlowgame.util;

public class Host {

	public String server;
	public int port;
	public String domain;

	public Host() {

	}

	public Host(String server, int port, String domain) {
		this.server = server;
		this.port = port;
		this.domain = domain;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
