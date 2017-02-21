package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class NotificationsResponse {
	private String message = "FAILURE";
	private List<NotificationsBean> notifications = new ArrayList<NotificationsBean>();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<NotificationsBean> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<NotificationsBean> notifications) {
		this.notifications = notifications;
	}
}
