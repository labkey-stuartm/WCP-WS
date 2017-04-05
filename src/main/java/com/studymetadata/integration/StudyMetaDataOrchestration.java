package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.dao.ActivityMetaDataDao;
import com.studymetadata.dao.StudyMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.ConsentDocumentResponse;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.ResourcesResponse;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;
import com.studymetadata.bean.TermsPolicyResponse;

public class StudyMetaDataOrchestration {
	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	StudyMetaDataDao studyMetaDataDao = new StudyMetaDataDao();
	ActivityMetaDataDao activityMetaDataDao = new ActivityMetaDataDao();

	/**
	 * @author Mohan
	 * @param authorization
	 * @return hasValidAuthorization
	 * @throws OrchestrationException
	 */
	public boolean isValidAuthorizationId(String authorization) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
		boolean hasValidAuthorization = false;
		try{
			hasValidAuthorization = studyMetaDataDao.isValidAuthorizationId(authorization);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Ends");
		return hasValidAuthorization;
	}
	
	/**
	 * @author Mohan
	 * @return GatewayInfoResponse
	 * @throws OrchestrationException
	 */
	public GatewayInfoResponse gatewayAppResourcesInfo(String authorization) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
		try{
			gatewayInfo = studyMetaDataDao.gatewayAppResourcesInfo(authorization);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Ends");
		return gatewayInfo;
	}
	
	/**
	 * @author Mohan
	 * @return StudyResponse
	 * @throws OrchestrationException
	 */
	public StudyResponse studyList(String authorization) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		try{
			studyResponse = studyMetaDataDao.studyList(authorization);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - studyList() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Ends");
		return studyResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return EligibilityConsentResponse
	 * @throws OrchestrationException
	 */
	public EligibilityConsentResponse eligibilityConsentMetadata(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Starts");
		EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
		try{
			eligibilityConsentResponse = studyMetaDataDao.eligibilityConsentMetadata(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - eligibilityConsentMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}
	
	/**
	 * 
	 * @author Mohan
	 * @param studyId
	 * @param consentVersion
	 * @param activityId
	 * @param activityVersion
	 * @return ConsentDocumentResponse
	 * @throws OrchestrationException
	 */
	public ConsentDocumentResponse consentDocument(String studyId, String consentVersion, String activityId, String activityVersion) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		try{
			consentDocumentResponse = studyMetaDataDao.consentDocument(studyId, consentVersion, activityId, activityVersion);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - consentDocument() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Ends");
		return consentDocumentResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return ResourcesResponse
	 * @throws OrchestrationException
	 */
	public ResourcesResponse resourcesForStudy(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Starts");
		ResourcesResponse resourcesResponse = new ResourcesResponse();
		try{
			resourcesResponse = studyMetaDataDao.resourcesForStudy(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - resourcesForStudy() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Ends");
		return resourcesResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return StudyResponse
	 * @throws OrchestrationException
	 */
	public StudyInfoResponse studyInfo(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Starts");
		StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
		try{
			studyInfoResponse = studyMetaDataDao.studyInfo(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - studyInfo() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Ends");
		return studyInfoResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return ActivityResponse
	 * @throws OrchestrationException
	 */
	public ActivityResponse studyActivityList(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try{
			activityResponse = activityMetaDataDao.studyActivityList(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - studyActivityList() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyActivityList() :: Ends");
		return activityResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return ActivityResponse
	 * @throws OrchestrationException
	 */
	public ActivityMetaDataResponse studyActivityMetadata(String studyId, String activityId, String activityVersion) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		try{
			activityMetaDataResponse = activityMetaDataDao.studyActivityMetadata(studyId, activityId, activityVersion);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - studyActivityMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyActivityMetadata() :: Ends");
		return activityMetaDataResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return StudyDashboardResponse
	 * @throws OrchestrationException
	 */
	public StudyDashboardResponse studyDashboardInfo(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try{
			studyDashboardResponse = studyMetaDataDao.studyDashboardInfo(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - studyDashboardInfo() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return TermsPolicyResponse
	 * @throws OrchestrationException
	 */
	public TermsPolicyResponse termsPolicy(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			termsPolicyResponse = studyMetaDataDao.termsPolicy(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - termsPolicy() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}
	
	/**
	 * @author Mohan
	 * @param skip
	 * @return NotificationsResponse
	 * @throws OrchestrationException
	 */
	public NotificationsResponse notifications(String skip) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			notificationsResponse = studyMetaDataDao.notifications(skip);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - notifications() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - notifications() :: Ends");
		return notificationsResponse;
	}
}