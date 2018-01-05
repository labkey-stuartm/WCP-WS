package com.studymetadata.bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides brief details about resource.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:13:43 PM
 *
 */
public class ResourcesBean {

	private String title = "";
	private String type = "";
	private String resourcesId = "";
	private String content = "";
	private String audience = "";
	private String notificationText = "";
	private Map<String, Object> availability = new LinkedHashMap<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public Map<String, Object> getAvailability() {
		return availability;
	}

	public void setAvailability(Map<String, Object> availability) {
		this.availability = availability;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

}
