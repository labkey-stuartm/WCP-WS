package com.studymetadata.bean;

public class SettingsBean{
	
	private boolean enrolling = false;
	private String platform = ""; //ios/android/both
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
