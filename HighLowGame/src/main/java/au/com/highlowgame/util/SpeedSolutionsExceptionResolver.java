package au.com.highlowgame.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import au.com.highlowgame.service.TranslationService;

public class SpeedSolutionsExceptionResolver extends SimpleMappingExceptionResolver {

	@Autowired
	TranslationService translationService;

	private List<String> excludedExceptionsForLog;
	
	protected Logger logger = Logger.getLogger(this.getClass());

	public SpeedSolutionsExceptionResolver() {
		super();
	}

	public List<String> getExcludedExceptionsForLog() {
		return excludedExceptionsForLog;
	}

	public void setExcludedExceptionsForLog(List<String> excludedExceptionsForLog) {
		this.excludedExceptionsForLog = excludedExceptionsForLog;
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		if (SSUtil.isJsonRequest(request)) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setHeader("Content-Type", "application/json; charset=utf-8");
			JSONMessage msg = null;
			if (ex instanceof ConcurrencyFailureException) {
				msg = new JSONMessage(Message.DATA_CONCURRENCY_FAILURE.getMessage(), null, Message.DATA_CONCURRENCY_FAILURE.getCode());
			} else if (ex instanceof DataIntegrityViolationException) {
				msg = new JSONMessage(Message.DATA_INTEGRITY_VIOLATION.getMessage(), null, Message.DATA_INTEGRITY_VIOLATION.getCode());
			} else {
				msg = new JSONMessage(translationService.translate(ex), JSONMessage.GENERIC_ERROR);
				msg.setCode(Message.INTERNAL_ERROR.getCode());
			}

			try {
				response.getOutputStream().write(msg.toJsonBytes());
			} catch (Exception e) {
			}
			return new ModelAndView();
		}

		return super.doResolveException(request, response, handler, ex);

	}

	protected void logException(Exception ex, HttpServletRequest request) {
		if (getExcludedExceptionsForLog() != null && getExcludedExceptionsForLog().size() > 0) {
			if (getExcludedExceptionsForLog().contains(ex.getClass().getSimpleName()))
				return;
		}

		logger.warn(buildLogMessage(ex, request), ex);

	}

	protected String buildLogMessage(Exception ex, HttpServletRequest request) {
		String method = request.getMethod();
		String uri = request.getRequestURI();
		return method + " to " + uri + " resulted in exception: " + ex.getMessage();
	}
}
