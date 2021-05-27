package au.com.highlowgame.util;

import org.apache.log4j.jdbcplus.JDBCColumnHandler;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class SSJDBCLogLocationColumnHandler implements JDBCColumnHandler {

	public SSJDBCLogLocationColumnHandler() {

	}

	@Override
	public Object getObject(LoggingEvent event, String table, String column) throws Exception {
		LocationInfo locationInfo = event.getLocationInformation();
		if (locationInfo != null)
			return locationInfo.getClassName() + ":" + locationInfo.getMethodName() + ":" + locationInfo.getLineNumber();

		return null;
	}

}
