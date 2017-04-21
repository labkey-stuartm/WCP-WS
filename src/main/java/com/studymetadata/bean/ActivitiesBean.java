package com.studymetadata.bean;

public class ActivitiesBean {
	private String activityId = "";
	private String activityVersion = "";
	private String title = "";
	private String type = ""; //active task/questionnaire
	private String startTime = "";
	private String endTime = "";
	private Boolean branching = false;
	private String lastModified = "";
	private ActivityFrequencyBean frequency = new ActivityFrequencyBean();
	
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public ActivityFrequencyBean getFrequency() {
		return frequency;
	}
	public void setFrequency(ActivityFrequencyBean frequency) {
		this.frequency = frequency;
	}
	public String getActivityVersion() {
		return activityVersion;
	}
	public void setActivityVersion(String activityVersion) {
		this.activityVersion = activityVersion;
	}
	public Boolean getBranching() {
		return branching;
	}
	public void setBranching(Boolean branching) {
		this.branching = branching;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	
}
