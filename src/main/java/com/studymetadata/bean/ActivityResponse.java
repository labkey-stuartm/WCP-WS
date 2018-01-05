package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides details about the activity {@link ActivitiesBean} and status in the
 * response.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:05:28 PM
 *
 */
public class ActivityResponse {

	private String message = StudyMetaDataConstants.FAILURE;
	private List<ActivitiesBean> activities = new ArrayList<>();

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
