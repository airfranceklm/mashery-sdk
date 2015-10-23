package com.afkl.generic.mashery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.afkl.generic.mashery.model.MasheryMethod;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents an Http Client used for making API calls to Mashery
 *
 */
public class MasheryClient {

	private Logger log = LoggerFactory.getLogger(MasheryClient.class);

	// Specific Resource Paths
	private static final String TOKEN_PATH = "/v3/token";
	private static final String ROOT_PATH = "/v3/rest/";
	private static final String SERVICES_PATH = ROOT_PATH + "services";
	private static final String ENDPOINTS_PATH = SERVICES_PATH + "/%s/endpoints";
	private static final String METHODS_PATH = ENDPOINTS_PATH + "/%s/methods";
	private static final String PACKAGES_PATH = ROOT_PATH + "packages";
	private static final String PLANS_PATH = PACKAGES_PATH + "/%s/plans";
	private static final String PLAN_SERVICES_PATH = PLANS_PATH + "/%s/services";
	private static final String PLAN_ENDPOINTS_PATH = PLAN_SERVICES_PATH + "/%s/endpoints";

	private static final String FILTER_QUERY = "filter=name:";
	private static final String GENERIC_JSON_RESOURCE = "{\"id\":\"%s\"}";

	private final URIBuilder uriBuilder;

	private List<NameValuePair> oauthParamList;

	private String apiKey;

	private String apiSecret;

	// If set the client will not send any modification requests through
	private boolean readOnly;

	private static final DefaultHttpClient httpClient = new DefaultHttpClient();

	private static ObjectMapper mapper;

	public MasheryClient(MasheryClientBuilder builder) {
		//Validate all parameters are present
		if (builder.host == null) throw new NullPointerException("host");
		if (builder.protocol == null) throw new NullPointerException("protocol");
		if (builder.apiKey == null) throw new NullPointerException("apiKey");
		if (builder.apiSecret == null) throw new NullPointerException("apiSecret");
		if (builder.tokenRequestParameters == null) throw new NullPointerException("tokenRequestParameters");

		this.apiKey = builder.apiKey;
		this.apiSecret = builder.apiSecret;

		uriBuilder = new URIBuilder().setScheme(builder.protocol).setHost(builder.host);
		if (builder.port > 0)
			uriBuilder.setPort(builder.port);

		oauthParamList = new ArrayList<NameValuePair>();
		for (String key : builder.tokenRequestParameters.keySet()) {
			oauthParamList.add(new BasicNameValuePair(key, builder.tokenRequestParameters.get(key)));
		}

		// Configure ObjectMapper
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	/**
	 * To be set if this client is only for reading and should not be
	 * allowed to make any modifications to the data.
	 *
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Delete the resource specified by the path via Mashery V3 API.
	 *
	 * @param path - the path specifying the resource to delete.
	 * @return - true iff delete was successful
	 */
	public boolean deleteResource(String path) {

		if (isEmpty(path)) {
			log.error("Cannot delete resource, path is empty");
			return false;
		}

		if (readOnly) {
			log.error("Attempted Delete operation. User has read only permissions");
			return false;
		}

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}
		HttpDelete delete = null;
		try {
			delete = new HttpDelete(uriBuilder.setPath(path).build());
		} catch (URISyntaxException e) {
			log.error("Cannot delete Resource, incorrect URI.");
			return false;
		}
		delete.addHeader(createAuthHeader(token));
		log.info("Deleting Resouce. " + path);

		HttpResponse response = null;
		int statusCode = 0;
		try {
			response = httpClient.execute(delete);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			log.error("Error deleting resource: " + e);
			return false;
		} catch (IOException e) {
			log.error("Error deleting resource: " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}

		if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
			OAuthTokenService.INSTANCE.clearToken();
		}

		return statusCode == HttpStatus.SC_OK;
	}

	/**
	 * Create the Mashery Resource via Mashery V3 API
	 *
	 * @param resource - the resource to be created
	 * @return - the Unique ID of the created resource
	 */
	public String createResource(MasheryResource resource) {

		if (resource == null) {
			log.error("Cannot create resource, resource is null");
			return null;
		}

		if (readOnly) {
			log.error("Attempted Create operation. User has read only permissions");
			return null;
		}

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return null;
		}
		HttpPost post;
		String deployableAsString = null;
		try {
			post = new HttpPost(uriBuilder.setPath(resource.getResourcePath()).build());
			deployableAsString = mapper.writeValueAsString(resource);
			log.info("Request BODY: " + deployableAsString);
			post.setEntity(new StringEntity(deployableAsString));
			post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			post.addHeader(createAuthHeader(token));
		} catch (URISyntaxException e) {
			log.error("Error creating resource: " + e);
			return null;
		} catch (JsonProcessingException e) {
			log.error("Error creating resource: " + e);
			return null;
		} catch (UnsupportedEncodingException e) {
			log.error("Error creating resource: " + e);
			return null;
		}

