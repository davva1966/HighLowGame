package au.com.highlowgame.util;

public class Application {

	private static String backendServer = "";
	private static int backendPort = 0;
	private static String backendDomain = "";

	private static String baseUrl = null;
	private static String relativeUrl = null;

	public static String getBackendServer() {
		return backendServer;
	}

	public static void setBackendServer(String backendServer) {
		Application.backendServer = backendServer;
	}

	public static int getBackendPort() {
		return backendPort;
	}

	public static void setBackendPort(int backendPort) {
		Application.backendPort = backendPort;
	}

	public static String getBackendDomain() {
		return backendDomain;
	}

	public static void setBackendDomain(String backendDomain) {
		Application.backendDomain = backendDomain;
	}

	public static String getBaseURL() {
		if (baseUrl == null) {
			StringBuffer s = new StringBuffer();
			s.append("http://");
			s.append(getBackendServer());
			if (getBackendPort() > 0) {
				s.append(":");
				s.append(getBackendPort());
			}
			if (SSUtil.notEmpty(getBackendDomain())) {
				s.append("/");
				s.append(getBackendDomain());
			}
			s.append("/");

			baseUrl = s.toString();
		}

		return baseUrl;
	}

	public static String getRelativeURL() {
		if (relativeUrl == null) {
			StringBuffer s = new StringBuffer();
			s.append("/");
			if (SSUtil.notEmpty(getBackendDomain())) {
				s.append(getBackendDomain());
				s.append("/");
			}
			
			relativeUrl = s.toString();
		}

		return relativeUrl;

	}

}
