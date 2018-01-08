package com.studymetadata.bean;

/**
 * Provides app updates details in response.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:07:33 PM
 *
 */
public class AppUpdatesResponse {

	private String message = "";
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
