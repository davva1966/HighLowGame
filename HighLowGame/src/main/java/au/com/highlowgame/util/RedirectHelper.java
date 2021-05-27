package au.com.highlowgame.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service("redirectHelperService")
public interface RedirectHelper {

	public String redirectToReferer(HttpServletRequest request);

	public String redirectWithNotFoundMessage(HttpServletRequest request);

	public String redirectToEntityEditView(String path, String entityId);

	public String redirectTo(String path);

	public String redirectTo(String basePath, String subPath);

}
