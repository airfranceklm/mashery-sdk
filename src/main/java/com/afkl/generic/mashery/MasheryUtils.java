package com.afkl.generic.mashery;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Utils class
 */
public class MasheryUtils {

    private static Logger log = LoggerFactory.getLogger(MasheryClient.class);

    /**
     * <p>
     * Compares two Strings, returning true if they represent equal strings of characters.
     * </p>
     *
     * @param o1 the first String, may be null
     * @param o2 the second String, may be null
     * @return true, if the Strings are equal
     */
    public static boolean isEqual(String o1, String o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    /**
     * Convert the JSON error response to a string
     * 
     * @param response containing json error
     * @return string representation of the error.
     */
    public static String retrieveErrorFromResponse(HttpResponse response) {
        Scanner scan = null;
        try {
            scan = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
            return scan.hasNext() ? scan.next() : "";
        }
        catch (IllegalStateException e) {
            log.error("Error retrieving error at method:  retrieveErrorFromResponse" + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error retrieving error at method:  retrieveErrorFromResponse" + e.getMessage());
        }
        finally {
            if (scan != null)
                scan.close();
        }

        return null;
    }

    public static String retrieveCompleteErrorResponseAsJson(String errorInformationInResponse, ObjectMapper mapper) {

        try {
            if (!MasheryUtils.isEmpty(errorInformationInResponse) && errorInformationInResponse.charAt(0) != '<') {
                Object errorJson = mapper.readValue(errorInformationInResponse, Object.class);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJson);
            }
        }

        catch (JsonProcessingException e) {
            log.error("Error retrieving error at method:  retrieveCompleteErrorResponseAsJson :" + e.getMessage());
        }
        catch (IllegalStateException e) {
            log.error("Error retrieving error at method retrieveCompleteErrorResponseAsJson :" + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error retrieving error at method retrieveCompleteErrorResponseAsJson :" + e.getMessage());
        }
        return errorInformationInResponse;
    }

    /**
     * Retrieve the Unique Identifier from response body.
     *
     * @param responseNode - response body which contains unique id
     * @return - Unique ID
     */
    public static String retrieveIdFromResponse(JsonNode responseNode, String name) {

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
        }
        else
            node = responseNode;

        if (node != null && !node.get("id").isNull())
            id = node.get("id").textValue();

        return id;
    }

    /**
     * Util method to create a Auth header with Bearer token
     * 
     * @param token - token to be included
     * @return Header object for Bearer Auth
     */
    public static Header createAuthHeader(String token) {
        return new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    /**
     * Utility method to check for empty string
     * 
     * @param toCheck string to check
     * @return true if string is null, has length 0 or contains only whitespace
     */
    public static boolean isEmpty(String toCheck) {
        return toCheck == null || toCheck.trim().length() == 0;
    }
}
