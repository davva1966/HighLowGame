package au.com.highlowgame.util;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import au.com.highlowgame.model.Player;

public class CheckCredentialsFilter extends GenericFilterBean {

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationManager authenticationManager;
	private String credentialsCharset = "UTF-8";

	public CheckCredentialsFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public void setCredentialsCharset(String credentialsCharset) {
		Assert.hasText(credentialsCharset, "credentialsCharset cannot be null or empty");
		this.credentialsCharset = credentialsCharset;
	}

	protected String getCredentialsCharset(HttpServletRequest httpRequest) {
		return credentialsCharset;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		String header = request.getHeader("Authorization");

		if (SSUtil.isJsonRequest(request)) {

			if (header == null || !header.startsWith("Basic ")) {
				chain.doFilter(request, response);
				return;
			}

			String[] tokens = extractAndDecodeHeader(header, request);
			assert tokens.length == 2;

			String username = tokens[0];
			String pwd = tokens[1];
			Message message = null;
			Player player = null;

			if (username == null)
				message = Message.INVALID_USER;

			if (message == null) {
//				try {
//					player = Player.findPlayerForUsername(username);
//				} catch (Throwable e) {
//				}
				if (player == null)
					message = Message.INVALID_USER;
			}
			
//			if (message == null) {
//				if (user != null && user.getDisabled())
//					message = Message.USER_DISABLED;
//			}

			if (message == null) {
				if (pwd == null)
					message = Message.INVALID_PASSWORD;
			}

			if (message == null) {
				try {
					if (authenticationIsRequired(username)) {
						UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, tokens[1]);
						authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
						authenticationManager.authenticate(authRequest);
					}
				} catch (AuthenticationException failed) {
					message = Message.INVALID_CREDENTIALS;
				}
			}

			if (message != null) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setHeader("Content-Type", "application/json; charset=utf-8");
				response.getOutputStream().write(message.toJsonBytes());
				return;
			}

		}

		chain.doFilter(request, response);
	}

	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, getCredentialsCharset(request));

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	private boolean authenticationIsRequired(String username) {
		// Only reauthenticate if username doesn't match SecurityContextHolder
		// and user isn't authenticated
		// (see SEC-53)
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

		if (existingAuth == null || !existingAuth.isAuthenticated()) {
			return true;
		}

		// Limit username comparison to providers which use usernames (ie
		// UsernamePasswordAuthenticationToken)
		// (see SEC-348)

		if (existingAuth instanceof UsernamePasswordAuthenticationToken && !existingAuth.getName().equals(username)) {
			return true;
		}

		// Handle unusual condition where an AnonymousAuthenticationToken is
		// already present
		// This shouldn't happen very often, as BasicProcessingFitler is meant
		// to be earlier in the filter
		// chain than AnonymousAuthenticationFilter. Nevertheless, presence of
		// both an AnonymousAuthenticationToken
		// together with a BASIC authentication request header should indicate
		// reauthentication using the
		// BASIC protocol is desirable. This behaviour is also consistent with
		// that provided by form and digest,
		// both of which force re-authentication if the respective header is
		// detected (and in doing so replace
		// any existing AnonymousAuthenticationToken). See SEC-610.
		if (existingAuth instanceof AnonymousAuthenticationToken) {
			return true;
		}

		return false;
	}

}
