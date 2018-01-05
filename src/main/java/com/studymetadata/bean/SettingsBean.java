package com.studymetadata.bean;

/**
 * Provides study settings details.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:14:11 PM
 *
 */
public class SettingsBean {

	private boolean enrolling = false;
	private String platform = "";
	private boolean rejoin = false;

	public boolean isEnrolling() {
		return enrolling;
	}

	public void setEnrolling(boolean enrolling) {
		this.enrolling = enrolling;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public boolean isRejoin() {
		return rejoin;
	}

	public void setRejoin(boolean rejoin) {
		this.rejoin = rejoin;
	}
}
