package com.studymetadata.bean;

/**
 * Provides failure {@link ErrorBean} details in response.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:11:21 PM
 *
 */
public class FailureResponse extends SuccessResponse {

	private ErrorBean errors = new ErrorBean();

	public ErrorBean getErrors() {
		return errors;
	}

	public void setErrors(ErrorBean errors) {
		this.errors = errors;
	}

}
