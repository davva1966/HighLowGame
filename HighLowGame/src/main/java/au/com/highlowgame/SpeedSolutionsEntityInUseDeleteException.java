package au.com.highlowgame;

import java.util.ArrayList;
import java.util.List;

public class SpeedSolutionsEntityInUseDeleteException extends SpeedSolutionsException {

	private static final long serialVersionUID = 1L;

	private List<WhereUsed> whereUsed;

	public static class WhereUsed {
		private String message;
		private Object[] arguments;
		private String whereUsedMessageCode;
		private List<? extends Object> whereUsed;
		
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Object[] getArguments() {
			return arguments;
		}

		public void setArguments(Object... arguments) {
			this.arguments = arguments;
		}


		public String getWhereUsedMessageCode() {
			return whereUsedMessageCode;
		}

		public void setWhereUsedMessageCode(String whereUsedMessageCode) {
			this.whereUsedMessageCode = whereUsedMessageCode;
		}

		public List<? extends Object> getWhereUsed() {
			return whereUsed;
		}

		public void setWhereUsed(List<? extends Object> whereUsed) {
			this.whereUsed = whereUsed;
		}
	}

	public SpeedSolutionsEntityInUseDeleteException() {
		super();
	}

	public List<WhereUsed> getWhereUsed() {
		if (whereUsed == null)
			whereUsed = new ArrayList<WhereUsed>();
		return whereUsed;
	}

	public void setWhereUsed(List<WhereUsed> whereUsed) {
		this.whereUsed = whereUsed;
	}

}
