package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:15:50 PM
 *
 */
public class SuccessResponse {
	
	private String resultType = StudyMetaDataConstants.FAILURE;

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

}
