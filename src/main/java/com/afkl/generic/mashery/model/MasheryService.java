package com.afkl.generic.mashery.model;

import java.util.List;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MasheryService implements MasheryResource {

	@JsonIgnore
	public static final String PATH = "/v3/rest/services";

	@JsonIgnore
	private String serviceId;

	private String name;

	private String created;

	private String updated;

	private List<MasheryEndpoint> endpoints;

	private String editorHandle;

	private Integer revisionNumber;

	private String robotsPolicy;

	private String crossDomainPolicy;

	private String description;

	private List<MasheryErrorSet> errorSets;

	private Integer qpsLimitOverall;

	private boolean rfc3986Encode;

	private MasherySecurityProfile securityProfile;

	private String version;

	private MasheryServiceCache cache;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public List<MasheryEndpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<MasheryEndpoint> endpoints) {
		this.endpoints = endpoints;
	}

	public String getEditorHandle() {
		return editorHandle;
	}

	public void setEditorHandle(String editorHandle) {
		this.editorHandle = editorHandle;
	}

	public Integer getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public String getRobotsPolicy() {
		return robotsPolicy;
	}

	public void setRobotsPolicy(String robotsPolicy) {
		this.robotsPolicy = robotsPolicy;
	}

	public String getCrossDomainPolicy() {
		return crossDomainPolicy;
	}

	public void setCrossDomainPolicy(String crossDomainPolicy) {
		this.crossDomainPolicy = crossDomainPolicy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MasheryErrorSet> getErrorSets() {
		return errorSets;
	}

	public void setErrorSets(List<MasheryErrorSet> errorSets) {
		this.errorSets = errorSets;
	}

	public Integer getQpsLimitOverall() {
		return qpsLimitOverall;
	}

	public void setQpsLimitOverall(int qpsLimitOverall) {
		this.qpsLimitOverall = qpsLimitOverall;
	}

	public boolean isRfc3986Encode() {
		return rfc3986Encode;
	}

	public void setRfc3986Encode(boolean rfc3986Encode) {
		this.rfc3986Encode = rfc3986Encode;
	}

	public MasherySecurityProfile getSecurityProfile() {
		return securityProfile;
	}

	public void setSecurityProfile(MasherySecurityProfile securityProfile) {
		this.securityProfile = securityProfile;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public MasheryServiceCache getCache() {
		return cache;
	}

	public void setCache(MasheryServiceCache cache) {
		this.cache = cache;
	}

	@JsonIgnore
	public String getResourcePath() {
		if (serviceId != null)
			return PATH + "/" + serviceId;

		return PATH;
	}
}
