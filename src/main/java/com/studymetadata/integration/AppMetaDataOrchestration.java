package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.bean.AppResponse;
import com.studymetadata.bean.AppUpdatesResponse;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.StudyUpdatesResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.dao.AppMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.Mail;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class AppMetaDataOrchestration {
private static final Logger LOGGER = Logger.getLogger(AppMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();
	
	AppMetaDataDao appMetaDataDao = new AppMetaDataDao();
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return TermsPolicyResponse
	 * @throws OrchestrationException
	 */
	public TermsPolicyResponse termsPolicy() throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			termsPolicyResponse = appMetaDataDao.termsPolicy();
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - termsPolicy() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}
	
	/**
	 * @author Mohan
	 * @param skip
	 * @return NotificationsResponse
	 * @throws OrchestrationException
	 */
	public NotificationsResponse notifications(String skip, String authorization) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			notificationsResponse = appMetaDataDao.notifications(skip, authorization);
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - notifications() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - notifications() :: Ends");
		return notificationsResponse;
	}
	
	/**
	 * @author Mohan
	 * @param subject
	 * @param body
	 * @return AppResponse
	 * @throws OrchestrationException
	 */
	public AppResponse feedback(String subject, String body) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - feedback() :: Starts");
		AppResponse response = new AppResponse();
		Boolean flag = false;
		try{
			String feedbackSubject = "My Studies App Feedback: "+subject;
			String feedbackBody = "<div>"
								 +"<div><span>Hi</span></div><br>"
								 +"<div><span>A user of the FDA My Studies mobile app has provided feedback via the app. Here&#39;s the content of the feedback:</span></div><br>"
								 +"<div><span><i>"+body+"</i></span></div><br>"
								 +"<div>"
								 +"<span>Thanks,</span><br><span>"+propMap.get("fda.smd.email.title")+"</span><br>"
								 +"<span>---------------------------------------------------------</span><br>"
								 +"<span style='font-size:9px;'>PS - This is an auto-generated email. Please do not reply.</span>"
								 +"</div>"
								 +"</div>";
			flag = Mail.sendemail(propMap.get("fda.smd.feedback"), feedbackSubject, feedbackBody);
			if(flag){
				response.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - feedback() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - feedback() :: Ends");
		return response;
	}
	
	
	/**
	 * @author Mohan
	 * @param subject
	 * @param body
	 * @param firstName
	 * @param email
	 * @return AppResponse
	 * @throws OrchestrationException
	 */
	public AppResponse contactUsDetails(String subject, String body, String firstName, String email) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - contactUsDetails() :: Starts");
		AppResponse response = new AppResponse();
		Boolean flag = false;
		try{
			String contactUsSubject = "My Studies App HelpDesk: '"+subject+"'";
			String contactUsContent = "<div>"
									 +"<div><span>Hi</span></div><br>"
									 +"<div style='padding-bottom:10px;'><span>A user of the FDA My Studies mobile app has reached out to the Helpdesk. Below are the contact form details:</span></div>"
									 +"<div>"
									 +"<div>___________________________________________</div>"
									 +"<div style='padding-top:20px;'>First Name: "+firstName+"</div>"
									 +"<div style='padding-top:10px;'>Email: <a href='mailto:"+email+"'>"+email+"</a></div>"
									 +"<div style='padding-top:10px;'>Subject: "+subject+"</div>"
									 +"<div style='padding-top:10px;padding-bottom:10px'>Message: "+body+"</div>"
									 +"</div>"
									 +"<div>___________________________________________</div><br>"
									 +"<div style='padding-top:10px;'><span>Please respond to the app user at the email he/she has provided.</span></div><br>"
									 +"<div>"
									 +"<span>Thanks,</span><br><span>"+propMap.get("fda.smd.email.title")+"</span><br>"
									 +"<span>---------------------------------------------------------</span><br>"
									 +"<span style='font-size:9px;'>PS - This is an auto-generated email. Please do not reply.</span>"
									 +"</div>"
									 +"</div>";
			flag = Mail.sendemail(propMap.get("fda.smd.contactus"), contactUsSubject, contactUsContent);
			
			if(flag){
				response.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - contactUsDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - contactUsDetails() :: Ends");
		return response;
	}
	
	/**
	 * @author Mohan
	 * @param appVersion
	 * @param os
	 * @return AppUpdatesResponse
	 * @throws OrchestrationException
	 */
	public AppUpdatesResponse appUpdates(String appVersion, String app) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - appUpdates() :: Starts");
		AppUpdatesResponse appUpdates = new AppUpdatesResponse();
		try{
			appUpdates = appMetaDataDao.appUpdates(appVersion, app);
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - appUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - appUpdates() :: Ends");
		return appUpdates;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @param studyVersion
	 * @return StudyUpdatesResponse
	 * @throws OrchestrationException
	 */
	public StudyUpdatesResponse studyUpdates(String studyId, String studyVersion) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - studyUpdates() :: Starts");
		StudyUpdatesResponse studyUpdates = new StudyUpdatesResponse();
		try{
			studyUpdates = appMetaDataDao.studyUpdates(studyId, studyVersion);
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - studyUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - studyUpdates() :: Ends");
		return studyUpdates;
	}
	
	/**
	 * @author Mohan
	 * @param forceUpdate
	 * @param osType
	 * @param appVersion
	 * @return String
	 * @throws OrchestrationException
	 */
	public String updateAppVersionDetails(String forceUpdate, String osType, String appVersion, String bundleId, String customStudyId, String message) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - updateAppVersionDetails() :: Starts");
		String updateAppVersionResponse = "OOPS! Something went wrong.";
		try{
			updateAppVersionResponse = appMetaDataDao.updateAppVersionDetails(forceUpdate, osType, appVersion, bundleId, customStudyId, message);
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - updateAppVersionDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - updateAppVersionDetails() :: Ends");
		return updateAppVersionResponse;
	}
	
	
	/**
	 * @author Mohan
	 * @param dbQuery
	 * @return String
	 * @throws OrchestrationException
	 */
	public String interceptorDataBaseQuery(String dbQuery) throws OrchestrationException{
		//LOGGER.info("INFO: AppMetaDataOrchestration - interceptorDataBaseQuery() :: Starts");
		String message = "OOPS! Something went wrong.";
		try{
			message = appMetaDataDao.interceptorDataBaseQuery(dbQuery);
		}catch(Exception e){
			//LOGGER.error("AppMetaDataOrchestration - interceptorDataBaseQuery() :: ERROR", e);
		}
		//LOGGER.info("INFO: AppMetaDataOrchestration - interceptorDataBaseQuery() :: Ends");
		return message;
	}
}
