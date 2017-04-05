package com.afkl.generic.mashery;

import com.afkl.generic.mashery.model.MasheryMethod;
import com.afkl.generic.mashery.model.MasheryPackage;
import com.afkl.generic.mashery.model.MasheryService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Http Client used for making API calls to Mashery
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

    private static final String SERVICE_FIELDS = "fields=id,name,created,updated,editorHandle,revisionNumber,robotsPolicy,crossdomainPolicy,description,cache.cacheTtl,errorSets,qpsLimitOverall,rfc3986Encode,securityProfile,version";
    private static final String PACKAGE_FIELDS = "fields=name,id,description,created,updated,keyAdapter,keyLength,nearQuotaThreshold,notifyAdminEmails,notifyAdminPeriod,notifyDeveloperPeriod,plans,sharedSecretLength,notifyAdminNearQuota,notifyAdminOverQuota,notifyAdminOverThrottle,notifyDeveloperNearQuota,notifyDeveloperOverQuota,notifyDeveloperOverThrottle";

    private final URIBuilder uriBuilder;

    private List<NameValuePair> oauthParamList;

    private String apiKey;

    private String apiSecret;

    // AreaUuid
    private String apiScope;

    // If set the client will not send any modification requests through
    private boolean readOnly;

    private static final DefaultHttpClient httpClient = new DefaultHttpClient();

    private static ObjectMapper mapper;

    private String newLine = System.getProperty("line.separator");

    public MasheryClient(MasheryClientBuilder builder) {
        // Validate all parameters are present
        if (builder.host == null)
            throw new NullPointerException("host");
        if (builder.protocol == null)
            throw new NullPointerException("protocol");
        if (builder.apiKey == null)
            throw new NullPointerException("apiKey");
        if (builder.apiSecret == null)
            throw new NullPointerException("apiSecret");
        if (builder.tokenRequestParameters == null)
            throw new NullPointerException("tokenRequestParameters");

        this.apiKey = builder.apiKey;
        this.apiSecret = builder.apiSecret;

        uriBuilder = new URIBuilder().setScheme(builder.protocol).setHost(builder.host);
        if (builder.port > 0)
            uriBuilder.setPort(builder.port);

        oauthParamList = new ArrayList<NameValuePair>();
        for (String key : builder.tokenRequestParameters.keySet()) {
            oauthParamList.add(new BasicNameValuePair(key, builder.tokenRequestParameters.get(key)));
            if (MasheryUtils.isEqual(key, "scope"))
                this.apiScope = builder.tokenRequestParameters.get(key);
        }

        // Configure ObjectMapper
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * To be set if this client is only for reading and should not be allowed to make any modifications to the data.
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
    public MasheryApiResponse deleteResource(String path) {

        if (MasheryUtils.isEmpty(path)) {
            log.error("Cannot delete resource, path is empty");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Delete Resource.");
        }

        if (readOnly) {
            log.error("Attempted Delete operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Delete Resource.");
        }

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
        }
        HttpDelete delete = null;
        try {
            delete = new HttpDelete(uriBuilder.setPath(path).build());
        }
        catch (URISyntaxException e) {
            log.error("Cannot delete Resource, incorrect URI.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Cannot delete Resource. " + MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        delete.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
        log.info("Deleting Resource. " + path);

        HttpResponse response = null;
        int statusCode = 0;
        try {
            response = httpClient.execute(delete);

            if (response == null) {
                log.error("Could not delete resource. No response from API.");
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Delete Resource.");
            }

            statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                OAuthTokenService.INSTANCE.clearToken();
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.TOKEN_UNAUTHORIZED.getDescription());
            } else if (statusCode != HttpStatus.SC_OK) {
                log.error("Response error: " + response.getStatusLine().getStatusCode());
                String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                log.error(errorInformationInResponse);
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ":" + newLine
                                + MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper) + newLine + "Response Status: " + response.getStatusLine().getStatusCode());
            }

        }
        catch (ClientProtocolException e) {
            log.error("Error deleting resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error deleting resource ." + MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error deleting resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error deleting resource ." + MasheryClientError.IO_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", statusCode == HttpStatus.SC_OK, null);
    }

    /**
     * Create the Mashery Resource via Mashery V3 API
     *
     * @param resource - the resource to be created
     * @return - the Unique ID of the created resource
     */
    public MasheryApiResponse createResourceAndReturnId(MasheryResource resource) {

        if (resource == null) {
            log.error("Cannot create resource, resource is null");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Create Resource.");
        }

        if (readOnly) {
            log.error("Attempted Create operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Create Resource.");
        }

        String[] tokenResponse = retrieveOauthToken();

        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
        }
        HttpPost post;
        String deployableAsString = null;
        try {
            post = new HttpPost(uriBuilder.setPath(resource.getResourcePath()).build());
            deployableAsString = mapper.writeValueAsString(resource);
            log.info("Request BODY: " + deployableAsString);
            post.setEntity(new StringEntity(deployableAsString));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
        }
        catch (URISyntaxException e) {
            log.error("Error creating resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error creating resource: " + MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (JsonProcessingException e) {
            log.error("Error creating resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error creating resource: " + MasheryClientError.JSON_PROCESSING_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error creating resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error creating resource: " + MasheryClientError.UNSUPPORTED_ENCODING_EXCEPTION.getDescription() + newLine + e.getMessage());
        }

        log.info("Creating new resource " + resource.getResourcePath());

        HttpResponse response = null;
        String uniqueId = null;
        try {
            response = httpClient.execute(post);

            if (response == null) {
                log.error("Could not create resource. No response from API.");
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Create Resource.");
            }

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                OAuthTokenService.INSTANCE.clearToken();
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.TOKEN_UNAUTHORIZED.getDescription());
            } else if (statusCode != HttpStatus.SC_OK) {
                log.error("Response error: " + response.getStatusLine().getStatusCode());
                String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                log.error(errorInformationInResponse);
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ":" + newLine
                                + MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper) + newLine + "Response Status: " + response.getStatusLine().getStatusCode());
            }

            if (response.getEntity() != null) {
                JsonNode jsonResponse = mapper.readTree(response.getEntity().getContent());
                uniqueId = MasheryUtils.retrieveIdFromResponse(jsonResponse, "");
            }
        }
        catch (ClientProtocolException e) {
            log.error("Error creating resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error creating resource: " + MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error creating resource: " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error creating resource: " + MasheryClientError.IO_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }

        log.info("Unique ID of Resource: " + uniqueId);
        return MasheryApiResponse.MasheryApiResponseBuilder().build(uniqueId, true, null);
    }

    /**
     * Modify the existing Mashery Resource via Mashery V3 API.
     *
     * @param resource - the resource to be modified with the parameters to be changed.
     * @return - true if the modify was successful.
     */
    public MasheryApiResponse modifyResource(MasheryResource resource) {

        if (resource == null) {
            log.error("Cannot modify resource, resource is null");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Modify Resource");
        }

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Modify Resource");
        }

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
        }
        HttpPut put = null;
        String deployableAsString = null;
        try {
            put = new HttpPut(uriBuilder.setPath(resource.getResourcePath()).build());
            deployableAsString = mapper.writeValueAsString(resource);
            log.info("Sending PUT request to:" + put.getURI().toString() + " Payload: " + deployableAsString);
            put.setEntity(new StringEntity(deployableAsString));
            put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            put.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
        }
        catch (URISyntaxException e) {
            log.error("Error modifying resource. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error modifying resource. " + MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (JsonProcessingException e) {
            log.error("Error modifying resource. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error modifying resource. " + MasheryClientError.JSON_PROCESSING_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error modifying resource. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error modifying resource. " + MasheryClientError.UNSUPPORTED_ENCODING_EXCEPTION.getDescription() + newLine + e.getMessage());
        }

        HttpResponse response = null;
        int statusCode = 0;
        try {
            response = httpClient.execute(put);
            if (response == null) {
                log.error("Could not modify resource. No response from API.");
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription());
            }

            statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                log.error("Invalid Token, please retry.");
                OAuthTokenService.INSTANCE.clearToken();
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.TOKEN_UNAUTHORIZED.getDescription().toString());
            }

            if (statusCode != HttpStatus.SC_OK && response != null) {
                log.error("Response error: " + response.getStatusLine().getStatusCode());
                String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                log.error(errorInformationInResponse);
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ":" + newLine
                                + MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper) + newLine + "Response Status: " + response.getStatusLine().getStatusCode());
            }

        }
        catch (ClientProtocolException e) {
            log.error("Error modifying resource. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error modifying resource. " + MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error modifying resource. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error modifying resource. " + MasheryClientError.IO_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", statusCode == HttpStatus.SC_OK, null);
    }

    /**
     * Fetch a valid OAuth Access token to be used for Mashery V3 API calls.
     *
     * @return - valid access token.
     */
    private String[] retrieveOauthToken() {

        String token = OAuthTokenService.INSTANCE.retrieveToken(apiScope);
        if (token != null)
            // return token with no error message example : {12356,null};
            return new String[] {token, null};
        log.info("Either existing cached token expired OR belongs to different Area ID. So trying to retrieve new access token for given Area ID..");
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
                    log.error(MasheryUtils.retrieveErrorFromResponse(oauthResponse));
                    return new String[] {null, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ". Response Status: " + oauthResponse.getStatusLine().getStatusCode() + newLine + "Error Response : "
                                    + newLine + oauthResponse.toString()};
                }
                return new String[] {null, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription()};
            }
            HttpEntity body = oauthResponse.getEntity();
            responseNode = mapper.readTree(body.getContent());
        }
        catch (URISyntaxException e) {
            log.error("Unable to fetch OAuth token " + e);
            return new String[] {null, "Unable to fetch OAuth token " + MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + newLine + e.getMessage()};
            // return null;
        }
        catch (JsonProcessingException e) {
            log.error("Unable to fetch OAuth token " + e);
            return new String[] {null, "Unable to fetch OAuth token " + MasheryClientError.JSON_PROCESSING_EXCEPTION.getDescription() + newLine + e.getMessage()};
            // return null;
        }
        catch (IllegalStateException e) {
            log.error("Unable to fetch OAuth token " + e);
            return new String[] {null, "Unable to fetch OAuth token " + MasheryClientError.ILLEGAL_STATE_EXCEPTION.getDescription() + newLine + e.getMessage()};
            // return null;
        }
        catch (IOException e) {
            log.error("Unable to fetch OAuth token " + e);
            return new String[] {null, "Unable to fetch OAuth token " + MasheryClientError.IO_EXCEPTION.getDescription() + newLine + e.getMessage()};
            // return null;
        }
        finally {
            // Reset credentials
            httpClient.getCredentialsProvider().clear();

            if (oauthResponse != null)
                try {
                    EntityUtils.consume(oauthResponse.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }

        String accessToken = responseNode.get("access_token").asText();
        int ttl = responseNode.get("expires_in").asInt();
        String scope = responseNode.get("scope").asText();

        OAuthTokenService.INSTANCE.putToken(accessToken, ttl, scope);

        // return accessToken;
        return new String[] {accessToken, null};
    }

    /**
     * Generic add Endpoint to Plan. Will search for package with same name as service. Containing Plan named `default`
     *
     * @param serviceName  - Name of Service containing the endpoint
     * @param endpointName - Name of the Endpoint
     * @param packageName  - name of package containing plan to be updated
     * @param planName     - plan name where endpoint will be added
     * @return masheryApiResponse - response object with status and error details
     */
    public MasheryApiResponse addEndpointToPlan(String endpointName, String planName, String serviceName, String packageName) {

        // Validate Params are non empty
        if (MasheryUtils.isEmpty(endpointName) || MasheryUtils.isEmpty(planName) || MasheryUtils.isEmpty(serviceName) || MasheryUtils.isEmpty(endpointName))
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Add Endpoint To Plan");
        // return false;

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Add Endpoint To Plan");
            // return false;
        }

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
            // return false;
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
            post.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setEntity(new StringEntity(String.format(GENERIC_JSON_RESOURCE, serviceId)));
            response = httpClient.execute(post);

            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (response != null) {
                    String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                    // Check if Error message is 'Service already exists in the Plan'
                    if (!errorInformationInResponse.contains("already exists")) {
                        log.error("Response error: " + response.getStatusLine().getStatusCode());
                        log.error(errorInformationInResponse);
                        return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ":" + newLine
                                        + MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper) + newLine + "Response Status: " + response.getStatusLine().getStatusCode());
                    }
                } else {
                    log.error("Error creating plan service " + planServicePath);
                    return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Create plan Service -" + planServicePath);
                }
            }

            try {
                EntityUtils.consume(response.getEntity());
            }
            catch (IOException e) {
                // Ignore
            }

            uriBuilder.removeQuery();
            post = new HttpPost(uriBuilder.setPath(planServicePath + "/" + serviceId + "/endpoints").build());
            post.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setEntity(new StringEntity(String.format(GENERIC_JSON_RESOURCE, endpointId)));
            response = httpClient.execute(post);

            // Check if Error message is 'Endpoint already exists in the Plan'
            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (response != null) {
                    log.error("Error creating plan endpoint " + post.getURI().toString() + " Status code: " + response.getStatusLine().getStatusCode());
                    String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                    log.error(errorInformationInResponse);
                    return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false,
                                    MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + ":" + newLine + MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper) + newLine
                                                    + "Error creating plan endpoint -" + post.getURI().toString() + " Status code: " + response.getStatusLine().getStatusCode());
                } else {
                    log.error("Error creating plan endpoint " + endpointId);
                    return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Error creating plan endpoint -" + endpointId);
                }
            }
        }
        catch (ClientProtocolException e) {
            log.error("Error adding endpoint to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error adding endpoint to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.IO_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        catch (URISyntaxException e) {
            log.error("Error adding endpoint to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + newLine + e.getMessage());
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", true, null);
    }

    public MasheryApiResponse addMethodToPlan(String methodName, String endpointName, String planName, String serviceName, String packageName) {
        // Validate Params are non empty
        if (MasheryUtils.isEmpty(methodName) || MasheryUtils.isEmpty(endpointName) || MasheryUtils.isEmpty(planName) || MasheryUtils.isEmpty(serviceName))
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Add Method To Plan");
        // return false;

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Add Method To Plan");
            // return false;
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
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Specified Endpoint, " + endpointName + ", is not associated with the Plan " + planName);
            // return false;
        }

        // Create method Node
        ObjectNode methodNode = mapper.createObjectNode();
        methodNode.put("id", methodId);
        methodNode.put("name", methodName);

        return addMethodNode(methodNode, resource, planMethodPath, endpointId);
    }

    private MasheryApiResponse addMethodNode(ObjectNode methodNode, String resource, String planMethodPath, String endpointId) {

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
            // return false;
        }

        JsonNode responseNode = null;
        try {
            responseNode = mapper.readTree(resource);
            JsonNode methodsNode = responseNode.get("methods");
            if (methodsNode == null) {
                log.error("Error adding method to plan.");
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Error adding method to plan.");
                // return false;
            }
            ((ArrayNode) methodsNode).add(methodNode);
        }
        catch (JsonProcessingException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.JSON_PROCESSING_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }
        catch (IOException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.IO_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }

        uriBuilder.removeQuery();
        HttpPut put = null;
        try {
            put = new HttpPut(uriBuilder.setPath(planMethodPath).build());
            put.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            String putBody = mapper.writeValueAsString(responseNode);
            put.setEntity(new StringEntity(putBody));
        }
        catch (URISyntaxException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.UNSUPPORTED_ENCODING_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }
        catch (JsonProcessingException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.JSON_PROCESSING_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }

        HttpResponse response = null;
        try {
            response = httpClient.execute(put);

            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (response != null) {
                    String responseError = MasheryUtils.retrieveErrorFromResponse(response);
                    log.error("Error creating plan method " + put.getURI().toString() + " Status code: " + response.getStatusLine().getStatusCode());
                    log.error("Error Response: " + responseError);
                    return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false,
                                    MasheryClientError.RESPONSE_ERROR_FROM_API.getDescription() + "Error creating plan method" + ". Status code: " + response.getStatusLine().getStatusCode());
                } else {
                    log.error("Error creating plan method " + endpointId);
                    return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Error creating plan method -" + endpointId);
                }
                // return false;
            }

        }
        catch (ClientProtocolException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
            // return false;
        }
        catch (IOException e) {
            log.error("Error adding method to plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.IO_EXCEPTION.getDescription() + "Error adding method to plan. " + e.getMessage());
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }

        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", true, null);
    }

    public MasheryApiResponse addMethodToPlan(MasheryMethod method, String planName, String serviceName, String packageName, String endpointName) {
        // Validate Params are non empty
        if (MasheryUtils.isEmpty(method.getName()) || MasheryUtils.isEmpty(planName) || MasheryUtils.isEmpty(serviceName) || MasheryUtils.isEmpty(endpointName))
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.RESOURCE_PATH_EMPTY.getDescription() + "Add method To Plan");
        // return false;

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Add method To Plan");
            // return false;
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
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, "Specified Method, " + method.getName() + ", is not associated with the Plan " + planName);
            // return false;
        }

        method.setId(methodId);
        ObjectNode methodNode = mapper.valueToTree(method);

        return addMethodNode(methodNode, resource, planMethodPath, endpointId);
    }

    public MasheryApiResponse removeEndpointFromPlan(String endpointName, String planName, String serviceName, String packageName) {

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Remove Endpoint From Plan");
            // return false;
        }

        String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
        String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
        String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
        String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

        String planServicePath = String.format(PLAN_SERVICES_PATH, packageId, planId);

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
            // return false;
        }

        HttpResponse response = null;
        try {
            // We will never need to remove the plan service.
            uriBuilder.removeQuery();
            HttpDelete delete = new HttpDelete(uriBuilder.setPath(planServicePath + "/" + serviceId + "/endpoints/" + endpointId).build());
            delete.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            response = httpClient.execute(delete);

            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Remove Endpoint From Plan");
            // return false;

        }
        catch (ClientProtocolException e) {
            log.error("Error removing endpoint from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + "Error removing endpoint from plan. " + e.getMessage());
            // return false;
        }
        catch (IOException e) {
            log.error("Error removing endpoint from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.IO_EXCEPTION.getDescription() + "Error removing endpoint from plan. " + e.getMessage());
            // return false;
        }
        catch (URISyntaxException e) {
            log.error("Error removing endpoint from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + "Error removing endpoint from plan. " + e.getMessage());
            // return false;
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", true, null);
    }

    public MasheryApiResponse removeMethodFromPlan(String methodName, String endpointName, String planName, String serviceName, String packageName) {

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription() + "Remove Method from Plan");
            // return false;
        }

        String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
        String endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, serviceId));
        String methodId = determineResourceIdFromName(methodName, String.format(METHODS_PATH, serviceId, endpointId));
        String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
        String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

        String planMethodPath = String.format(PLAN_ENDPOINTS_PATH + "/%s", packageId, planId, serviceId, endpointId);

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_OAUTH_RETRIEVED.getDescription() + newLine + tokenResponse[1]);
            // return false;
        }

        HttpResponse response = null;
        try {
            uriBuilder.removeQuery();
            HttpDelete delete = new HttpDelete(uriBuilder.setPath(planMethodPath + "/methods/" + methodId).build());
            delete.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            response = httpClient.execute(delete);

            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.NO_RESPONSE_FROM_API.getDescription() + "Remove Method from Plan");
            // return false;

        }
        catch (ClientProtocolException e) {
            log.error("Error removing method from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.getDescription() + "Error removing method from plan. " + e.getMessage());
            // return false;
        }
        catch (IOException e) {
            log.error("Error removing method from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.IO_EXCEPTION.getDescription() + "Error removing method from plan. " + e.getMessage());
            // return false;
        }
        catch (URISyntaxException e) {
            log.error("Error removing method from plan. " + e);
            return MasheryApiResponse.MasheryApiResponseBuilder().build(null, false, MasheryClientError.URI_SYNTAX_EXCEPTION.getDescription() + "Error removing method from plan. " + e.getMessage());
            // return false;
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return MasheryApiResponse.MasheryApiResponseBuilder().build("SUCCESS", true, null);
    }

    /**
     * @param resourceName - the name of the resource to lookup
     * @param path         - that api path where to find the resource
     * @return the unique Id of the resource
     */
    private String determineResourceIdFromName(String resourceName, String path) {
        JsonNode responseNode = null;
        try {
            String resource = fetchResource(path, FILTER_QUERY + URLEncoder.encode(resourceName, "UTF-8"));
            if (MasheryUtils.isEmpty(resource))
                return null;

            responseNode = mapper.readTree(resource);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error encoding resource name: " + resourceName);
            return null;
        }
        catch (JsonProcessingException e) {
            log.error("Error encoding resource name: " + resourceName);
            return null;
        }
        catch (IOException e) {
            log.error("Error encoding resource name: " + resourceName);
            return null;
        }

        return MasheryUtils.retrieveIdFromResponse(responseNode, resourceName);
    }

    /**
     * Fetch generic resource via Mashery from the specified path and query
     *
     * @param path  - location of the resource
     * @param query - query param representing extra filter on resource fetch
     * @return response body as http entity
     */
    public String fetchResource(String path, String query) {

        if (MasheryUtils.isEmpty(path)) {
            log.error("Cannot fetch resource, Path is null");
            return null;
        }
        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return null;
        }

        HttpResponse response = null;
        uriBuilder.setPath(path);
        if (!MasheryUtils.isEmpty(query))
            uriBuilder.setQuery(query);
        else
            uriBuilder.removeQuery();

        try {
            HttpGet get = new HttpGet(uriBuilder.build());
            get.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
            response = httpClient.execute(get);
            if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (response != null) {
                    log.error("Response error: " + response.getStatusLine().getStatusCode());
                    String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                    log.error(errorInformationInResponse);
                    String errorJson = MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper);
                    log.error("Error : >> " + errorJson);
                } else {
                    log.error(MasheryClientError.NO_RESPONSE_FROM_API.getDescription());
                }
            } else if (response.getEntity() != null) {
                return IOUtils.toString(response.getEntity().getContent());
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error retrieving resource. " + e);
        }
        catch (URISyntaxException e) {
            log.error("Error retrieving resource. " + e);
        }
        catch (ClientProtocolException e) {
            log.error("Error retrieving resource. " + e);
        }
        catch (IOException e) {
            log.error("Error retrieving resource. " + e);
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return null;
    }

    public MasheryService fetchMasheryService(String serviceId) {
        String serviceResponse = fetchResource(SERVICES_PATH + "/" + serviceId, SERVICE_FIELDS);
        try {
            return mapper.readValue(serviceResponse, MasheryService.class);
        }
        catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public MasheryPackage fetchMasheryPackageByName(String name) {
        MasheryPackage masheryPackage = null;
        try {
            String serviceResponse = fetchResource(PACKAGES_PATH, FILTER_QUERY + URLEncoder.encode(name, "UTF-8") + "&" + PACKAGE_FIELDS);
            MasheryPackage[] packageRes = mapper.readValue(serviceResponse, MasheryPackage[].class);
            if (packageRes.length == 0) {
                masheryPackage = null;
            }
            else if (packageRes.length > 1) {
                for (MasheryPackage aPackage : packageRes) {
                    if (MasheryUtils.isEqual(aPackage.getName(), name)) {
                        masheryPackage = aPackage;
                        break;
                    }
                }
            }
            else{
                masheryPackage = packageRes[0];
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            masheryPackage = null;
        }
        catch (IOException e) {
            log.error(e.getMessage());
            masheryPackage = null;
        }
        return masheryPackage;
    }

    /**
     * Create the Mashery Resource via Mashery V3 API
     *
     * @param resource - the resource to be created
     * @return - Complete response json
     */
    public String createResourceAndReturnCompleteResponse(MasheryResource resource) {

        if (resource == null) {
            log.error("Cannot create resource, resource is null");
            return null;
        }

        if (readOnly) {
            log.error("Attempted Create operation. User has read only permissions");
            return null;
        }

        String[] tokenResponse = retrieveOauthToken();

        if (tokenResponse[0] == null && tokenResponse[1] != null) {
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
            post.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
        }
        catch (URISyntaxException e) {
            log.error("Error creating resource: " + e);
            return null;
        }
        catch (JsonProcessingException e) {
            log.error("Error creating resource: " + e);
            return null;
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error creating resource: " + e);
            return null;
        }

        log.info("Creating new resource " + resource.getResourcePath());

        HttpResponse response = null;
        try {
            response = httpClient.execute(post);

            if (response == null) {
                log.error("Could not create resource. No response from API.");
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                OAuthTokenService.INSTANCE.clearToken();
                return null;
            } else if (statusCode != HttpStatus.SC_OK) {
                log.error("Response error: " + response.getStatusLine().getStatusCode());
                String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                log.error(errorInformationInResponse);
                return null;
            }

            if (response.getEntity() != null) {
                return IOUtils.toString(response.getEntity().getContent());
            }
        }
        catch (ClientProtocolException e) {
            log.error("Error creating resource: " + e);
            return null;
        }
        catch (IOException e) {
            log.error("Error creating resource: " + e);
            return null;
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }

        return null;
    }

    /**
     * Modify the existing Mashery Resource via Mashery V3 API.
     *
     * @param resource - the resource to be modified with the parameters to be changed.
     * @return - Complete response json
     */
    public String modifyResourceAndReturnCompleteResponse(MasheryResource resource) {

        if (resource == null) {
            log.error("Cannot modify resource, resource is null");
            return null;
        }

        if (readOnly) {
            log.error("Attempted Modify operation. User has read only permissions");
            return null;
        }

        String[] tokenResponse = retrieveOauthToken();
        if (tokenResponse[0] == null && tokenResponse[1] != null) {
            log.error("Unable to retrieve OAuth token.");
            return null;
        }
        HttpPut put = null;
        String deployableAsString = null;
        try {
            put = new HttpPut(uriBuilder.setPath(resource.getResourcePath()).build());
            deployableAsString = mapper.writeValueAsString(resource);
            log.info("Sending PUT request to:" + put.getURI().toString() + " Payload: " + deployableAsString);
            put.setEntity(new StringEntity(deployableAsString));
            put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            put.addHeader(MasheryUtils.createAuthHeader(tokenResponse[0]));
        }
        catch (URISyntaxException e) {
            log.error("Error modifying resource. " + e);
            return null;
        }
        catch (JsonProcessingException e) {
            log.error("Error modifying resource. " + e);
            return null;
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error modifying resource. " + e);
            return null;
        }

        HttpResponse response = null;
        int statusCode = 0;
        try {
            response = httpClient.execute(put);
            if (response == null) {
                log.error("Could not modify resource. No response from API.");
                return null;
            }

            statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                log.error("Invalid Token, please retry.");
                OAuthTokenService.INSTANCE.clearToken();
                return null;
            }

            if (statusCode != HttpStatus.SC_OK && response != null) {
                log.error("Response error: " + response.getStatusLine().getStatusCode());
                String errorInformationInResponse = MasheryUtils.retrieveErrorFromResponse(response);
                log.error(errorInformationInResponse);
                return null;
            }

            if (response.getEntity() != null) {
                return IOUtils.toString(response.getEntity().getContent());
            }

        }
        catch (ClientProtocolException e) {
            log.error("Error modifying resource. " + e);
            return null;
        }
        catch (IOException e) {
            log.error("Error modifying resource. " + e);
            return null;
        }
        finally {
            if (response != null)
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    // Ignore
                }
        }
        return null;
    }

    public String retrieveEndpointFromService(String serviceName, String endpointName) {
        String endpointId = null;
        String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
        endpointId = determineResourceIdFromName(endpointName, String.format(ENDPOINTS_PATH, new Object[] {serviceId}));
        return endpointId;
    }

    public List<String> fetchPlanEndpointNames(String serviceName, String packageName, String planName) {
        String serviceId = determineResourceIdFromName(serviceName, SERVICES_PATH);
        String packageId = determineResourceIdFromName(packageName, PACKAGES_PATH);
        String planId = determineResourceIdFromName(planName, String.format(PLANS_PATH, packageId));

        String planServicePath = String.format(PLAN_SERVICES_PATH, packageId, planId);
        String resource = fetchResource(planServicePath + "/" + serviceId + "/endpoints", "");
        JsonNode node = null;
        List<String> endpointNames = new ArrayList<String>();
        try {
            node = mapper.readTree(resource);
            for (JsonNode child : node) {
                endpointNames.add(child.get("name").textValue());
            }
        }
        catch (IOException e) {
            log.error("Error in fetching the plan endpoint names, Unable to parse the response  ", e.getMessage());
            return null;
        }
        return endpointNames;
    }
}
