package com.afkl.generic.mashery.model;

import java.util.List;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class MasheryPackage implements MasheryResource {

	@JsonIgnore
	public static final String PATH = "/v3/rest/packages";

	@JsonIgnore
	private String packageId;

	private String name;

	private String description;

	private Period notifyDeveloperPeriod;

	private boolean notifyDeveloperNearQuota;

	private boolean notifyDeveloperOverQuota;

	private boolean notifyDeveloperOverThrottle;

	private Period notifyAdminPeriod;

	private boolean notifyAdminNearQuota;

	private boolean notifyAdminOverQuota;

	private boolean notifyAdminOverThrottle;

	private String notifyAdminEmails;

	private Integer nearQuotaThreshold;

	//TODO create Eav object
	private Object eav;

	private String keyAdapter;

	private Integer keyLength;

	private Integer sharedSecretLength;

	private List<MasheryPlan> plans;

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

	public Period getNotifyDeveloperPeriod() {
		return notifyDeveloperPeriod;
	}

	public void setNotifyDeveloperPeriod(Period notifyDeveloperPeriod) {
		this.notifyDeveloperPeriod = notifyDeveloperPeriod;
	}

	public boolean isNotifyDeveloperNearQuota() {
		return notifyDeveloperNearQuota;
	}

	public void setNotifyDeveloperNearQuota(boolean notifyDeveloperNearQuota) {
		this.notifyDeveloperNearQuota = notifyDeveloperNearQuota;
	}

	public boolean isNotifyDeveloperOverQuota() {
		return notifyDeveloperOverQuota;
	}

	public void setNotifyDeveloperOverQuota(boolean notifyDeveloperOverQuota) {
		this.notifyDeveloperOverQuota = notifyDeveloperOverQuota;
	}

	public boolean isNotifyDeveloperOverThrottle() {
		return notifyDeveloperOverThrottle;
	}

	public void setNotifyDeveloperOverThrottle(boolean notifyDeveloperOverThrottle) {
		this.notifyDeveloperOverThrottle = notifyDeveloperOverThrottle;
	}

	public Period getNotifyAdminPeriod() {
		return notifyAdminPeriod;
	}

	public void setNotifyAdminPeriod(Period notifyAdminPeriod) {
		this.notifyAdminPeriod = notifyAdminPeriod;
	}

	public boolean isNotifyAdminNearQuota() {
		return notifyAdminNearQuota;
	}

	public void setNotifyAdminNearQuota(boolean notifyAdminNearQuota) {
		this.notifyAdminNearQuota = notifyAdminNearQuota;
	}

	public boolean isNotifyAdminOverQuota() {
		return notifyAdminOverQuota;
	}

	public void setNotifyAdminOverQuota(boolean notifyAdminOverQuota) {
		this.notifyAdminOverQuota = notifyAdminOverQuota;
	}

	public boolean isNotifyAdminOverThrottle() {
		return notifyAdminOverThrottle;
	}

	public void setNotifyAdminOverThrottle(boolean notifyAdminOverThrottle) {
		this.notifyAdminOverThrottle = notifyAdminOverThrottle;
	}

	public String getNotifyAdminEmails() {
		return notifyAdminEmails;
	}

	public void setNotifyAdminEmails(String notifyAdminEmails) {
		this.notifyAdminEmails = notifyAdminEmails;
	}

	public Integer getNearQuotaThreshold() {
		return nearQuotaThreshold;
	}

	public void setNearQuotaThreshold(int nearQuotaThreshold) {
		this.nearQuotaThreshold = nearQuotaThreshold;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public Object getEav() {
		return eav;
	}

	public void setEav(Object eav) {
		this.eav = eav;
	}

	public String getKeyAdapter() {
		return keyAdapter;
	}

	public void setKeyAdapter(String keyAdapter) {
		this.keyAdapter = keyAdapter;
	}

	public Integer getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public Integer getSharedSecretLength() {
		return sharedSecretLength;
	}

	public void setSharedSecretLength(int sharedSecretLength) {
		this.sharedSecretLength = sharedSecretLength;
	}

	public List<MasheryPlan> getPlans() {
		return plans;
	}

	public void setPlans(List<MasheryPlan> plans) {
		this.plans = plans;
	}

	@JsonIgnore
	public String getResourcePath() {
		if (packageId != null)
			return PATH + "/" + packageId;

		return PATH;
	}

	public enum Period {
		MINUTE("minute"),
		HOUR("hour"),
		DAY("day"),
		MONTH("month");

		private final String string;

		private Period(String oauthHeader) {
			this.string = oauthHeader;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static Period fromString(String oauthHeader) {
			for (Period val : Period.values()) {
				if (val.string.equalsIgnoreCase(oauthHeader))
					return val;
			}
			return null;
		}
	}
}
