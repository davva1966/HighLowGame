package au.com.highlowgame.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class SpringMessage {

	private String code;
	private Object[] args;

	public SpringMessage(String code) {
		super();
		this.code = code;
	}

	public SpringMessage(String code, Object... args) {
		super();
		this.code = code;
		this.args = args;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public static List<SpringMessage> getFieldErrors(String field, Errors errors) {
		FieldError fieldError = errors.getFieldError(field);
		if (fieldError != null) {
			SpringMessage msg = new SpringMessage(fieldError.getCode());
			msg.setArgs(fieldError.getArguments());
			List<SpringMessage> messages = new ArrayList<SpringMessage>();
			messages.add(msg);
			return messages;
		}
		return null;
	}

}
