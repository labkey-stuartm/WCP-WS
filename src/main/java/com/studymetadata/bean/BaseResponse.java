package com.studymetadata.bean;

/**
 * Provides error details in response.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:07:41 PM
 *
 */
public class BaseResponse {

	private String errorMessage = "";
	private String errorCode = "";
	private String resultType = "";

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

}
