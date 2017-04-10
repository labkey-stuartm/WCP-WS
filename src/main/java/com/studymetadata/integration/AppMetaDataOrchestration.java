package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.dao.AppMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
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
	public TermsPolicyResponse termsPolicy(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: AppMetaDataOrchestration - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			termsPolicyResponse = appMetaDataDao.termsPolicy(studyId);
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
}
