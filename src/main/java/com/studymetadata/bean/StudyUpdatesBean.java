package com.studymetadata.bean;

public class StudyUpdatesBean {
	private Boolean consent = false;
	private Boolean activities = false;
	private Boolean resources = false;
	private Boolean info = false;
	
	public Boolean getConsent() {
		return consent;
	}
	public void setConsent(Boolean consent) {
		this.consent = consent;
	}
	public Boolean getActivities() {
		return activities;
	}
	public void setActivities(Boolean activities) {
		this.activities = activities;
	}
	public Boolean getResources() {
		return resources;
	}
	public void setResources(Boolean resources) {
		this.resources = resources;
	}
	public Boolean getInfo() {
		return info;
	}
	public void setInfo(Boolean info) {
		this.info = info;
	}
	
}
