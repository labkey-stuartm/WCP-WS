package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class ActivityResponse {
	private String message = "FAILURE";
	private List<ActivitiesBean> activities = new ArrayList<ActivitiesBean>(); //<refer Appendix 7 for structure>
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ActivitiesBean> getActivities() {
		return activities;
	}
	public void setActivities(List<ActivitiesBean> activities) {
		this.activities = activities;
	}
}
