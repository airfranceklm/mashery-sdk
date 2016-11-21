package com.afkl.generic.mashery;

/**
 * Created by x085982 on 3/7/2016. Domain class for API Response from Mashery Client Useful to pass the information to the consumers with some basic error description
 */
public class MasheryApiResponse {

    private String response;
    private boolean status;
    private String error;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static MasheryApiResponse MasheryApiResponseBuilder() {
        return new MasheryApiResponse();
    }

    public MasheryApiResponse build(String response, boolean status, String error) {
        this.response = response;
        this.status = status;
        this.error = error;
        return this;
    }
}
