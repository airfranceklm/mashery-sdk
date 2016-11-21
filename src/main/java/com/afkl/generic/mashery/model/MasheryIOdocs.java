/**
 * 
 */
package com.afkl.generic.mashery.model;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * @author x085982
 *
 */
public class MasheryIOdocs implements MasheryResource {

    @JsonIgnore
    public static final String PATH = "/v3/rest/iodocs/services";
    private String serviceId;
    @JsonIgnore
    private Boolean updateIodocsFlag;
    @JsonRawValue
    private String definition;
    private Boolean defaultApi;

    /**
     * @return the serviceId
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * @return the defaultApi
     */
    public Boolean getDefaultApi() {
        return defaultApi;
    }

    /**
     * @param defaultApi the defaultApi to set
     */
    public void setDefaultApi(Boolean defaultApi) {
        this.defaultApi = defaultApi;
    }

    /**
     * @return the updateIodocsFlag
     */
    public Boolean getUpdateIodocsFlag() {
        return updateIodocsFlag;
    }

    /**
     * @param updateIodocsFlag the updateIodocsFlag to set
     */
    public void setUpdateIodocsFlag(Boolean updateIodocsFlag) {
        this.updateIodocsFlag = updateIodocsFlag;
    }

    @JsonIgnore
    public String getResourcePath() {
        if (updateIodocsFlag)
            return PATH + "/" + serviceId;
        return PATH;
    }
}
