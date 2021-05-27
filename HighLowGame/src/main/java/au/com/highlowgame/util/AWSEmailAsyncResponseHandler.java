package au.com.highlowgame.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;

import au.com.highlowgame.service.AWSMailSender;

public class AWSEmailAsyncResponseHandler implements AsyncHandler<SendRawEmailRequest, SendRawEmailResult> {

	@Autowired
	AWSMailSender mailSender;

	private Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings("rawtypes")
	protected EmailMessage message;

	public AWSEmailAsyncResponseHandler() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public EmailMessage getMessage() {
		return message;
	}

	@SuppressWarnings("rawtypes")
	public void setMessage(EmailMessage message) {
		this.message = message;
	}

	@Override
	@SuppressWarnings({
			"unchecked" })
	public void onError(Exception exception) {
		// If failure, wait 10 seconds and try again
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			return;
		}
		try {
			mailSender.send(message);
		} catch (Exception e1) {

			String toString = null;
			String ccString = null;
			String bccString = null;

			toString = SSUtil.formatListToSeparated(message.getTo(), ", ");
			try {
				ccString = SSUtil.formatListToSeparated(message.getCC(), ", ");
			} catch (NotSupportedException e) {
			}
			try {
				bccString = SSUtil.formatListToSeparated(message.getBCC(), ", ");
			} catch (NotSupportedException e) {
			}

			StringBuilder msg = new StringBuilder();
			msg.append("Email delivery failed.");
			if (SSUtil.notEmpty(toString))
				msg.append(SSUtil.NEWLINE + " To: " + toString);
			if (SSUtil.notEmpty(ccString))
				msg.append(SSUtil.NEWLINE + " CC: " + ccString);
			if (SSUtil.notEmpty(bccString))
				msg.append(SSUtil.NEWLINE + " BCC: " + bccString);

			msg.append(SSUtil.NEWLINE + SSUtil.NEWLINE + "Error: " + e1.toString());

			logger.warn(msg);

		}

	}

	@Override
	public void onSuccess(SendRawEmailRequest request, SendRawEmailResult result) {

	}

}
