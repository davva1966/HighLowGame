package au.com.highlowgame.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import au.com.highlowgame.util.SSUtil;

public class ShortenHTMLString extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String value;
	private int maxLength;
	private String var;

	@Override
	public int doStartTag() throws JspException {
		try {
			if (this.value != null && !this.value.isEmpty()) {
				String output = SSUtil.shortenHtml(value, maxLength);

				if (this.var != null && !this.var.isEmpty()) {
					this.pageContext.getRequest().setAttribute(this.var, output);
				} else {
					this.pageContext.getOut().write(output);
				}
			}
		} catch (Exception e) {
		}

		return super.doStartTag();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}