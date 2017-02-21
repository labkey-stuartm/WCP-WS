package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

public class SuccessResponse {
	private String resultType = "FAILURE";
	
	public String getResultType() {
		return null == resultType ? StudyMetaDataConstants.FAILURE : resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

}
