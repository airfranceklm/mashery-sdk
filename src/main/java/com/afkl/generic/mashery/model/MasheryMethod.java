package com.afkl.generic.mashery.model;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MasheryMethod implements MasheryResource {

	@JsonIgnore
	public static final String PATH = "/v3/rest/services/%s/endpoints/%s/methods";

	private String id;

	private String name;

	private String sampleJsonResponse;

	private String sampleXmlResponse;

	private int rateLimitCeiling;

	private String rateLimitPeriod;

	private int qpsLimitCeiling;

	@JsonIgnore
	private String serviceId;

	@JsonIgnore
	private String endpointId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSampleJsonResponse() {
		return sampleJsonResponse;
	}

	public int getRateLimitCeiling() {
		return rateLimitCeiling;
	}

	public void setRateLimitCeiling(int rateLimitCeiling) {
		this.rateLimitCeiling = rateLimitCeiling;
	}

	public String getRateLimitPeriod() {
		return rateLimitPeriod;
	}

	public void setRateLimitPeriod(String rateLimitPeriod) {
		this.rateLimitPeriod = rateLimitPeriod;
	}

	public int getQpsLimitCeiling() {
		return qpsLimitCeiling;
	}

	public void setQpsLimitCeiling(int qpsLimitCeiling) {
		this.qpsLimitCeiling = qpsLimitCeiling;
	}

	public void setSampleJsonResponse(String sampleJsonResponse) {
		this.sampleJsonResponse = sampleJsonResponse;
	}

	public String getSampleXmlResponse() {
		return sampleXmlResponse;
	}

	public void setSampleXmlResponse(String sampleXmlResponse) {
		this.sampleXmlResponse = sampleXmlResponse;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setEndpointId(String endpointId) {
		this.endpointId = endpointId;
	}

	@JsonIgnore
	public String getResourcePath() {
		return String.format(PATH, serviceId, endpointId);
	}
}
