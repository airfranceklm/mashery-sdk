package com.afkl.generic.mashery.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

public class MasheryEndpointCache {

	private Integer cacheTtlOverride;

	private boolean clientSurrogateControlEnabled;

	private boolean includeApiKeyInContentCacheKey;

	private boolean respondFromStaleCacheEnabled;

	private boolean responseCacheControlEnabled;

	private List<CacheHeader> contentCacheKeyHeaders;

	private boolean varyHeaderEnabled;


	public boolean isClientSurrogateControlEnabled() {
		return clientSurrogateControlEnabled;
	}

	public Integer getCacheTtlOverride() {
		return cacheTtlOverride;
	}

	public void setCacheTtlOverride(int cacheTtlOverride) {
		this.cacheTtlOverride = cacheTtlOverride;
	}

	public boolean isIncludeApiKeyInContentCacheKey() {
		return includeApiKeyInContentCacheKey;
	}

	public void setIncludeApiKeyInContentCacheKey(
			boolean includeApiKeyInContentCacheKey) {
		this.includeApiKeyInContentCacheKey = includeApiKeyInContentCacheKey;
	}

	public boolean isRespondFromStaleCacheEnabled() {
		return respondFromStaleCacheEnabled;
	}

	public void setRespondFromStaleCacheEnabled(boolean respondFromStaleCacheEnabled) {
		this.respondFromStaleCacheEnabled = respondFromStaleCacheEnabled;
	}

	public boolean isResponseCacheControlEnabled() {
		return responseCacheControlEnabled;
	}

	public void setResponseCacheControlEnabled(boolean responseCacheControlEnabled) {
		this.responseCacheControlEnabled = responseCacheControlEnabled;
	}

	public boolean isVaryHeaderEnabled() {
		return varyHeaderEnabled;
	}

	public void setVaryHeaderEnabled(boolean varyHeaderEnabled) {
		this.varyHeaderEnabled = varyHeaderEnabled;
	}

	public void setClientSurrogateControlEnabled(
			boolean clientSurrogateControlEnabled) {
		this.clientSurrogateControlEnabled = clientSurrogateControlEnabled;
	}

	public List<CacheHeader> getContentCacheKeyHeaders() {
		return contentCacheKeyHeaders;
	}

	public void setContentCacheKeyHeaders(List<CacheHeader> contentCacheKeyHeaders) {
		this.contentCacheKeyHeaders = contentCacheKeyHeaders;
	}

	public enum CacheHeader {
		ACCEPT("accept"),
		ACCEPT_CHARSET("accept-charset"),
		ACCEPT_ENCODING("accept-encoding"),
		ACCEPT_LANGUAGE("accept-language");

		private final String string;

		private CacheHeader(String cacheHeader) {
			this.string = cacheHeader;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static CacheHeader fromString(String cacheHeader) {
			for (CacheHeader val : CacheHeader.values()) {
				if (val.string.equalsIgnoreCase(cacheHeader))
					return val;
			}
			return null;
		}
	}
}
