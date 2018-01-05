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

/**
 * Study metadata service that communicates with study metadata
 * {@link StudyMetaDataDao} repository.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:37:11 PM
 *
 */
public class StudyMetaDataOrchestration {

	private static final Logger LOGGER = Logger
			.getLogger(StudyMetaDataOrchestration.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	StudyMetaDataDao studyMetaDataDao = new StudyMetaDataDao();

	/**
	 * Check Authorization for the provided authorization identifier
	 * 
	 * @author BTC
	 * @param authorization
	 * @return {@link Boolean}
	 * @throws OrchestrationException
	 */
	public boolean isValidAuthorizationId(String authorization)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
		boolean hasValidAuthorization = false;
		try {
			hasValidAuthorization = studyMetaDataDao
					.isValidAuthorizationId(authorization);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Ends");
		return hasValidAuthorization;
	}

	/**
	 * Get Gateway info and Gateway resources data
	 * 
	 * @author BTC
	 * @param authorization
	 * @return {@link GatewayInfoResponse}
	 * @throws OrchestrationException
	 */
	public GatewayInfoResponse gatewayAppResourcesInfo(String authorization)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
		try {
			gatewayInfo = studyMetaDataDao
					.gatewayAppResourcesInfo(authorization);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Ends");
		return gatewayInfo;
	}

	/**
	 * Get all the configured studies from the WCP
	 * 
	 * @author BTC
	 * @param authorization
	 * @return {@link StudyResponse}
	 * @throws OrchestrationException
	 */
	public StudyResponse studyList(String authorization)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		try {
			studyResponse = studyMetaDataDao.studyList(authorization);
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataOrchestration - studyList() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Ends");
		return studyResponse;
	}

	/**
	 * Get eligibility and consent info for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link EligibilityConsentResponse}
	 * @throws OrchestrationException
	 */
	public EligibilityConsentResponse eligibilityConsentMetadata(String studyId)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Starts");
		EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
		try {
			eligibilityConsentResponse = studyMetaDataDao
					.eligibilityConsentMetadata(studyId);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - eligibilityConsentMetadata() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}

	/**
	 * Get consent document by passing the consent version or the activity
	 * identifier and activity version for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @param consentVersion
	 * @param activityId
	 * @param activityVersion
	 * @return {@link ConsentDocumentResponse}
	 * @throws OrchestrationException
	 */
	public ConsentDocumentResponse consentDocument(String studyId,
			String consentVersion, String activityId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		try {
			consentDocumentResponse = studyMetaDataDao.consentDocument(studyId,
					consentVersion, activityId, activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - consentDocument() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Ends");
		return consentDocumentResponse;
	}

	/**
	 * Get resources metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link ResourcesResponse}
	 * @throws OrchestrationException
	 */
	public ResourcesResponse resourcesForStudy(String studyId)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Starts");
		ResourcesResponse resourcesResponse = new ResourcesResponse();
		try {
			resourcesResponse = studyMetaDataDao.resourcesForStudy(studyId);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - resourcesForStudy() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Ends");
		return resourcesResponse;
	}

	/**
	 * Get study metadata for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link StudyInfoResponse}
	 * @throws OrchestrationException
	 */
	public StudyInfoResponse studyInfo(String studyId)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Starts");
		StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
		try {
			studyInfoResponse = studyMetaDataDao.studyInfo(studyId);
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataOrchestration - studyInfo() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Ends");
		return studyInfoResponse;
	}

	/**
	 * Check mail service
	 * 
	 * @author BTC
	 * @return {@link Boolean}
	 * @throws OrchestrationException
	 */
	public boolean sampleMail() throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - sampleMail() :: Starts");
		boolean flag = false;
		try {
			flag = Mail.sendemail("apps@boston-technology.com", "Test Mail",
					"It works!");
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataOrchestration - sampleMail() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - sampleMail() :: Ends");
		return flag;
	}

	/**
	 * Check study for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @return {@link Boolean}
	 * @throws OrchestrationException
	 */
	public boolean isValidStudy(String studyId) throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Starts");
		boolean flag = false;
		try {
			flag = studyMetaDataDao.isValidStudy(studyId);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Ends");
		return flag;
	}

	/**
	 * Check activity for the provided study and activity identifier
	 * 
	 * @author BTC
	 * @param activityId
	 * @param studyId
	 * @param activityVersion
	 * @return {@link Boolean}
	 * @throws OrchestrationException
	 */
	public boolean isValidActivity(String activityId, String studyId,
			String activityVersion) throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Starts");
		boolean flag = false;
		try {
			flag = studyMetaDataDao.isValidActivity(activityId, studyId,
					activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - isValidActivity() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Ends");
		return flag;
	}

	/**
	 * Check whether activity is questionnaire for the provided study and
	 * activity identifier
	 * 
	 * @author BTC
	 * @param activityId
	 * @param studyId
	 * @param activityVersion
	 * @return {@link Boolean}
	 * @throws OrchestrationException
	 */
	public boolean isActivityTypeQuestionnaire(String activityId,
			String studyId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Starts");
		boolean flag = false;
		try {
			flag = studyMetaDataDao.isActivityTypeQuestionnaire(activityId,
					studyId, activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: ERROR",
					e);
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Ends");
		return flag;
	}
}