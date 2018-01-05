package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides status of the request in response.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:07:24 PM
 *
 */
public class AppResponse {

	String message = StudyMetaDataConstants.FAILURE;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
