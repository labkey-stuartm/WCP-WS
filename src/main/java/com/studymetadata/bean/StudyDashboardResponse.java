package com.studymetadata.bean;

public class StudyDashboardResponse {
	private String message = "FAILURE";
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
