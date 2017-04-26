package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.dao.StudyMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.Mail;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.ConsentDocumentResponse;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.ResourcesResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;

public class StudyMetaDataOrchestration {
	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	StudyMetaDataDao studyMetaDataDao = new StudyMetaDataDao();

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
	 * This method is used to test the sample mail
	 * 
	 * @author Mohan
	 * @return boolean
	 * @throws OrchestrationException
	 */
	public boolean sampleMail() throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - sampleMail() :: Starts");
		boolean flag = false;
		try{
			flag = Mail.sendemail("mohant@boston-technology.com","Test Mail", "Hello!");
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - sampleMail() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - sampleMail() :: Ends");
		return flag;
	}
	
	/**
	 * This method is used to validate the studyId exists or not
	 * 
	 * @author Mohan
	 * @param studyId
	 * @return boolean
	 * @throws OrchestrationException
	 */
	public boolean isValidStudy(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Starts");
		boolean flag = false;
		try{
			flag = studyMetaDataDao.isValidStudy(studyId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Ends");
		return flag;
	}
	
	/**
	 * This method is used to validate the activity exists or not
	 * 
	 * @author Mohan
	 * @param activityId
	 * @return boolean
	 * @throws OrchestrationException
	 */
	public boolean isValidActivity(String activityId) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Starts");
		boolean flag = false;
		try{
			flag = studyMetaDataDao.isValidActivity(activityId);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidActivity() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Ends");
		return flag;
	}
	
}