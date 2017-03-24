package com.studymetadata.bean;

import java.util.HashMap;
import java.util.Map;

import com.studymetadata.util.StudyMetaDataConstants;

public class StudyUpdatesResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private Map<String, Object> updates = new HashMap<String, Object>();
	private String currentVersion = ""; //current study version
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, Object> getUpdates() {
		return updates;
	}
	public void setUpdates(Map<String, Object> updates) {
		this.updates = updates;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
}
