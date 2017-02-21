package com.studymetadata.bean;

public class StudyActivityResponse {
	private String message = "FAILURE";
	private ActivitiesBean activity = new ActivitiesBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ActivitiesBean getActivity() {
		return activity;
	}
	public void setActivity(ActivitiesBean activity) {
		this.activity = activity;
	}
}
