package au.com.highlowgame.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class SSURIBuilder extends URIBuilder {

	protected boolean parametersAdded = false;

	public SSURIBuilder() {
	}

	public SSURIBuilder(String string) throws URISyntaxException {
		super(string);
	}

	public SSURIBuilder(URI uri) {
		super(uri);
	}

	public URIBuilder setParameter(final String param, final String value) {
		parametersAdded = true;
		return super.setParameter(param, value);

	}

	public URIBuilder addParameter(final String param, final String value) {
		parametersAdded = true;
		return super.addParameter(param, value);

	}

	public void addPath(String additionalPath) throws Exception {
		if (parametersAdded)
			throw new Exception("addPath cannot be called after parameters are added");
		URI uri = URI.create(build().toString());
		String path = uri.getPath();
		if (path == null)
			path = "";

		if (path.endsWith("/") == false && additionalPath.startsWith("/") == false)
			additionalPath = "/" + additionalPath;

		setPath(path + additionalPath);
	}

}
