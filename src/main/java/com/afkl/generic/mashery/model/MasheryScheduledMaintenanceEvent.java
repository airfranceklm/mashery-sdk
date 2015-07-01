package com.afkl.generic.mashery.model;

import java.util.List;

import com.afkl.generic.mashery.MasheryResource;


public class MasheryScheduledMaintenanceEvent implements MasheryResource {

	private static final String PATH = "/v3/rest/scheduledMaintenanceEvents";

	private String name;

	private String startDateTime;

	private String endDateTime;

	// TODO listed as 'ScheduledMaintenanceEventEndpoint' but no docs
	private List<String> endpoints;

	public String getName() {
		return name;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public List<String> getEndpoints() {
		return endpoints;
	}

	public String getResourcePath() {
		return PATH;
	}
}
