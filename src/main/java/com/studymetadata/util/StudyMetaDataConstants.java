package com.studymetadata.util;

import java.text.SimpleDateFormat;

public interface StudyMetaDataConstants {
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String INACTIVE = "INACTIVE";
	
	public final String SESSION_EXPIRED_MSG = "Session expired.";
	public final String CONNECTION_ERROR_MSG = "Oops, something went wrong. Please try again after sometime.";
	public final String ACCOUNT_DEACTIVATE_ERROR_MSG = "Your account has been deactivated.";
	public final String INVALID_INPUT_ERROR_MSG = "Invalid input.";
	public final String LOGIN_FAILURE = "Please check your login credentials.";
	
	SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SDF_DATE_TIME_12 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat SDF_TIME_24 = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat SDF_TIME_12 = new SimpleDateFormat("hh:mm a");
	
	SimpleDateFormat SDF_MMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("EEE, MMM dd, yyyy");
	SimpleDateFormat DISPLAY_DATE_TIME = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a");
	SimpleDateFormat DISPLAY_DATE_TIME_MIN = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
	
	public static final String PASSWORD_SALT = "BTCSoft";
	
	//Study related constants
	public static final String STUDY_TYPE_GT = "GT";
	public static final String STUDY_TYPE_SD = "SD";
	public static final String STUDY_PLATFORM_TYPE_IOS = "I";
	public static final String STUDY_PLATFORM_TYPE_ANDROID = "A";
	
	//Study Status
    public static final String STUDY_PRE_LAUNCH = "Pre-launch";
    public static final String STUDY_ACTIVE = "Active";
    public static final String STUDY_PAUSED = "Paused";
    public static final String STUDY_DEACTIVATED = "Deactivated";
    
     //Study Category type
    public static final String STUDY_REF_CATEGORIES = "Categories";
    public static final String STUDY_REF_SPONSERS = "Research Sponsors";
	
	//Authorization
	public static final String ANDROID_APP_TOKEN = "a7296a62-44e8-4a06-8d85-50bc861d6c64";
	public static final String IOS_APP_TOKEN = "ee91a4f6-d9c4-4ee9-a0e2-5682c5b1c916";
	public static final String IOS_BUNDLE_ID = "com.btc.FDA";
	public static final String ANDROID_BUNDLE_ID = "com.harvard.fda";
	
	public static final String YES = "Yes";
	public static final String NO = "No";
	
	//Status messages
	public static final String INVALID_AUTHORIZATION = "Invalid Authorization key";
	public static final String INVALID_INPUT = "Invalid inputs";
	public static final String NO_RECORD = "No records found";
	
	//type constants
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_HTML = "html";
	public static final String TYPE_PDF = "pdf";
	
	public static final String TYPE_TOKEN = "token";
	public static final String TYPE_BOTH = "both";
	public static final String TYPE_TEST = "test";
	
	public static final String TYPE_ACTIVE_TASK = "active task";
	public static final String TYPE_QUESTIONNAIRE = "questionnaire";
	
	public static final String TYPE_UTF8 = "UTF-8";
	
}
