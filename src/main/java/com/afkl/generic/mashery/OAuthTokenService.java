package com.afkl.generic.mashery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton service to allow re-use of OAuth Tokens.
 *
 */
public enum OAuthTokenService {
	INSTANCE;

	private Logger log = LoggerFactory.getLogger(OAuthTokenService.class);

	private String token;

	private int ttl;

	private long tokenTimeStamp;

	private String scope;

	// Buffer to ensure expired tokens are not used (in seconds)
	private final static int bufferTime = 10;

	/**
	 * Retrieve the OAuth token if previously stored and still valid.
	 *
	 * @return valid oauth token
	 */
	public synchronized String retrieveToken(String apiScope) {

		if (token == null)
			return null;

		long timeSinceStore = System.currentTimeMillis() - tokenTimeStamp;

		if (timeSinceStore/1000 > (ttl - bufferTime))
			return null;

		if (!MasheryUtils.isEqual(apiScope, scope))
			return null;
		log.info("Valid token found in cache belongs to the given Area ID, so using the existing access token..");
		return token;
	}

	/**
	 * Insert Oauth token with the remaining time to live.
	 *
	 * @param token - valid token
	 * @param ttl - time to live (in seconds)
	 * @param scope - AreaUUID i.e environment to which the token belong to
	 */
	public synchronized void putToken(String token, int ttl, String scope) {
		this.token = token;
		this.ttl = ttl;
		this.tokenTimeStamp = System.currentTimeMillis();
		this.scope = scope;
	}

	/**
	 * Invalidate the stored token.
	 */
	public void clearToken() {
		this.token = null;
	}
}
