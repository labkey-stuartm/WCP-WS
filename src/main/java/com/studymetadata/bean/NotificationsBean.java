package com.studymetadata.bean;

public class NotificationsBean {
	private String notificationId = "";
	private String notificationText = ""; //<refer Appendix11 for notification structure>
	
	public String getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	public String getNotificationText() {
		return notificationText;
	}
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}
	
}
