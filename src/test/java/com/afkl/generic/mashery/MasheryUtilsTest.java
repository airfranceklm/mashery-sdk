/*
 * Copyright (c) KLM Royal Dutch Airlines. All Rights Reserved.
 * ============================================================
 */
package com.afkl.generic.mashery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO Describe purpose of this class
 */
public class MasheryUtilsTest {
    private String apiScope;
    private String errorInformationInResponse;
    private ObjectMapper mapper;
    private String errorString;
    private String errorResponse;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        this.apiScope = "scope";
        this.errorInformationInResponse = "{" + "\"errorCode\" : \"400\"," + "\"errors\" : [" + "{" + "    \"property\" : \"systemDomains[0]\","
                        + "    \"message\" : \"Property 'whz.api.{{subEnv.name.lc}}.klm.com' is not a valid domain.\"" + "    }," + "    {" + "    \"property\" : \"trafficManagerDomain\","
                        + "    \"message\" : \"Found an endpoint with method 'GET', domain 'api.ite1.klm.com' and path '/travel/complaints' that belongs to someone else\"" + "    }" + "],"
                        + "\"errorMessage\" :  \"Unable to create service definition endpoint.\"" + " }";
        this.errorString = "<h1>Developer Over Qps</h1>";
        this.errorResponse = "Response Error Occured";

    }

    /**
     * Test method for {@link com.afkl.generic.mashery.MasheryUtils#isEqual(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testIsEqual() {
        assertTrue(MasheryUtils.isEqual("scope", apiScope));
        assertFalse(MasheryUtils.isEqual("Scope", apiScope));
        assertFalse(MasheryUtils.isEqual("scope", null));
        assertFalse(MasheryUtils.isEqual("", null));
        assertFalse(MasheryUtils.isEqual(null, ""));
        assertTrue(MasheryUtils.isEqual(null, null));
        assertFalse(MasheryUtils.isEqual(null, apiScope));
    }

    /**
     * Test method for {@link com.afkl.generic.mashery.MasheryUtils#retrieveCompleteErrorResponseAsJson(java.lang.String, com.fasterxml.jackson.databind.ObjectMapper)}.
     */
    @Test
    public void testRetrieveCompleteErrorResponseAsJson() {
        assertNotNull(MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper));
        assertEquals(MasheryUtils.retrieveCompleteErrorResponseAsJson(errorString, mapper), errorString);
        assertNull(MasheryUtils.retrieveCompleteErrorResponseAsJson(null, mapper));
        assertEquals(MasheryUtils.retrieveCompleteErrorResponseAsJson(errorResponse, mapper), errorResponse);
        System.out.println(MasheryUtils.retrieveCompleteErrorResponseAsJson(errorInformationInResponse, mapper));
    }

}
