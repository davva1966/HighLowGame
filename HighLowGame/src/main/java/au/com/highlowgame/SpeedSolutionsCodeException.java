package au.com.highlowgame;

import au.com.highlowgame.util.AppUtil;

public class SpeedSolutionsCodeException extends SpeedSolutionsException {

	private static final long serialVersionUID = 1L;

	private Object[] arguments;

	public SpeedSolutionsCodeException(String messageCode) {
		super(messageCode);
	}

	public SpeedSolutionsCodeException(String messageCode, Object... arguments) {
		super(messageCode);
		this.arguments = arguments;
	}

	public String getMessageCode() {
		return getMessage();
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	@Override
	public String getLocalizedMessage() {
		try {
			if (getArguments() == null)
				return AppUtil.translate(getMessage());
			else
				return AppUtil.translate(getMessage(), getArguments());
		} catch (Throwable e) {
			return getMessage();
		}
	}

}
