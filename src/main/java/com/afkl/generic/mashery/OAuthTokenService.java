package com.afkl.generic.mashery;

/**
 * Singleton service to allow re-use of OAuth Tokens.
 *
 */
public enum OAuthTokenService {
	INSTANCE;

	private String token;

	private int ttl;

	private long tokenTimeStamp;

	// Buffer to ensure expired tokens are not used (in seconds)
	private final static int bufferTime = 10;

	/**
	 * Retrieve the OAuth token if previously stored and still valid.
	 *
	 * @return valid oauth token
	 */
	public synchronized String retrieveToken() {

		if (token == null)
			return null;

		long timeSinceStore = System.currentTimeMillis() - tokenTimeStamp;

		if (timeSinceStore/1000 > (ttl - bufferTime))
			return null;

		return token;
	}

	/**
	 * Insert Oauth token with the remaining time to live.
	 *
	 * @param token - valid token
	 * @param ttl - time to live (in seconds)
	 */
	public synchronized void putToken(String token, int ttl) {
		this.token = token;
		this.ttl = ttl;
		this.tokenTimeStamp = System.currentTimeMillis();
	}

	/**
	 * Invalidate the stored token.
	 */
	public void clearToken() {
		this.token = null;
	}
}
