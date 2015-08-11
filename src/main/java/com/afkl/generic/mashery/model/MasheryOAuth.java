package com.afkl.generic.mashery.model;

import com.fasterxml.jackson.annotation.JsonValue;


public class MasheryOAuth {

	private boolean accessTokenTtlEnabled;

	private Integer accessTokenTtl;

	private AccessTokenType accessTokenType;

	private boolean allowMultipleToken;

	private Integer authorizationCodeTtl;

	private OAuthHeader[] forwardedHeaders;

	private boolean masheryTokenApiEnabled;

	private boolean refreshTokenEnabled;

	private boolean enableRefreshTokenTtl;

	private boolean tokenBasedRateLimitsEnabled;

	private boolean forceOauthRedirectUri;

	private boolean forceSslRedirectUrlEnabled;

	/**
	 * authorization_code, implicit, password, client_credentials
	 */
	private String[] grantTypes;

	/**
	 *  hmac-sha-1, hmac-sha-256
	 */
	private String macAlgorithim;

	private Integer qpsLimitCeiling;

	private Integer rateLimitCeiling;

	private Integer refreshTokenTtl;

	private boolean secureTokensEnabled;

	public boolean isAccessTokenTtlEnabled() {
		return accessTokenTtlEnabled;
	}

	public void setAccessTokenTtlEnabled(boolean accessTokenTtlEnabled) {
		this.accessTokenTtlEnabled = accessTokenTtlEnabled;
	}

	public Integer getAccessTokenTtl() {
		return accessTokenTtl;
	}

	public void setAccessTokenTtl(int accessTokenTtl) {
		this.accessTokenTtl = accessTokenTtl;
	}

	public AccessTokenType getAccessTokenType() {
		return accessTokenType;
	}

	public void setAccessTokenType(AccessTokenType accessTokenType) {
		this.accessTokenType = accessTokenType;
	}

	public boolean isAllowMultipleToken() {
		return allowMultipleToken;
	}

	public void setAllowMultipleToken(boolean allowMultipleToken) {
		this.allowMultipleToken = allowMultipleToken;
	}

	public Integer getAuthorizationCodeTtl() {
		return authorizationCodeTtl;
	}

	public void setAuthorizationCodeTtl(int authorizationCodeTtl) {
		this.authorizationCodeTtl = authorizationCodeTtl;
	}

	public OAuthHeader[] getForwardedHeaders() {
		return forwardedHeaders;
	}

	public void setForwardedHeaders(OAuthHeader[] forwardedHeaders) {
		this.forwardedHeaders = forwardedHeaders;
	}

	public boolean isMasheryTokenApiEnabled() {
		return masheryTokenApiEnabled;
	}

	public void setMasheryTokenApiEnabled(boolean masheryTokenApiEnabled) {
		this.masheryTokenApiEnabled = masheryTokenApiEnabled;
	}

	public boolean isRefreshTokenEnabled() {
		return refreshTokenEnabled;
	}

	public void setRefreshTokenEnabled(boolean refreshTokenEnabled) {
		this.refreshTokenEnabled = refreshTokenEnabled;
	}

	public boolean isEnableRefreshTokenTtl() {
		return enableRefreshTokenTtl;
	}

	public void setEnableRefreshTokenTtl(boolean enableRefreshTokenTtl) {
		this.enableRefreshTokenTtl = enableRefreshTokenTtl;
	}

	public boolean isTokenBasedRateLimitsEnabled() {
		return tokenBasedRateLimitsEnabled;
	}

	public void setTokenBasedRateLimitsEnabled(boolean tokenBasedRateLimitsEnabled) {
		this.tokenBasedRateLimitsEnabled = tokenBasedRateLimitsEnabled;
	}

	public boolean isForceOauthRedirectUri() {
		return forceOauthRedirectUri;
	}

	public void setForceOauthRedirectUri(boolean forceOauthRedirectUri) {
		this.forceOauthRedirectUri = forceOauthRedirectUri;
	}

	public boolean isForceSslRedirectUrlEnabled() {
		return forceSslRedirectUrlEnabled;
	}

	public void setForceSslRedirectUrlEnabled(boolean forceSslRedirectUrlEnabled) {
		this.forceSslRedirectUrlEnabled = forceSslRedirectUrlEnabled;
	}

	public String[] getGrantTypes() {
		return grantTypes;
	}

	public void setGrantTypes(String[] grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getMacAlgorithim() {
		return macAlgorithim;
	}

	public void setMacAlgorithim(String macAlgorithim) {
		this.macAlgorithim = macAlgorithim;
	}

	public Integer getQpsLimitCeiling() {
		return qpsLimitCeiling;
	}

	public void setQpsLimitCeiling(int qpsLimitCeiling) {
		this.qpsLimitCeiling = qpsLimitCeiling;
	}

	public Integer getRateLimitCeiling() {
		return rateLimitCeiling;
	}

	public void setRateLimitCeiling(int rateLimitCeiling) {
		this.rateLimitCeiling = rateLimitCeiling;
	}

	public Integer getRefreshTokenTtl() {
		return refreshTokenTtl;
	}

	public void setRefreshTokenTtl(int refreshTokenTtl) {
		this.refreshTokenTtl = refreshTokenTtl;
	}

	public boolean isSecureTokensEnabled() {
		return secureTokensEnabled;
	}

	public void setSecureTokensEnabled(boolean secureTokensEnabled) {
		this.secureTokensEnabled = secureTokensEnabled;
	}

	public enum AccessTokenType {
		MAC("mac"),
		BEARER("bearer");

		private final String string;

		private AccessTokenType(String tokenType) {
			this.string = tokenType;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static AccessTokenType fromString(String tokenType) {
			for (AccessTokenType val : AccessTokenType.values()) {
				if (val.string.equalsIgnoreCase(tokenType))
					return val;
			}
			return null;
		}
	}

	public enum OAuthHeader {
		ACCESS_TOKEN("access-token"),
		CLIENT_ID("client-id"),
		SCOPE("scope"),
		USER_CONTEXT("user-context");

		private final String string;

		private OAuthHeader(String oauthHeader) {
			this.string = oauthHeader;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static OAuthHeader fromString(String oauthHeader) {
			for (OAuthHeader val : OAuthHeader.values()) {
				if (val.string.equalsIgnoreCase(oauthHeader))
					return val;
			}
			return null;
		}
	}
}
