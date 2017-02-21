package com.studymetadata.bean;


public class FailureResponse extends SuccessResponse {
	private ErrorBean errors = new ErrorBean();

	public ErrorBean getErrors() {
		return errors;
	}
	public void setErrors(ErrorBean errors) {
		this.errors = errors;
	}

}
