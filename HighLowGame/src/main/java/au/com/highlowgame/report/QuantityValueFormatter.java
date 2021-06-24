package au.com.highlowgame.report;

import java.math.BigDecimal;

import au.com.highlowgame.util.FormattingHelper;
import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.definition.ReportParameters;

public class QuantityValueFormatter extends AbstractValueFormatter<String, BigDecimal> {

	public QuantityValueFormatter() {
		super();
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String format(BigDecimal value, ReportParameters reportParameters) {
		return FormattingHelper.instance().formatNumber(value);
	}

}
