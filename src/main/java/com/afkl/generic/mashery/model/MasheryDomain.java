package com.afkl.generic.mashery.model;

import com.afkl.generic.mashery.MasheryResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MasheryDomain implements MasheryResource {

	private static final String PATH = "/v3/rest/domains";

	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@JsonIgnore
	public String getResourcePath() {
		return PATH;
	}
}
