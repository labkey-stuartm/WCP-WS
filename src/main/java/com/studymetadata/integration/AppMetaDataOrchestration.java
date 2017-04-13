package com.studymetadata.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.studymetadata.bean.AppResponse;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.dao.AppMetaDataDao;
import com.studymetadata.dto.UserDto;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.Mail;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class AppMetaDataOrchestration {
private static final Logger LOGGER = Logger.getLogger(AppMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
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
	public NotificationsResponse notifications(String skip) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			notificationsResponse = appMetaDataDao.notifications(skip);
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
			flag = Mail.sendemail(propMap.get("fda.smd.feedback"), subject, body);
			
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
			flag = Mail.sendemail(propMap.get("fda.smd.contactus"), subject, body);
			
			if(flag){
				response.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataOrchestration - contactUsDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataOrchestration - contactUsDetails() :: Ends");
		return response;
	}
}
