package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

public class NotificationsResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private List<NotificationsBean> notifications = new ArrayList<>();
	
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
