package au.com.highlowgame.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;

public class TimeZoneDateTransformer extends AbstractTransformer implements ObjectFactory {

	SimpleDateFormat simpleDateFormatter;

	public TimeZoneDateTransformer(String dateFormat, TimeZone tz) {
		simpleDateFormatter = new SimpleDateFormat(dateFormat);
		simpleDateFormatter.setTimeZone(tz);
	}

	public void transform(Object value) {
		getContext().writeQuoted(simpleDateFormatter.format(value));
	}

	@SuppressWarnings("rawtypes")
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
		try {
			return simpleDateFormatter.parse(value.toString());
		} catch (ParseException e) {
			throw new JSONException(String.format("Failed to parse %s with %s pattern.", value, simpleDateFormatter.toPattern()), e);
		}
	}

}
