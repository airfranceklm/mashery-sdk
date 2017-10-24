package com.afkl.generic.mashery.model;

import java.util.List;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class MasheryEndpoint implements MasheryResource {

	@JsonIgnore
	public static final String PATH = "/v3/rest/services/%s/endpoints";

	@JsonIgnore
	private String endpointId;

	private boolean allowMissingApiKey;

	private String apiKeyValueLocationKey;

	private List<ValueLocation> apiKeyValueLocations;

	private String apiMethodDetectionKey;

	private List<ValueLocation> apiMethodDetectionLocations;

	private MasheryEndpointCache cache;

	private Integer connectionTimeoutForSystemDomainRequest;

	private Integer connectionTimeoutForSystemDomainResponse;

	private boolean cookiesDuringHttpRedirectsEnabled;

	private MasheryCors cors;

	private String customRequestAuthenticationAdapter;

	private boolean dropApiKeyFromIncomingCall;

	private boolean forceGzipOfBackendCall;

	private boolean gzipPassthroughSupportEnabled;

	private List<String> headersToExcludeFromIncomingCall;

	private boolean highSecurity;

	private boolean hostPassthroughIncludedInBackendCallHeader;

	private boolean inboundSslRequired;

	private String jsonpCallbackParameter;

	private String jsonpCallbackParameterValue;

	private boolean rateLimitHeadersEnabled;

	private List<ForwardHeader> forwardedHeaders;

	private List<ReturnHeader> returnedHeaders;

	private List<MasheryMethod> methods;

	private String name;

	private Integer numberOfHttpRedirectsToFollow;

	private String outboundRequestTargetPath;

	private String outboundRequestTargetQueryParameters;

	private ProtocolType outboundTransportProtocol;

	private MasheryProcessor processor;

	private List<MasheryDomain> publicDomains;

	private RequestAuthenticationType requestAuthenticationType;

	private String requestPathAlias;

	private RequestProtocol requestProtocol;

	private List<OAuthGrantType> oauthGrantTypes;

	private String stringsToTrimFromApiKey;

	private List<HttpMethod> supportedHttpMethods;

	private MasherySystemDomainAuthentication systemDomainAuthentication;

	private List<MasheryDomain> systemDomains;

	private String trafficManagerDomain;

	private boolean useSystemDomainCredentials;

	private String systemDomainCredentialKey;

	private String systemDomainCredentialSecret;

	private String type;

	@JsonIgnore
	private String serviceId;

	public boolean isAllowMissingApiKey() {
		return allowMissingApiKey;
	}

	public String getApiKeyValueLocationKey() {
		return apiKeyValueLocationKey;
	}

	public List<ValueLocation> getApiKeyValueLocations() {
		return apiKeyValueLocations;
	}

	public String getApiMethodDetectionKey() {
		return apiMethodDetectionKey;
	}

	public List<ValueLocation> getApiMethodDetectionLocations() {
		return apiMethodDetectionLocations;
	}

	public MasheryEndpointCache getCache() {
		return cache;
	}

	public Integer getConnectionTimeoutForSystemDomainRequest() {
		return connectionTimeoutForSystemDomainRequest;
	}

	public Integer getConnectionTimeoutForSystemDomainResponse() {
		return connectionTimeoutForSystemDomainResponse;
	}

	public boolean isCookiesDuringHttpRedirectsEnabled() {
		return cookiesDuringHttpRedirectsEnabled;
	}

	public MasheryCors getCors() {
		return cors;
	}

	public String getCustomRequestAuthenticationAdapter() {
		return customRequestAuthenticationAdapter;
	}

	public boolean isDropApiKeyFromIncomingCall() {
		return dropApiKeyFromIncomingCall;
	}

	public boolean isForceGzipOfBackendCall() {
		return forceGzipOfBackendCall;
	}

	public boolean isGzipPassthroughSupportEnabled() {
		return gzipPassthroughSupportEnabled;
	}

	public List<String> getHeadersToExcludeFromIncomingCall() {
		return headersToExcludeFromIncomingCall;
	}

	public boolean isHighSecurity() {
		return highSecurity;
	}

	public boolean isHostPassthroughIncludedInBackendCallHeader() {
		return hostPassthroughIncludedInBackendCallHeader;
	}

	public boolean isInboundSslRequired() {
		return inboundSslRequired;
	}

	public String getJsonpCallbackParameter() {
		return jsonpCallbackParameter;
	}

	public String getJsonpCallbackParameterValue() {
		return jsonpCallbackParameterValue;
	}

	public boolean isRateLimitHeadersEnabled() {
		return rateLimitHeadersEnabled;
	}

	public List<ForwardHeader> getForwardedHeaders() {
		return forwardedHeaders;
	}

	public List<ReturnHeader> getReturnedHeaders() {
		return returnedHeaders;
	}

	public List<MasheryMethod> getMethods() {
		return methods;
	}

	public String getName() {
		return name;
	}

	public Integer getNumberOfHttpRedirectsToFollow() {
		return numberOfHttpRedirectsToFollow;
	}

	public String getOutboundRequestTargetPath() {
		return outboundRequestTargetPath;
	}

	public String getOutboundRequestTargetQueryParameters() {
		return outboundRequestTargetQueryParameters;
	}

	public ProtocolType getOutboundTransportProtocol() {
		return outboundTransportProtocol;
	}

	public MasheryProcessor getProcessor() {
		return processor;
	}

	public List<MasheryDomain> getPublicDomains() {
		return publicDomains;
	}

	public RequestAuthenticationType getRequestAuthenticationType() {
		return requestAuthenticationType;
	}

	public String getRequestPathAlias() {
		return requestPathAlias;
	}

	public RequestProtocol getRequestProtocol() {
		return requestProtocol;
	}

	public List<OAuthGrantType> getOauthGrantTypes() {
		return oauthGrantTypes;
	}

	public String getStringsToTrimFromApiKey() {
		return stringsToTrimFromApiKey;
	}

	public List<HttpMethod> getSupportedHttpMethods() {
		return supportedHttpMethods;
	}

	public MasherySystemDomainAuthentication getSystemDomainAuthentication() {
		return systemDomainAuthentication;
	}

	public List<MasheryDomain> getSystemDomains() {
		return systemDomains;
	}

	public String getTrafficManagerDomain() {
		return trafficManagerDomain;
	}

	public boolean isUseSystemDomainCredentials() {
		return useSystemDomainCredentials;
	}

	public void setEndpointId(String endpointId) {
		this.endpointId = endpointId;
	}

	public void setAllowMissingApiKey(boolean allowMissingApiKey) {
		this.allowMissingApiKey = allowMissingApiKey;
	}

	public void setApiKeyValueLocationKey(String apiKeyValueLocationKey) {
		this.apiKeyValueLocationKey = apiKeyValueLocationKey;
	}

	public void setApiKeyValueLocations(List<ValueLocation> apiKeyValueLocations) {
		this.apiKeyValueLocations = apiKeyValueLocations;
	}

	public void setApiMethodDetectionKey(String apiMethodDetectionKey) {
		this.apiMethodDetectionKey = apiMethodDetectionKey;
	}

	public void setApiMethodDetectionLocations(
			List<ValueLocation> apiMethodDetectionLocations) {
		this.apiMethodDetectionLocations = apiMethodDetectionLocations;
	}

	public void setCache(MasheryEndpointCache cache) {
		this.cache = cache;
	}

	public void setConnectionTimeoutForSystemDomainRequest(
			int connectionTimeoutForSystemDomainRequest) {
		this.connectionTimeoutForSystemDomainRequest = connectionTimeoutForSystemDomainRequest;
	}

	public void setConnectionTimeoutForSystemDomainResponse(
			int connectionTimeoutForSystemDomainResponse) {
		this.connectionTimeoutForSystemDomainResponse = connectionTimeoutForSystemDomainResponse;
	}

	public void setCookiesDuringHttpRedirectsEnabled(
			boolean cookiesDuringHttpRedirectsEnabled) {
		this.cookiesDuringHttpRedirectsEnabled = cookiesDuringHttpRedirectsEnabled;
	}

	public void setCors(MasheryCors cors) {
		this.cors = cors;
	}

	public void setCustomRequestAuthenticationAdapter(
			String customRequestAuthenticationAdapter) {
		this.customRequestAuthenticationAdapter = customRequestAuthenticationAdapter;
	}

	public void setDropApiKeyFromIncomingCall(boolean dropApiKeyFromIncomingCall) {
		this.dropApiKeyFromIncomingCall = dropApiKeyFromIncomingCall;
	}

	public void setForceGzipOfBackendCall(boolean forceGzipOfBackendCall) {
		this.forceGzipOfBackendCall = forceGzipOfBackendCall;
	}

	public void setGzipPassthroughSupportEnabled(
			boolean gzipPassthroughSupportEnabled) {
		this.gzipPassthroughSupportEnabled = gzipPassthroughSupportEnabled;
	}

	public void setHeadersToExcludeFromIncomingCall(
			List<String> headersToExcludeFromIncomingCall) {
		this.headersToExcludeFromIncomingCall = headersToExcludeFromIncomingCall;
	}

	public void setHighSecurity(boolean highSecurity) {
		this.highSecurity = highSecurity;
	}

	public void setHostPassthroughIncludedInBackendCallHeader(
			boolean hostPassthroughIncludedInBackendCallHeader) {
		this.hostPassthroughIncludedInBackendCallHeader = hostPassthroughIncludedInBackendCallHeader;
	}

	public void setInboundSslRequired(boolean inboundSslRequired) {
		this.inboundSslRequired = inboundSslRequired;
	}

	public void setJsonpCallbackParameter(String jsonpCallbackParameter) {
		this.jsonpCallbackParameter = jsonpCallbackParameter;
	}

	public void setJsonpCallbackParameterValue(String jsonpCallbackParameterValue) {
		this.jsonpCallbackParameterValue = jsonpCallbackParameterValue;
	}

	public void setRateLimitHeadersEnabled(boolean rateLimitHeadersEnabled) {
		this.rateLimitHeadersEnabled = rateLimitHeadersEnabled;
	}

	public void setForwardedHeaders(List<ForwardHeader> forwardedHeaders) {
		this.forwardedHeaders = forwardedHeaders;
	}

	public void setReturnedHeaders(List<ReturnHeader> returnedHeaders) {
		this.returnedHeaders = returnedHeaders;
	}

	public void setMethods(List<MasheryMethod> methods) {
		this.methods = methods;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumberOfHttpRedirectsToFollow(int numberOfHttpRedirectsToFollow) {
		this.numberOfHttpRedirectsToFollow = numberOfHttpRedirectsToFollow;
	}

	public void setOutboundRequestTargetPath(String outboundRequestTargetPath) {
		this.outboundRequestTargetPath = outboundRequestTargetPath;
	}

	public void setOutboundRequestTargetQueryParameters(
			String outboundRequestTargetQueryParameters) {
		this.outboundRequestTargetQueryParameters = outboundRequestTargetQueryParameters;
	}

	public void setOutboundTransportProtocol(ProtocolType outboundTransportProtocol) {
		this.outboundTransportProtocol = outboundTransportProtocol;
	}

	public void setProcessor(MasheryProcessor processor) {
		this.processor = processor;
	}

	public void setPublicDomains(List<MasheryDomain> publicDomains) {
		this.publicDomains = publicDomains;
	}

	public void setRequestAuthenticationType(RequestAuthenticationType requestAuthenticationType) {
		this.requestAuthenticationType = requestAuthenticationType;
	}

	public void setRequestPathAlias(String requestPathAlias) {
		this.requestPathAlias = requestPathAlias;
	}

	public void setRequestProtocol(RequestProtocol requestProtocol) {
		this.requestProtocol = requestProtocol;
	}

	public void setOauthGrantTypes(List<OAuthGrantType> oauthGrantTypes) {
		this.oauthGrantTypes = oauthGrantTypes;
	}

	public void setStringsToTrimFromApiKey(String stringsToTrimFromApiKey) {
		this.stringsToTrimFromApiKey = stringsToTrimFromApiKey;
	}

	public void setSupportedHttpMethods(List<HttpMethod> supportedHttpMethods) {
		this.supportedHttpMethods = supportedHttpMethods;
	}

	public void setSystemDomainAuthentication(
			MasherySystemDomainAuthentication systemDomainAuthentication) {
		this.systemDomainAuthentication = systemDomainAuthentication;
	}

	public void setSystemDomains(List<MasheryDomain> systemDomains) {
		this.systemDomains = systemDomains;
	}

	public void setTrafficManagerDomain(String trafficManagerDomain) {
		this.trafficManagerDomain = trafficManagerDomain;
	}

	public void setUseSystemDomainCredentials(boolean useSystemDomainCredentials) {
		this.useSystemDomainCredentials = useSystemDomainCredentials;
	}

	public void setSystemDomainCredentialKey(String systemDomainCredentialKey) {
		this.systemDomainCredentialKey = systemDomainCredentialKey;
	}

	public void setSystemDomainCredentialSecret(String systemDomainCredentialSecret) {
		this.systemDomainCredentialSecret = systemDomainCredentialSecret;
	}

	public String getSystemDomainCredentialKey() {
		return systemDomainCredentialKey;
	}

	public String getSystemDomainCredentialSecret() {
		return systemDomainCredentialSecret;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@JsonIgnore
	public String getResourcePath() {
		String path = String.format(PATH, serviceId);
		if (endpointId != null)
			path = path + "/" + endpointId;

		return path;
	}

	public enum ValueLocation {
		REQUEST_PARAMETERS("request-parameters"),
		REQUEST_PATH("request-path"),
		REQUEST_BODY("request-body"),
		REQUEST_HEADER("request-header"),
		CUSTOM("custom");

		private final String string;

		private ValueLocation(String valueLocation) {
			this.string = valueLocation;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static ValueLocation fromString(String value) {
			for (ValueLocation val : ValueLocation.values()) {
				if (val.string.equalsIgnoreCase(value))
					return val;
			}
			return null;
		}
	}

	public enum ForwardHeader {
		MASHERY_HOST("mashery-host"),
		MASHERY_MESSAGE_ID("mashery-message-id"),
		MASHERY_SERVICE_ID("mashery-service-id");

		private final String string;

		private ForwardHeader(String header) {
			this.string = header;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static ForwardHeader fromString(String header) {
			for (ForwardHeader val : ForwardHeader.values()) {
				if (val.string.equalsIgnoreCase(header))
					return val;
			}
			return null;
		}
	}

	public enum ReturnHeader {
		MASHERY_MESSAGE_ID("mashery-message-id"),
		MASHERY_RESPONDER("mashery-responder");

		private final String string;

		private ReturnHeader(String header) {
			this.string = header;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static ReturnHeader fromString(String header) {
			for (ReturnHeader val : ReturnHeader.values()) {
				if (val.string.equalsIgnoreCase(header))
					return val;
			}
			return null;
		}
	}

	public enum ProtocolType {
		HTTP("http"),
		HTTPS("https"),
		ANY("any");

		private final String string;

		private ProtocolType(String protocol) {
			this.string = protocol;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static ProtocolType fromString(String protocol) {
			for (ProtocolType val : ProtocolType.values()) {
				if (val.string.equalsIgnoreCase(protocol))
					return val;
			}
			return null;
		}
	}

	public enum RequestAuthenticationType {
		API_KEY("apiKey"),
		OAuth2("oauth"),
		API_KEY_SECRET_SHA256("apiKeyAndSecret_SHA256"),
		API_KEY_SECRET_MD5("apiKeyAndSecret_MD5"),
		CUSTOM("custom");

		private final String string;

		private RequestAuthenticationType(String authType) {
			this.string = authType;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static RequestAuthenticationType fromString(String auth) {
			for (RequestAuthenticationType val : RequestAuthenticationType.values()) {
				if (val.string.equalsIgnoreCase(auth))
					return val;
			}
			return null;
		}
	}

	public enum RequestProtocol {
		REST("rest"),
		SOAP("soap"),
		XML_RPC("xml-rpc"),
		JSON_RPC("json-rpc"),
		OTHER("other");

		private final String string;

		private RequestProtocol(String protocol) {
			this.string = protocol;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static RequestProtocol fromString(String protocol) {
			for (RequestProtocol val : RequestProtocol.values()) {
				if (val.string.equalsIgnoreCase(protocol))
					return val;
			}
			return null;
		}
	}

	public enum OAuthGrantType {
		AUTH_CODE("authorization_code"),
		IMPLICIT("implicit"),
		PASSWORD("password"),
		CLIENT_CREDENTIALS("client_credentials");

		private final String string;

		private OAuthGrantType(String grantType) {
			this.string = grantType;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static OAuthGrantType fromString(String grantType) {
			for (OAuthGrantType val : OAuthGrantType.values()) {
				if (val.string.equalsIgnoreCase(grantType))
					return val;
			}
			return null;
		}
	}

	public enum HttpMethod {
		GET("get"),
		POST("post"),
		PUT("put"),
		DELETE("delete"),
		HEAD("head"),
		PATCH("patch"),
		OPTIONS("options");

		private final String string;

		private HttpMethod(String method) {
			this.string = method;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static HttpMethod fromString(String method) {
			for (HttpMethod val : HttpMethod.values()) {
				if (val.string.equalsIgnoreCase(method))
					return val;
			}
			return null;
		}
	}
}
