package au.com.highlowgame.report;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import au.com.highlowgame.util.SSUtil;

public class SpeedInvoiceReportSelectionMethodArgumentResolver implements HandlerMethodArgumentResolver {

	public SpeedInvoiceReportSelectionMethodArgumentResolver() {
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(ReportSelection.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String name = ModelFactory.getNameForParameter(parameter);
		String reportName = webRequest.getParameter("report");
		if (SSUtil.empty(reportName))
			throw new Exception("No 'report' attribute defined in view");

		Report report = Report.valueOf(reportName);

		WebDataBinder dataBinder = binderFactory.createBinder(webRequest, report.getSelectionBean(), name);
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues(webRequest.getParameterMap());
		dataBinder.bind(mutablePropertyValues);

		if (parameter.hasParameterAnnotation(Valid.class))
			dataBinder.validate();

		BindingResult bindingResult = dataBinder.getBindingResult();

		// Add resolved attribute and BindingResult at the end of the model
		Map<String, Object> bindingResultModel = bindingResult.getModel();
		mavContainer.removeAttributes(bindingResultModel);
		mavContainer.addAllAttributes(bindingResultModel);

		return dataBinder.getBindingResult().getTarget();
	}

}
