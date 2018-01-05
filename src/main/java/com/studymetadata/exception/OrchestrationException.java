package com.studymetadata.exception;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:36:21 PM
 *
 */
public class OrchestrationException extends CommonException {

	/**
	 * 
	 * @param pErrorCode
	 * @param arg1
	 */
	public OrchestrationException(String pErrorCode, Throwable arg1) {
		super(pErrorCode, arg1);
	}

	private static final long serialVersionUID = 1081061948859074979L;

}
