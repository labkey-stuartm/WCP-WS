package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

public class AppUpdatesResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private Boolean forceUpdate = false;
	private String currentVersion = "";
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getForceUpdate() {
		return forceUpdate;
	}
	public void setForceUpdate(Boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
}
