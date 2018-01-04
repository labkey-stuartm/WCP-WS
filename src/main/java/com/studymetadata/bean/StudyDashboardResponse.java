package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:15:11 PM
 *
 */
public class StudyDashboardResponse {
	
	private String message = StudyMetaDataConstants.FAILURE;
	private DashboardBean dashboard = new DashboardBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public DashboardBean getDashboard() {
		return dashboard;
	}
	public void setDashboard(DashboardBean dashboard) {
		this.dashboard = dashboard;
	}
}