		log.info("Creating new resource " + resource.getResourcePath());

		HttpResponse response = null;
		String uniqueId = null;
		try {
			response = httpClient.execute(post);

			if (response == null) {
				return null;
			}

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
				OAuthTokenService.INSTANCE.clearToken();
				return null;
			} else if (statusCode != HttpStatus.SC_OK) {
				log.error("Response error: " + response.getStatusLine().getStatusCode());
				log.error(retrieveErrorFromResponse(response));
				return null;
			}

			if (response.getEntity() != null) {
				JsonNode jsonResponse = mapper.readTree(response.getEntity().getContent());
				uniqueId = retrieveIdFromResponse(jsonResponse, "");
			}
		} catch (ClientProtocolException e) {
			log.error("Error creating resource: " + e);
		} catch (IOException e) {
			log.error("Error creating resource: " + e);
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					//Ignore
				}
		}

		log.info("Unique ID of Resource: " + uniqueId);
		return uniqueId;
	}


	/**
	 * Modify the existing Mashery Resource via Mashery V3 API.
	 *
	 * @param resource - the resource to be modified with the parameters to be changed.
	 * @return - true iff the modify was successful.
	 */
	public boolean modifyResource(MasheryResource resource) {

		if (resource == null) {
			log.error("Cannot modify resource, resource is null");
			return false;
		}

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}
		HttpPut put = null;
		String deployableAsString = null;
		try {
			put = new HttpPut(uriBuilder.setPath(resource.getResourcePath()).build());
			deployableAsString = mapper.writeValueAsString(resource);
			log.info("Sending PUT request to:" + put.getURI().toString() + " Payload: " + deployableAsString);
			put.setEntity(new StringEntity(deployableAsString));
			put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			put.addHeader(createAuthHeader(token));
		} catch (URISyntaxException e) {
			log.error("Error modifying resource. " + e);
			return false;
		} catch (JsonProcessingException e) {
			log.error("Error modifying resource. " + e);
			return false;
		} catch (UnsupportedEncodingException e) {
			log.error("Error modifying resource. " + e);
			return false;
		}

		HttpResponse response = null;
		int statusCode = 0;
		try {
			response = httpClient.execute(put);
			if (response == null) {
				log.error("Could not modify resource. No response from API.");
				return false;
			}

			statusCode = response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			log.error("Error modifying resource. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error modifying resource. " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}

		if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
			log.error("Invalid Token, please retry.");
			OAuthTokenService.INSTANCE.clearToken();
		}

		if (statusCode != HttpStatus.SC_OK && response != null) {
			log.error("Response error: " + response.getStatusLine().getStatusCode());
			log.error(retrieveErrorFromResponse(response));
			return false;
		}

		return true;
	}

	/**
	 * Fetch a valid OAuth Access token to be used for Mashery V3 API calls.
	 *
	 * @return - valid access token.
	 */
	private String retrieveOauthToken() {

		String token = OAuthTokenService.INSTANCE.retrieveToken();
		if (token != null)
			return token;

		JsonNode responseNode = null;
		HttpResponse oauthResponse = null;
		try {
			HttpPost tokenPost = new HttpPost(uriBuilder.setPath(TOKEN_PATH).build());
			tokenPost.setEntity(new UrlEncodedFormEntity(oauthParamList));
			tokenPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

			String basicAuth = Base64.encodeBase64String((apiKey + ":" + apiSecret).getBytes());
			tokenPost.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

			log.info("Sending OAuth request to: " + tokenPost.getURI().toString());

			oauthResponse = httpClient.execute(tokenPost);

			if (oauthResponse == null || oauthResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if (oauthResponse != null) {
					log.error("OAuth Token Response Status: " + oauthResponse.getStatusLine().getStatusCode());
					log.error(retrieveErrorFromResponse(oauthResponse));
				}
				return null;
			}
			HttpEntity body = oauthResponse.getEntity();
			responseNode = mapper.readTree(body.getContent());
		} catch (URISyntaxException e) {
			log.error("Unable to fetch OAuth token " + e);
			return null;
		} catch (JsonProcessingException e) {
			log.error("Unable to fetch OAuth token " + e);
			return null;
		} catch (IllegalStateException e) {
			log.error("Unable to fetch OAuth token " + e);
			return null;
		} catch (IOException e) {
			log.error("Unable to fetch OAuth token " + e);
			return null;
		} finally {
			// Reset credentials
			httpClient.getCredentialsProvider().clear();

			if (oauthResponse != null)
				try {
					EntityUtils.consume(oauthResponse.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}

		String accessToken = responseNode.get("access_token").asText();
		int ttl = responseNode.get("expires_in").asInt();

		OAuthTokenService.INSTANCE.putToken(accessToken, ttl);

		return accessToken;
	}

	/**
	 * Generic add Endpoint to Plan. Will search for package with same name as service.
	 *  Containing Plan named `default`
	 *
	 * @param serviceName - Name of Service containing the endpoint
	 * @param endpointName - Name of the Endpoint
	 * @param packageName - name of package containing plan to be updated
	 * @param planName - plan name where endpoint will be added
	 * @return
	 */
	public boolean addEndpointToPlan(String endpointName, String planName, String serviceName, String packageName) {

		// Validate Params are non empty
		if (isEmpty(endpointName) || isEmpty(planName) || isEmpty(serviceName) || isEmpty(endpointName))
			return false;

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}
		String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
		String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
		String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
		String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

		String planServicePath = String.format(PLAN_SERVICES_PATH, packageId, planId);
		HttpResponse response = null;
		uriBuilder.removeQuery();
		try {
			HttpPost post = new HttpPost(uriBuilder.setPath(planServicePath).build());
			post.addHeader(createAuthHeader(token));
			post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			post.setEntity(new StringEntity(String.format(GENERIC_JSON_RESOURCE, serviceId)));
			response = httpClient.execute(post);

			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if (response != null) {
					String responseError = retrieveErrorFromResponse(response);
					// Check if Error message is 'Service already exists in the Plan'
					if (!responseError.contains("already exists")) {
						log.error("Error creating plan service " + planServicePath + ". Status code: " + response.getStatusLine().getStatusCode());
						log.error("Error Response: " + responseError);
						return false;
					}
				} else {
					log.error("Error creating plan service " + planServicePath);
					return false;
				}
			}

			try {
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				// Ignore
			}

			uriBuilder.removeQuery();
			post = new HttpPost(uriBuilder.setPath(planServicePath + "/" + serviceId + "/endpoints").build());
			post.addHeader(createAuthHeader(token));
			post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			post.setEntity(new StringEntity(String.format(GENERIC_JSON_RESOURCE, endpointId)));
			response = httpClient.execute(post);

			// Check if Error message is 'Endpoint already exists in the Plan'
			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if (response != null) {
					String responseError = retrieveErrorFromResponse(response);
					log.error("Error creating plan endpoint " + post.getURI().toString() + " Status code: " + response.getStatusLine().getStatusCode());
					log.error("Error Response: " + responseError);
				} else {
					log.error("Error creating plan endpoint " + endpointId);
				}
				return false;
			}
		} catch (ClientProtocolException e) {
			log.error("Error adding endpoint to plan. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error adding endpoint to plan. " + e);
			return false;
		} catch (URISyntaxException e) {
			log.error("Error adding endpoint to plan. " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}
		return true;
	}

	public boolean addMethodToPlan(String methodName, String endpointName, String planName, String serviceName, String packageName) {
		// Validate Params are non empty
		if (isEmpty(methodName) || isEmpty(endpointName) || isEmpty(planName) || isEmpty(serviceName))
			return false;

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
		String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
		String methodId = determineResourceIdFromName(methodName, String.format(METHODS_PATH, serviceId, endpointId));
		String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
		String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

		String planMethodPath = String.format(PLAN_ENDPOINTS_PATH + "/%s", packageId, planId, serviceId, endpointId);

		String resource = fetchResource(planMethodPath, "fields=methods");
		if (resource == null) {
			log.error("Specified Endpoint, " + endpointName + ", is not associated with the Plan " + planName);
			return false;
		}

		// Create method Node
		ObjectNode methodNode = mapper.createObjectNode();
		methodNode.put("id", methodId);
		methodNode.put("name", methodName);

		return addMethodNode(methodNode, resource, planMethodPath, endpointId);
	}

	private boolean addMethodNode(ObjectNode methodNode, String resource, String planMethodPath, String endpointId) {

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}

		JsonNode responseNode = null;
		try {
			responseNode = mapper.readTree(resource);
			JsonNode methodsNode = responseNode.get("methods");
			if (methodsNode == null) {
				log.error("Error adding method to plan.");
				return false;
			}
			((ArrayNode) methodsNode).add(methodNode);
		} catch (JsonProcessingException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		}

		uriBuilder.removeQuery();
		HttpPut put = null;
		try {
			put = new HttpPut(uriBuilder.setPath(planMethodPath).build());
			put.addHeader(createAuthHeader(token));
			put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			String putBody = mapper.writeValueAsString(responseNode);
			put.setEntity(new StringEntity(putBody));
		}  catch (URISyntaxException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		} catch (UnsupportedEncodingException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		} catch (JsonProcessingException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		}

		HttpResponse response = null;
		try {
			response = httpClient.execute(put);

			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if (response != null) {
					String responseError = retrieveErrorFromResponse(response);
					log.error("Error creating plan method " + put.getURI().toString() + " Status code: " + response.getStatusLine().getStatusCode());
					log.error("Error Response: " + responseError);
				} else {
					log.error("Error creating plan method " + endpointId);
				}
				return false;
			}

		} catch (ClientProtocolException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error adding method to plan. " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}

		return true;
	}

	public boolean addMethodToPlan(MasheryMethod method, String planName, String serviceName, String packageName, String endpointName) {
		// Validate Params are non empty
		if (isEmpty(method.getName()) || isEmpty(planName) || isEmpty(serviceName) || isEmpty(endpointName))
			return false;

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
		String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
		String methodId = determineResourceIdFromName(method.getName(), String.format(METHODS_PATH, serviceId, endpointId));
		String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
		String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

		String planMethodPath = String.format(PLAN_ENDPOINTS_PATH + "/%s", packageId, planId, serviceId, endpointId);

		String resource = fetchResource(planMethodPath, "fields=methods");
		if (resource == null) {
			log.error("Specified Method, " + method.getName() + ", is not associated with the Plan " + planName);
			return false;
		}

		method.setId(methodId);
		ObjectNode methodNode = mapper.valueToTree(method);

		return addMethodNode(methodNode, resource, planMethodPath, endpointId);
	}

	/**
	 * Convert the JSON error response to a string
	 * @param response containing json error
	 * @return string representation of the error.
	 */
	private static String retrieveErrorFromResponse(HttpResponse response) {
		Scanner scan = null;
		try {
			scan = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
			return scan.hasNext() ? scan.next() : "";
		} catch (IllegalStateException e) {
			// Ignore
		} catch (IOException e) {
			// Ignore
		} finally {
			if (scan != null)
				scan.close();
		}

		return null;
	}

	// TODO Remove this if not used.
	private String retrieveErrorAsJson(HttpResponse response) {
		StringBuilder buf = new StringBuilder();
		try {
			JsonNode responseNode = mapper.readTree(response.getEntity().getContent());
			buf.append("Error Code: ").append(responseNode.get("errorCode").asInt());
			buf.append(". Error Message: ").append(responseNode.get("errorMessage"));
			JsonNode errorsNode = responseNode.get("errors");
			if (errorsNode != null && errorsNode.isArray()) {
				buf.append("Error: ");
				buf.append(responseNode.toString());
			}
		} catch (JsonProcessingException e) {
			log.error("Error retrieving error. Error-ception!");
		} catch (IllegalStateException e) {
			log.error("Error retrieving error. Error-ception!");
		} catch (IOException e) {
			log.error("Error retrieving error. Error-ception!");
		}
		return buf.toString();
	}

	public boolean removeEndpointFromPlan(String endpointName, String planName, String serviceName, String packageName) {

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
		String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
		String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
		String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

		String planServicePath = String.format(PLAN_SERVICES_PATH, packageId, planId);

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}

		HttpResponse response = null;
		try {
			// We will never need to remove the plan service.
			uriBuilder.removeQuery();
			HttpDelete delete = new HttpDelete(uriBuilder.setPath(planServicePath + "/" + serviceId + "/endpoints/" + endpointId).build());
			delete.addHeader(createAuthHeader(token));
			response = httpClient.execute(delete);

			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return false;

		} catch (ClientProtocolException e) {
			log.error("Error removing endpoint from plan. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error removing endpoint from plan. " + e);
			return false;
		} catch (URISyntaxException e) {
			log.error("Error removing endpoint from plan. " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}
		return true;
	}

	public boolean removeMethodFromPlan(String methodName, String endpointName, String planName, String serviceName, String packageName) {

		if (readOnly) {
			log.error("Attempted Modify operation. User has read only permissions");
			return false;
		}

		String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
		String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
		String methodId = determineResourceIdFromName(methodName, String.format(METHODS_PATH, serviceId, endpointId));
		String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
		String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

		String planMethodPath = String.format(PLAN_ENDPOINTS_PATH + "/%s", packageId, planId, serviceId, endpointId);

		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return false;
		}

		HttpResponse response = null;
		try {
			uriBuilder.removeQuery();
			HttpDelete delete = new HttpDelete(uriBuilder.setPath(planMethodPath + "/methods/" + methodId).build());
			delete.addHeader(createAuthHeader(token));
			response = httpClient.execute(delete);

			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return false;

		} catch (ClientProtocolException e) {
			log.error("Error removing method from plan. " + e);
			return false;
		} catch (IOException e) {
			log.error("Error removing method from plan. " + e);
			return false;
		} catch (URISyntaxException e) {
			log.error("Error removing method from plan. " + e);
			return false;
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}
		return true;
	}

	/**
	 * @param resourceName - the name of the resource to lookup
	 * @param path - that api path where to find the resource
	 * @return the unique Id of the resource
	 */
	private String determineResourceIdFromName(String resourceName, String path) {
		JsonNode responseNode = null;
		try {
			String resource= fetchResource(path, FILTER_QUERY + URLEncoder.encode(resourceName, "UTF-8"));
			if (isEmpty(resource))
				return null;

			responseNode = mapper.readTree(resource);
		} catch (UnsupportedEncodingException e) {
			log.error("Error encoding resource name: " + resourceName);
			return null;
		} catch (JsonProcessingException e) {
			log.error("Error encoding resource name: " + resourceName);
			return null;
		} catch (IOException e) {
			log.error("Error encoding resource name: " + resourceName);
			return null;
		}

		return retrieveIdFromResponse(responseNode, resourceName);
	}

	/**
	 * Fetch generic resource via Mashery from the specified path and query
	 * @param path - location of the resource
	 * @param query - query param representing extra filter on resource fetch
	 * @return response body as http entity
	 */
	public String fetchResource(String path, String query) {

		if (isEmpty(path)) {
			log.error("Cannot fetch resource, Path is null");
			return null;
		}
		String token = retrieveOauthToken();
		if (token == null) {
			log.error("Unable to retrieve OAuth token.");
			return null;
		}

		HttpResponse response = null;
		uriBuilder.setPath(path);
		if (!isEmpty(query))
			uriBuilder.setQuery(query);
		else
			uriBuilder.removeQuery();

		try {
			HttpGet get = new HttpGet(uriBuilder.build());
			get.addHeader(createAuthHeader(token));
			response = httpClient.execute(get);

			if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.error("Error retrieving resource :" + get.getURI().toString());
			} else if (response.getEntity() != null) {
				return IOUtils.toString(response.getEntity().getContent());
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Error retrieving resource. " + e);
		} catch (URISyntaxException e) {
			log.error("Error retrieving resource. " + e);
		} catch (ClientProtocolException e) {
			log.error("Error retrieving resource. " + e);
		} catch (IOException e) {
			log.error("Error retrieving resource. " + e);
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
		}
		return null;
	}

	/**
	 * Retrieve the Unique Identifier from response body.
	 *
	 * @param entity - response body which contains unique id
	 * @return - Unique ID
	 */
	private String retrieveIdFromResponse(JsonNode responseNode, String name) {

		String id = null;
		JsonNode node = null;

		// Response will come back as array of one element
		if (responseNode != null && responseNode.isArray()) {
			ArrayNode arrayOfNodes = ((ArrayNode) responseNode);
			for (int i = 0; i < arrayOfNodes.size(); i++) {
				JsonNode nameNode = arrayOfNodes.get(i).get("name");
				if (!nameNode.isNull() && nameNode.textValue().equals(name)) {
					node = arrayOfNodes.get(i);
				}
			}
			if (node == null)
				node = arrayOfNodes.get(0);
		} else
			node = responseNode;

		if (node != null && !node.get("id").isNull())
			id = node.get("id").textValue();

		return id;
	}

	/**
	 * Util method to create a Auth header with Bearer token
	 * @param token - token to be included
	 * @return Header object for Bearer Auth
	 */
	private static Header createAuthHeader(String token) {
		return new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
	}

	/**
	 * Utility method to check for empty string
	 * @param toCheck string to check
	 * @return true if string is null, has length 0 or contains only whitespace
	 */
	private static boolean isEmpty(String toCheck) {
		return toCheck == null || toCheck.trim().length() == 0;
	}
}