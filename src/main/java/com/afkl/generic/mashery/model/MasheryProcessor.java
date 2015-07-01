package com.afkl.generic.mashery.model;

import java.util.Map;


public class MasheryProcessor {

	private String adapter;

	private Map<String, String> preInputs;

	private Map<String, String> postInputs;

	private boolean preProcessEnabled;

	private boolean postProcessEnabled;

	public String getAdapter() {
		return adapter;
	}

	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}

	public void setPreInputs(Map<String, String> preInputs) {
		this.preInputs = preInputs;
	}

	public void setPostInputs(Map<String, String> postInputs) {
		this.postInputs = postInputs;
	}

	public void setPreProcessEnabled(boolean preProcessEnabled) {
		this.preProcessEnabled = preProcessEnabled;
	}

	public void setPostProcessEnabled(boolean postProcessEnabled) {
		this.postProcessEnabled = postProcessEnabled;
	}

	public Map<String, String> getPreInputs() {
		return preInputs;
	}

	public Map<String, String> getPostInputs() {
		return postInputs;
	}

	public boolean isPreProcessEnabled() {
		return preProcessEnabled;
	}

	public boolean isPostProcessEnabled() {
		return postProcessEnabled;
	}
}
