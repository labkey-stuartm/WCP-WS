package com.studymetadata.exception;

/**
 * 
 * @author BTC
 * @since Jan 4, 2018 3:36:01 PM
 *
 */
public interface ErrorCodes {

	String DAO_FACTORY_INIT_EXP = "DAO100";
	String DAO_EXP = "ERRDAO";
	String ORC_FACTORY_INIT_EXP = "ORC100";
	String INVALID_AUTH_CODE = "INVALID_AUTH_CODE";
	String UNKNOWN = "UNKNOWN";
	String INVALID_AUTHENTICATION = "INVALID_AUTHENTICATION";
	String INVALID_AUTHORIZATION = "INVALID_AUTHORIZATION";
	String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
	String NO_RECORD = "NO_RECORD_FOUND";
	String MULTIPLE_RECORDS = "ERRDUP";
	String NO_RECORD_UPDATE = "ERRNRU";
	String NO_RECORD_INSERT = "ERRNRI";
	String INVALID_DATE = "INVALID_DATE";
	String INACTIVE = "INACTIVE";
	String ACTIVE = "ACTIVE";
	String INACTIVE_MSG = "Service unavailable. Please try later.";
	String INVALID_INPUT = "INVALID_INPUT";
	String INVALID_EMAIL = "INVALID_EMAIL";
	String NO_DATA = "NODATA";
	String STATUS_100 = "100"; // OK
	String STATUS_101 = "101"; // Invalid Authentication (authKey is not valid).
	String STATUS_102 = "102"; // Invalid Inputs (If any of the input parameter
								// is missing).
	String STATUS_103 = "103"; // No Data available.
	String STATUS_104 = "104"; // Unknown Error
	String STATUS_105 = "105"; // If there is no data to update.
}
