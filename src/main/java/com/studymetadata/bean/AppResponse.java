package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

public class AppResponse {
	String message = StudyMetaDataConstants.FAILURE;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
