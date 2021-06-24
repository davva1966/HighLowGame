package au.com.highlowgame.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import au.com.highlowgame.service.TranslationService;

public class RedirectHelperImpl implements RedirectHelper {

	@Autowired
	TranslationService translationService;

	@Override
	public String redirectToReferer(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		return redirectTo(referer);

	}

	@Override
	public String redirectWithNotFoundMessage(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		try {
			SSURIBuilder builder = new SSURIBuilder(referer);
			// builder.addParameter("ssdisplaymessage", translationService.translate("message_com_ss_highlowgame_entity_deleted", entityName, null));
			return "redirect:" + builder.build();
		} catch (Exception e) {
			return "redirect:" + referer;
		}

	}


	@Override
	public String redirectToEntityEditView(String path, String entityId) {
		try {
			SSURIBuilder builder = new SSURIBuilder(path);
			builder.addPath(entityId);
			builder.addParameter("form", null);
			return "redirect:" + builder.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String redirectTo(String path) {
		return redirectTo(path, null);

	}

	@Override
	public String redirectTo(String basePath, String subPath) {
		try {
			SSURIBuilder builder = new SSURIBuilder(basePath);
			if (SSUtil.notEmpty(subPath))
				builder.addPath(subPath);
			return "redirect:" + builder.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
