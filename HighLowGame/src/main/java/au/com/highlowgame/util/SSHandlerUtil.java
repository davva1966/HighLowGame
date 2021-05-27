package au.com.highlowgame.util;

import java.io.Serializable;

public class SSHandlerUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private static SSHandlerUtil instance;

	static {
		instance = new SSHandlerUtil();
	}

	public SSHandlerUtil() {
		super();
	}

	public static SSHandlerUtil instance() {
		return instance;

	}

	public String getMaintainPermissionName(String entityName) {
		return "ADMIN";
	}

	public String getMaintainPermissionNameSingle(String entityName) {
		return "ADMIN";
	}

	public String getTimeZoneID() {
		return "Australia/Sydney";
	}

}
