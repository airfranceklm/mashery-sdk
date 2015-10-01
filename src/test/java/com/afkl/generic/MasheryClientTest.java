package com.afkl.generic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.afkl.generic.mashery.MasheryClient;
import com.afkl.generic.mashery.MasheryClientBuilder;
import com.afkl.generic.mashery.MasheryResource;
import com.afkl.generic.mashery.OAuthTokenService;
import com.afkl.generic.mashery.model.MasheryEndpoint;
import com.afkl.generic.mashery.model.MasheryMethod;
import com.afkl.generic.mashery.model.MasheryPackage;
import com.afkl.generic.mashery.model.MasheryPlan;
import com.afkl.generic.mashery.model.MasheryService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MasheryClientTest extends TestCase {


	public static final String OAUTH_TOKEN = "fakeOAuthToken";

	private static final String RESOUCE_PATH = "/v3/rest/resources/1";

	private long oauthTokenExpireTime;

	private MasheryClient client;

	private LocalTestServer testServer;

	private HttpRequestHandler resourceHandler;

	private HttpRequestHandler tokenHandler;

	private MasheryResource resource;
	private int port;
	private String host;
	Map<String, String> params;

	String apiKey = "testApiKey";
	String apiSecret = "testApiSecret";
	String userName = "testUsername";
	String password = "testPassword";
	String scope = "testScope";
	String grantType = "password";
	String resourceId = "testResourceId";

	// Defaults
	String packageName = "testPackage";
	String endpointName = "testEndpoint";
	String methodName = "testMethod";
	String planName = "testPlan";
	String serviceName = "fakeService";

	@Override
	@Before
	public void setUp() throws Exception {
		OAuthTokenService.INSTANCE.clearToken();

		tokenHandler = new TokenHandler();
		resourceHandler = new ResourceHandler();

		testServer = new LocalTestServer(null, null);
		testServer.register("/v3/rest/*", resourceHandler);
		testServer.register("/v3/token", tokenHandler);
		testServer.start();

		port = testServer.getServicePort();
		host = testServer.getServiceHostName();

//		System.out.println("LocalTestServer available via http://" + host + ":" + port);

		params = new HashMap<String, String>();
		params.put("username", userName);
		params.put("password", password);
		params.put("scope", scope);
		params.put("grant_type", grantType);

		client = MasheryClientBuilder.masheryClient()
				.withHost(host)
				.withProtocol("http")
				.withPort(port)
				.withApiKey(apiKey)
				.withApiSecret(apiSecret)
				.withTokenRequestParams(params)
				.build();
	}

	@Override
	@After
	public void tearDown() {
		client = null;
	}


	/**
	 * Happy Path Tests
	 */

	@Test
	public void testDeleteResource() {
		assertTrue(client.deleteResource(RESOUCE_PATH));
	}

	@Test
	public void testCreateResource() {
		resource = new MasheryService();
		assertEquals(resourceId, client.createResource(resource));
	}

	@Test
	public void testModifyResource() {
		resource =  new MasheryPackage();
		assertTrue(client.modifyResource(resource));
	}

	@Test
	public void testAddEndpointToPlan() {
		assertTrue(client.addEndpointToPlan(endpointName, planName, serviceName , packageName));
	}

	@Test
	public void testRemoveEndpointFromPlan() {
		assertTrue(client.removeEndpointFromPlan(endpointName, planName, serviceName, packageName));
	}


	/**
	 * Error cases
	 */
	@Test
	public void testCreateResourceMissingParams() {

		client = MasheryClientBuilder.masheryClient()
				.withHost(host)
				.withProtocol("http")
				.withPort(port)
				.withApiKey("wrongKey")
				.withApiSecret(apiSecret)
				.withTokenRequestParams(params)
				.build();

		resource = new MasheryPackage();
		assertNull(client.createResource(resource));
	}

	@Test
	public void testErrorResponseFromMashery() {
		testServer.register("/v3/rest/services", new ErrorRequestHandler());
		resource = new MasheryService();
		assertNull(client.createResource(resource));
	}

	@Test
	public void testErrorOnTokenRequest() throws Exception {

		// To ensure token is expired... find better solution
		Thread.sleep(3000);

		testServer.register("/v3/token", new ErrorTokenRequestHandler());
		resource = new MasheryEndpoint();
		assertFalse(client.modifyResource(resource));
	}

	@Test
	public void testFetchResourceError() {
		assertNull(client.fetchResource("##535^ kk", "query"));
	}

	@Test
	public void testNullResources() {
		assertNull(client.createResource(null));
		assertFalse(client.modifyResource(null));
		assertFalse(client.deleteResource("    "));
		assertNull(client.fetchResource("  ", null));
	}

	@Test
	public void testAddEndpointToPlanFail() {
		testServer.register("/v3/rest/packages/*", new ErrorRequestHandler());
		assertFalse(client.addEndpointToPlan(endpointName, planName, serviceName , packageName));
	}

	@Test
	public void testAddMethodToPlan() {
		assertTrue(client.addMethodToPlan(methodName, endpointName, planName, serviceName, packageName));
	}

	@Test
	public void testAddMethodObjectToPlan() {
		MasheryMethod method = new MasheryMethod();
		method.setEndpointId("testEndpointID");
		method.setName("testMethodName");
		assertTrue(client.addMethodToPlan(method, planName, serviceName, packageName, endpointName));
	}

	@Test
	public void testAddMethodToPlanFail() {
		testServer.register("/v3/rest/packages/*", new ErrorRequestHandler());
		assertFalse(client.addMethodToPlan(methodName, endpointName, planName, serviceName, packageName));
	}

	@Test
	public void testRemoveMethodToPlan() {
		assertTrue(client.removeMethodFromPlan(methodName, endpointName, planName, serviceName, packageName));
	}


	@Test
	public void testModifyResourceFail() {
		testServer.register("/v3/rest/services/*", new ErrorRequestHandler());
		resource =  new MasheryEndpoint();
		assertFalse(client.modifyResource(resource));
	}

	@Test
	public void testReadOnly() {
		client.setReadOnly(true);
		assertNull(client.createResource(new MasheryPlan()));
		assertFalse(client.modifyResource((new MasheryPlan())));
		assertFalse(client.deleteResource("/v3/rest/services/1234"));
		assertFalse(client.removeEndpointFromPlan(endpointName, planName, serviceName, packageName));
		assertFalse(client.addEndpointToPlan(endpointName, planName, serviceName, packageName));
		assertNotNull(client.fetchResource("/v3/rest/services", null));
	}

	@Test
	public void testDeleteResourceErrors() {
		// No Path
		assertFalse(client.deleteResource(null));
//		 Expired token
//		client.deleteResource(RESOUCE_PATH);
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// ignore
//		}
//		assertFalse(client.deleteResource(RESOUCE_PATH));


	}

	@Test
	public void testTokenReuse() {
		client.fetchResource("/", "");
		client.fetchResource("/", "");
	}

	@Test
	public void testBadToken() {
		testServer.register("/v3/token", new BadTokenRequestHandler());
		assertFalse(client.deleteResource(RESOUCE_PATH));
	}

	@Test
	public void testAddEndpointToPlanError() {
		testServer.register("/v3/rest/packages/testResourceId/plans/testResourceId/services/testResourceId/endpoints", new ErrorRequestHandler());
		assertFalse(client.addEndpointToPlan(endpointName, planName, serviceName, packageName));
	}

	/**
	 * Util Classes
	 *
	 */

	class TokenHandler implements HttpRequestHandler {

		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {
			// Verify Credentials
			Header authHeader = request.getFirstHeader(HttpHeaders.AUTHORIZATION);
			if (authHeader == null) {
//				Assert.fail("No Auth Header included in Token Request");
				return;
			}

			String[] pieces = authHeader.getValue().split(" ");
			if (pieces.length != 2 || !"Basic".equals(pieces[0])) {
//				Assert.fail("Auth header incorrectly formatter");
				return;
			}

			String auth = new String(Base64.decodeBase64(pieces[1]), "UTF-8");
			String[] authPieces = auth.split(":");

			if (authPieces.length != 2 || !apiKey.equals(authPieces[0]) || !apiSecret.equals(authPieces[1])) {
//				Assert.fail("Authentication credentials incorrect");
				return;
			}

			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			String requestPayload = EntityUtils.toString(entity);

			for (String key : params.keySet()) {
				if (!requestPayload.contains(key + "=" + params.get(key))) {
//					Assert.fail("Parameter " + key + " missing from Request Payload");
					return;
				}
			}

			// return fake token
			ObjectMapper mapper = new ObjectMapper();
			OAuthTokenResponse oauthToken = new OAuthTokenResponse();
			oauthTokenExpireTime = System.currentTimeMillis() + (oauthToken.expiresIn * 1000);
			String tokenResponse = mapper.writeValueAsString(oauthToken);
			response.setEntity(new StringEntity(tokenResponse));
		}

	}

	class ResourceHandler implements HttpRequestHandler {

		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {

			// Verify Credentials
			Header authHeader = request.getFirstHeader(HttpHeaders.AUTHORIZATION);
			if (authHeader == null) {
				Assert.fail("No Auth Header included in Resource Request");
				return;
			}

			String[] pieces = authHeader.getValue().split(" ");
			if (pieces.length != 2 || !"Bearer".equals(pieces[0]) || !OAUTH_TOKEN.equals(pieces[1])) {
				Assert.fail("Auth header incorrectly formatter");
				return;
			}

			if (System.currentTimeMillis() > oauthTokenExpireTime) {
				response.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_UNAUTHORIZED, "Not Authorized"));
				return;
			}

			response.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));

			if (request.getRequestLine().getUri().contains("fields=methods")) {
				response.setEntity(new StringEntity("{\"methods\":[]}"));
			} else {
				response.setEntity(new StringEntity("{\"id\":\"" + resourceId  + "\"}"));
			}
		}
	}

	class ErrorRequestHandler implements HttpRequestHandler {

		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {
			response.setStatusCode(500);
			response.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 500, "Internal Server Error"));
		}

	}

	class ErrorTokenRequestHandler implements HttpRequestHandler {

		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {

			// return fake token
			ObjectMapper mapper = new ObjectMapper();
			String tokenResponse = mapper.writeValueAsString(new OAuthErrorResponse());
			response.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 400, "Bad Request"));
			response.setEntity(new StringEntity(tokenResponse));
		}
	}

	class BadTokenRequestHandler implements HttpRequestHandler {

		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {

			// return bad token
			response.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 400, "Bad Request"));
			response.setEntity(new StringEntity("{notReal:JSON}"));
		}
	}

	class OAuthTokenResponse {

		@JsonProperty("token_type")
		String tokenType = "bearer";

		@JsonProperty("mapi")
		String mapi = "";

		@JsonProperty("access_token")
		String accessToken = OAUTH_TOKEN;

		// For Testing
		@JsonProperty("expires_in")
		int expiresIn = 20;

		@JsonProperty("refresh_token")
		String refreshToken = "fakeRefreshToken";

		@JsonProperty("scope")
		String scope = "test";
	}

	class OAuthErrorResponse {

		@JsonProperty("error")
		String error = "Bad Request";

		@JsonProperty("error_description")
		String errorDescription = "Invalid Client Credentials";

	}
}
