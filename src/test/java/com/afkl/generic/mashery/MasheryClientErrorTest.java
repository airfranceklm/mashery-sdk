package com.afkl.generic.mashery;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by x085982 on 3/9/2016.
 */
public class MasheryClientErrorTest {
    private MasheryClientError masheryClientError;


    @Test
    public void testGetDescription() throws Exception {
        assertEquals("Unable to retrieve OAuth token.", MasheryClientError.NO_OAUTH_RETRIEVED.getDescription());
        assertEquals("User has read only permissions, Could not perform operation : ", MasheryClientError.READ_ONLY_USER_CANNOT_PERFORM.getDescription());
        assertNotEquals("unknown exception",MasheryClientError.NO_RESPONSE_FROM_API.getDescription());
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals(901, MasheryClientError.TOKEN_UNAUTHORIZED.getCode());
        assertEquals(902, MasheryClientError.RESPONSE_ERROR_FROM_API.getCode());
        assertNotEquals(803,MasheryClientError.URI_SYNTAX_EXCEPTION.getCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("801 : No response from API, Could not perform operation : ", MasheryClientError.NO_RESPONSE_FROM_API.toString());
        assertEquals("807 : ClientProtocolException occurred. ", MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.toString());
        assertNotEquals("807 : ClientProtocolException occurred : ", MasheryClientError.CLIENT_PROTOCOL_EXCEPTION.toString());
    }
}