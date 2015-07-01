package com.afkl.generic.mashery.model;

import java.util.List;

public class MasheryErrorSet {

	private String name;

	private String type;

	private boolean jsonp;

	private String jsonpType;

	private List<String> errorMessages;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isJsonp() {
		return jsonp;
	}

	public String getJsonpType() {
		return jsonpType;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}
}
