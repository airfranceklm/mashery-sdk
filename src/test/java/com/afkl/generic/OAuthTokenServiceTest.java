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
		String token = OAuthTokenService.INSTANCE.retrieveToken();
		assertNull(token);
	}

	@Test
	public void testPutAndRetrieve() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 15);
		assertEquals(testToken, OAuthTokenService.INSTANCE.retrieveToken());
	}

	@Test
	public void testPutWithShortTtl() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 9);
		// Need to account for buffer time of 10 seconds
		// Thread.sleep(2000);
		String token = OAuthTokenService.INSTANCE.retrieveToken();
		assertFalse(testToken == token);
	}

	@Test
	public void testClearToken() throws Exception {
		String testToken = UUID.randomUUID().toString();
		OAuthTokenService.INSTANCE.putToken(testToken, 15);
		OAuthTokenService.INSTANCE.clearToken();
		assertFalse(testToken == OAuthTokenService.INSTANCE.retrieveToken());
	}
}
