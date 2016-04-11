package com.afkl.generic;

import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.afkl.generic.mashery.OAuthTokenService;

public class OAuthTokenServiceTest extends TestCase {

	@Before
	@Override
	protected void setUp() throws Exception {
		// Hack
		OAuthTokenService.INSTANCE.clearToken();
	}

	@After
	@Override
	protected void tearDown() throws Exception {
		//
	}

	@Test
	public void testRetrieveToken() throws Exception {
		String token = OAuthTokenService.INSTANCE.retrieveToken("1111111-2222-333333-4444");
		assertNull(token);
	}

	@Test
	public void testPutAndRetrieve() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 15,"1111111-2222-333333-4444");
		assertEquals(testToken, OAuthTokenService.INSTANCE.retrieveToken("1111111-2222-333333-4444"));
	}

	@Test
	public void testPutWithShortTtl() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 9,"1111111-2222-333333-4444");
		// Need to account for buffer time of 10 seconds
		// Thread.sleep(2000);
		String token = OAuthTokenService.INSTANCE.retrieveToken("1111111-2222-333333-4444");
		assertFalse(testToken == token);
	}

	@Test
	public void testClearToken() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 15,"1111111-2222-333333-4444");
		OAuthTokenService.INSTANCE.clearToken();
		assertFalse(testToken == OAuthTokenService.INSTANCE.retrieveToken("1111111-2222-333333-4444"));
	}


	@Test
	public void testTokenwithSameAreaID() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 100,"1111111-2222-333333-4444");
		assertEquals(null, OAuthTokenService.INSTANCE.retrieveToken(null));
	}

	@Test
	public void testTokenwithDifferentAreaID() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 15,"1111111-2222-333333-4444");
		assertFalse(testToken == OAuthTokenService.INSTANCE.retrieveToken("88888-2222-333333-4444"));
	}
}
