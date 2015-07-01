package com.afkl.generic.mashery;

import java.util.Map;

/**
 * Builder class for the Mashery Client.
 * This allows for cleaner construction of the Mashery client.
 *
 */
public class MasheryClientBuilder {

	String host;
	String protocol;
	int port;
	String apiKey;
	String apiSecret;
	Map<String, String> tokenRequestParameters;

	public static MasheryClientBuilder masheryClient() {
		return new MasheryClientBuilder();
	}

	public MasheryClientBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public MasheryClientBuilder withProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public MasheryClientBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public MasheryClientBuilder withApiKey(String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	public MasheryClientBuilder withApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
		return this;
	}

	public MasheryClientBuilder withTokenRequestParams(Map<String, String> params) {
		this.tokenRequestParameters = params;
		return this;
	}

	public MasheryClient build() {
		return new MasheryClient(this);
	}
}
