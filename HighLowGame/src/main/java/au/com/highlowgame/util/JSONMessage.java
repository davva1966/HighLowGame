package au.com.highlowgame.util;

import flexjson.JSONSerializer;

public class JSONMessage {

	public static final int GENERIC_INFO = 0;
	public static final int GENERIC_ERROR = 999;

	public int code;
	public String message;
	public String[] arguments;

	public JSONMessage() {
		this(null, GENERIC_INFO);
	}

	public JSONMessage(String message, int code) {
		this(message, null, code);
	}

	public JSONMessage(String message, String[] arguments, int code) {
		this.code = code;
		this.message = message;
		this.arguments = arguments;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String... arguments) {
		this.arguments = arguments;
	}

	public String toJson() {
		return new JSONSerializer().exclude("*.class").deepSerialize(this);

	}

	public byte[] toJsonBytes() {
		String json = toJson();
		byte[] jsonBytes = null;
		try {
			jsonBytes = json.getBytes("UTF-8");
		} catch (Exception e) {
			jsonBytes = json.getBytes();
		}
		return jsonBytes;
	}

}
