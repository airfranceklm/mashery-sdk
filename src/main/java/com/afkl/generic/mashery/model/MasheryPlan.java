package com.afkl.generic.mashery.model;

import java.util.List;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MasheryPlan implements MasheryResource {

	@JsonIgnore
	public static final String PATH = "/v3/rest/packages/%s/plans";

	@JsonIgnore
	private String planId;

	private String name;

	private String description;

	private Object eav;

	private boolean selfServiceKeyProvisioningEnabled;

	private boolean adminKeyProvisioningEnabled;

	private String notes;

	private Integer maxNumKeysAllowed;

	private Integer numKeysBeforeReview;

	private Integer qpsLimitCeiling;

	private boolean qpsLimitExempt;

	private boolean qpsLimitKeyOverrideAllowed;

	private Integer rateLimitCeiling;

	private boolean rateLimitExempt;

	private boolean rateLimitKeyOverrideAllowed;

	private MasheryPackage.Period rateLimitPeriod;

	private boolean responseFilterOverrideAllowed;

	private String emailTemplateSetId;

	// TODO find out what this should be
	private List<Object> services;

	private String packageId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getEav() {
		return eav;
	}

	public void setEav(Object eav) {
		this.eav = eav;
	}

	public boolean isSelfServiceKeyProvisioningEnabled() {
		return selfServiceKeyProvisioningEnabled;
	}

	public void setSelfServiceKeyProvisioningEnabled(
			boolean selfServiceKeyProvisioningEnabled) {
		this.selfServiceKeyProvisioningEnabled = selfServiceKeyProvisioningEnabled;
	}

	public boolean isAdminKeyProvisioningEnabled() {
		return adminKeyProvisioningEnabled;
	}

	public void setAdminKeyProvisioningEnabled(boolean adminKeyProvisioningEnabled) {
		this.adminKeyProvisioningEnabled = adminKeyProvisioningEnabled;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getMaxNumKeysAllowed() {
		return maxNumKeysAllowed;
	}

	public void setMaxNumKeysAllowed(int maxNumKeysAllowed) {
		this.maxNumKeysAllowed = maxNumKeysAllowed;
	}

	public Integer getNumKeysBeforeReview() {
		return numKeysBeforeReview;
	}

	public void setNumKeysBeforeReview(int numKeysBeforeReview) {
		this.numKeysBeforeReview = numKeysBeforeReview;
	}

	public Integer getQpsLimitCeiling() {
		return qpsLimitCeiling;
	}

	public void setQpsLimitCeiling(int qpsLimitCeiling) {
		this.qpsLimitCeiling = qpsLimitCeiling;
	}

	public boolean isQpsLimitExempt() {
		return qpsLimitExempt;
	}

	public void setQpsLimitExempt(boolean qpsLimitExempt) {
		this.qpsLimitExempt = qpsLimitExempt;
	}

	public boolean isQpsLimitKeyOverrideAllowed() {
		return qpsLimitKeyOverrideAllowed;
	}

	public void setQpsLimitKeyOverrideAllowed(boolean qpsLimitKeyOverrideAllowed) {
		this.qpsLimitKeyOverrideAllowed = qpsLimitKeyOverrideAllowed;
	}

	public Integer getRateLimitCeiling() {
		return rateLimitCeiling;
	}

	public void setRateLimitCeiling(int rateLimitCeiling) {
		this.rateLimitCeiling = rateLimitCeiling;
	}

	public boolean isRateLimitExempt() {
		return rateLimitExempt;
	}

	public void setRateLimitExempt(boolean rateLimitExempt) {
		this.rateLimitExempt = rateLimitExempt;
	}

	public boolean isRateLimitKeyOverrideAllowed() {
		return rateLimitKeyOverrideAllowed;
	}

	public void setRateLimitKeyOverrideAllowed(boolean rateLimitKeyOverrideAllowed) {
		this.rateLimitKeyOverrideAllowed = rateLimitKeyOverrideAllowed;
	}

	public MasheryPackage.Period getRateLimitPeriod() {
		return rateLimitPeriod;
	}

	public void setRateLimitPeriod(MasheryPackage.Period rateLimitPeriod) {
		this.rateLimitPeriod = rateLimitPeriod;
	}

	public boolean isResponseFilterOverrideAllowed() {
		return responseFilterOverrideAllowed;
	}

	public void setResponseFilterOverrideAllowed(
			boolean responseFilterOverrideAllowed) {
		this.responseFilterOverrideAllowed = responseFilterOverrideAllowed;
	}

	public String getEmailTemplateSetId() {
		return emailTemplateSetId;
	}

	public void setEmailTemplateSetId(String emailTemplateSetId) {
		this.emailTemplateSetId = emailTemplateSetId;
	}

	public List<Object> getServices() {
		return services;
	}

	public void setServices(List<Object> services) {
		this.services = services;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	@JsonIgnore
	public String getResourcePath() {
		String path = String.format(PATH, packageId);
		if (planId != null)
			path = path + "/" + planId;

		return path;
	}
}
