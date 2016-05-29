package com.afkl.generic.mashery;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        StringBuilder buf = new StringBuilder();
        try {
            Object errorJson = mapper.readValue(errorInformationInResponse, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJson);
        }
        catch (JsonProcessingException e) {
            log.error("Error retrieving error at method:  retrieveCompleteErrorResponseAsJson" + e.getMessage());
        }
        catch (IllegalStateException e) {
            log.error("Error retrieving error at method retrieveCompleteErrorResponseAsJson" + e.getMessage());
        }
        catch (IOException e) {
            log.error("Error retrieving error at method retrieveCompleteErrorResponseAsJson" + e.getMessage());
        }
        return buf.toString();
    }
}
