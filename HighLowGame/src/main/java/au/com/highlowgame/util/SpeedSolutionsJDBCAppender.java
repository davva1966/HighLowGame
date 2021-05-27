package au.com.highlowgame.util;

import org.apache.log4j.Layout;
import org.apache.log4j.jdbcplus.JDBCAppender;

public class SpeedSolutionsJDBCAppender extends JDBCAppender {

	public SpeedSolutionsJDBCAppender() {
	}

	public SpeedSolutionsJDBCAppender(Layout layout) {
		super(layout);
	}

	@Override
	public void setUsername(String value) {
		if (SSUtil.empty(value))
			value = "highlow";
		super.setUsername(value);
	}

	@Override
	public void setPassword(String value) {
		if (SSUtil.empty(value))
			value = "h1ghl0w";
		super.setPassword(value);
	}

	@Override
	public void setUrl(String value) {
		if (SSUtil.empty(value))
			value = "jdbc:mysql://52.62.85.87/higlowgame";
		super.setUrl(value);
	}

}
