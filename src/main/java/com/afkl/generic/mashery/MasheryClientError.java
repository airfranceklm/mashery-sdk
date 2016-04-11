package com.afkl.generic.mashery;

/**
 * Created by x085982 on 3/7/2016.
 * Error description class for the Mashery Client.
 * This allows for wrapping the error details to the response
 */
public enum MasheryClientError {

    NO_OAUTH_RETRIEVED(900, "Unable to retrieve OAuth token."),
    TOKEN_UNAUTHORIZED(901, "Invalid Token or Token UnAuthorized , please retry."),
    RESPONSE_ERROR_FROM_API(902, "Response error occurred from Mashery API."),

    NO_RESPONSE_FROM_API(801, "No response from API, Could not perform operation : "),
    RESOURCE_PATH_EMPTY(802, "Path is empty, Could not perform operation : "),
    READ_ONLY_USER_CANNOT_PERFORM(803, "User has read only permissions, Could not perform operation : "),

    URI_SYNTAX_EXCEPTION(804, "URISyntaxException occurred. "),
    JSON_PROCESSING_EXCEPTION(805, "JsonProcessingException. "),
    UNSUPPORTED_ENCODING_EXCEPTION(806, "UnsupportedEncodingException occurred. "),
    CLIENT_PROTOCOL_EXCEPTION(807, "ClientProtocolException occurred. "),
    IO_EXCEPTION(808, "IOException occurred while creating resource. "),
    ILLEGAL_STATE_EXCEPTION(809, "IllegalStateException occurred. ");


    private final int code;
    private final java.lang.String description;

    private MasheryClientError(int code, java.lang.String description) {
        this.code = code;
        this.description = description;
    }

    public java.lang.String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + " : " + description;
    }
}
