package au.com.highlowgame.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.springframework.format.number.AbstractNumberFormatter;
import org.springframework.util.ClassUtils;

public abstract class SpeedSolutionsAbstractNumberFormatter extends AbstractNumberFormatter {

	protected static final boolean roundingModeOnDecimalFormat = ClassUtils.hasMethod(DecimalFormat.class, "setRoundingMode", RoundingMode.class);

	protected RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	/**
	 * Specify the rounding mode to use for decimal parsing. Default is {@link RoundingMode#UNNECESSARY}.
	 */
	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

}
