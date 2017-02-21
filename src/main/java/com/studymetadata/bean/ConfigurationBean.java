package com.studymetadata.bean;

public class ConfigurationBean {
	private String anchorDateType = ""; //joining/other
	private String activityId = "";
	private String key = "";
	private Integer start = 0; //elapsed days from anchor date
	private Integer end = 0; //elapsed days from anchor date
	
	private String startTime = "";
	private String endTime = "";
	private String lifetime = "";
	private String runLifetime = "";
	
	private String timeRangeType = "";
	private String type = ""; //questionnaire/active task/health kit
	
	public String getAnchorDateType() {
		return anchorDateType;
	}
	public void setAnchorDateType(String anchorDateType) {
		this.anchorDateType = anchorDateType;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
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
	public String getLifetime() {
		return lifetime;
	}
	public void setLifetime(String lifetime) {
		this.lifetime = lifetime;
	}
	public String getRunLifetime() {
		return runLifetime;
	}
	public void setRunLifetime(String runLifetime) {
		this.runLifetime = runLifetime;
	}
	public String getTimeRangeType() {
		return timeRangeType;
	}
	public void setTimeRangeType(String timeRangeType) {
		this.timeRangeType = timeRangeType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
