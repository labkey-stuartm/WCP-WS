package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 *
 */
public class QuestionInfoBean {
	private String activityId = "";
	private String activityVersion = "";
	private String key = ""; //question key
	
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityVersion() {
		return activityVersion;
	}
	public void setActivityVersion(String activityVersion) {
		this.activityVersion = activityVersion;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
