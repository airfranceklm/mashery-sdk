package com.afkl.generic.mashery.model;

public class MasheryCors {

	private boolean allDomainsEnabled;

	private Integer maxAge;

	public boolean isAllDomainsEnabled() {
		return allDomainsEnabled;
	}

	public void setAllDomainsEnabled(boolean allDomainsEnabled) {
		this.allDomainsEnabled = allDomainsEnabled;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
}
