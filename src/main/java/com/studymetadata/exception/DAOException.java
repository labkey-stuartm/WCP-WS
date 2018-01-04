package com.studymetadata.exception;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:33:54 PM
 *
 */
public class DAOException extends CommonException {

	/**
	 * 
	 * @param pErrorCode
	 * @param arg1
	 */
	public DAOException(String pErrorCode, Throwable arg1) {
		super(pErrorCode, arg1);
	}

	private static final long serialVersionUID = 3618589435796185208L;

}
