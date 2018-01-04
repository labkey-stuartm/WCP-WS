package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:15:40 PM
 *
 */
public class StudyUpdatesResponse {
	
	private String message = StudyMetaDataConstants.FAILURE;
	private StudyUpdatesBean updates = new StudyUpdatesBean();
	private String currentVersion = "";
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public StudyUpdatesBean getUpdates() {
		return updates;
	}
	public void setUpdates(StudyUpdatesBean updates) {
		this.updates = updates;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
}
