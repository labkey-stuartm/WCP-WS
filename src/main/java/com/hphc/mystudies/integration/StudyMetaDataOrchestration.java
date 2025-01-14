/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.integration;

import com.hphc.mystudies.bean.ConsentDocumentResponse;
import com.hphc.mystudies.bean.EligibilityConsentResponse;
import com.hphc.mystudies.bean.GatewayInfoResponse;
import com.hphc.mystudies.bean.ParticipantPropertiesMetadata;
import com.hphc.mystudies.bean.ParticipantPropertiesResponseBean;
import com.hphc.mystudies.bean.ParticipantPropertyMetaData;
import com.hphc.mystudies.bean.ResourcesResponse;
import com.hphc.mystudies.bean.StudyInfoResponse;
import com.hphc.mystudies.bean.StudyResponse;
import com.hphc.mystudies.dao.StudyMetaDataDao;
import com.hphc.mystudies.dto.ParticipantPropertiesBO;
import com.hphc.mystudies.dto.ParticipantPropertiesDraftBO;
import com.hphc.mystudies.exception.OrchestrationException;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Study metadata service that communicates with study metadata {@link StudyMetaDataDao} repository.
 *
 * @author BTC
 */
public class StudyMetaDataOrchestration {

  private static final Logger LOGGER = Logger.getLogger(StudyMetaDataOrchestration.class);

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  StudyMetaDataDao studyMetaDataDao = new StudyMetaDataDao();

  /**
   * Check Authorization for the provided authorization identifier
   *
   * @param authorization the Basic Authorization
   * @return {@link Boolean}
   * @throws OrchestrationException
   * @author BTC
   */
  public boolean isValidAuthorizationId(String authorization) throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
    boolean hasValidAuthorization = false;
    try {
      hasValidAuthorization = studyMetaDataDao.isValidAuthorizationId(authorization);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Ends");
    return hasValidAuthorization;
  }

  /**
   * Get Gateway info and Gateway resources data
   *
   * @param authorization the Basic Authorization
   * @return {@link GatewayInfoResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public GatewayInfoResponse gatewayAppResourcesInfo(String authorization)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Starts");
    GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
    try {
      gatewayInfo = studyMetaDataDao.gatewayAppResourcesInfo(authorization);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - gatewayAppResourcesInfo() :: Ends");
    return gatewayInfo;
  }

  /**
   * Get all the configured studies from the WCP
   *
   * @param authorization the Basic Authorization
   * @return {@link StudyResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public StudyResponse studyList(
      String authorization, String applicationId, String orgId, String language)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Starts");
    StudyResponse studyResponse = new StudyResponse();
    try {
      studyResponse = studyMetaDataDao.studyList(authorization, applicationId, orgId, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - studyList() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - studyList() :: Ends");
    return studyResponse;
  }

  /**
   * Get eligibility and consent info for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link EligibilityConsentResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public EligibilityConsentResponse eligibilityConsentMetadata(String studyId, String language)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Starts");
    EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
    try {
      eligibilityConsentResponse = studyMetaDataDao.eligibilityConsentMetadata(studyId, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - eligibilityConsentMetadata() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - eligibilityConsentMetadata() :: Ends");
    return eligibilityConsentResponse;
  }

  /**
   * Get consent document by passing the consent version or the activity identifier and activity
   * version for the provided study identifier
   *
   * @param studyId the study identifier
   * @param consentVersion the consent version
   * @param activityId the activity identifier
   * @param activityVersion the activity version
   * @return {@link ConsentDocumentResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public ConsentDocumentResponse consentDocument(
      String studyId,
      String consentVersion,
      String activityId,
      String activityVersion,
      String language)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Starts");
    ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
    try {
      consentDocumentResponse =
          studyMetaDataDao.consentDocument(
              studyId, consentVersion, activityId, activityVersion, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - consentDocument() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - consentDocument() :: Ends");
    return consentDocumentResponse;
  }

  /**
   * Get resources metadata for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link ResourcesResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public ResourcesResponse resourcesForStudy(String studyId, String language)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Starts");
    ResourcesResponse resourcesResponse = new ResourcesResponse();
    try {
      resourcesResponse = studyMetaDataDao.resourcesForStudy(studyId, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - resourcesForStudy() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - resourcesForStudy() :: Ends");
    return resourcesResponse;
  }

  /**
   * Get study metadata for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link StudyInfoResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public StudyInfoResponse studyInfo(String studyId, String language)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Starts");
    StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
    try {
      studyInfoResponse = studyMetaDataDao.studyInfo(studyId, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - studyInfo() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - studyInfo() :: Ends");
    return studyInfoResponse;
  }

  /**
   * Check study for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link Boolean}
   * @throws OrchestrationException
   * @author BTC
   */
  public boolean isValidStudy(String studyId) throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Starts");
    boolean flag = false;
    try {
      flag = studyMetaDataDao.isValidStudy(studyId);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Ends");
    return flag;
  }

  /**
   * Check activity for the provided study and activity identifier
   *
   * @param activityId the activity identifier
   * @param studyId the study identifier
   * @param activityVersion the activity version
   * @return {@link Boolean}
   * @throws OrchestrationException
   * @author BTC
   */
  public boolean isValidActivity(String activityId, String studyId, String activityVersion)
      throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Starts");
    boolean flag = false;
    try {
      flag = studyMetaDataDao.isValidActivity(activityId, studyId, activityVersion);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidActivity() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Ends");
    return flag;
  }

  /**
   * Check whether activity is questionnaire for the provided study and activity identifier
   *
   * @param activityId the activity identifier
   * @param studyId the study identifier
   * @param activityVersion the activity version
   * @return {@link Boolean}
   * @throws OrchestrationException
   * @author BTC
   */
  public boolean isActivityTypeQuestionnaire(
      String activityId, String studyId, String activityVersion) throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Starts");
    boolean flag = false;
    try {
      flag = studyMetaDataDao.isActivityTypeQuestionnaire(activityId, studyId, activityVersion);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Ends");
    return flag;
  }

  /**
   * Check Enrollment Token is valid or not.
   *
   * @param token
   * @return {@link Boolean}
   * @throws OrchestrationException
   * @author BTC
   */
  public boolean isValidToken(String token) throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidToken() :: Starts");
    boolean flag = false;
    try {
      flag = studyMetaDataDao.isValidToken(token);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidToken() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidToken() :: Ends");
    return flag;
  }

  /**
   * Get all the configured studies from the WCP
   *
   * @param studyId
   * @return {@link StudyResponse}
   * @throws OrchestrationException
   * @author BTC
   */
  public StudyResponse study(String studyId, String language) throws OrchestrationException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - study() :: Starts");
    StudyResponse studyResponse = new StudyResponse();
    try {
      studyResponse = studyMetaDataDao.study(studyId, language);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - study() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - study() :: Ends");
    return studyResponse;
  }

  public ParticipantPropertiesResponseBean getParticipantProperties(
      String studyId, String studyVersion) throws Exception {
    LOGGER.info("INFO: StudyMetaDataOrchestration - getParticipantProperties() :: Starts");
    ParticipantPropertiesResponseBean participantPropertiesResponseBean =
        new ParticipantPropertiesResponseBean();
    List<ParticipantPropertyMetaData> participantPropertyMetadataList = new ArrayList<>();
    ParticipantPropertiesMetadata participantPropertiesMetadata =
        new ParticipantPropertiesMetadata();
    List<ParticipantPropertiesBO> participantPropertiesBOList = null;
    List<ParticipantPropertiesDraftBO> participantPropertiesDraftBOList = null;
    try {
      if (StringUtils.isNotEmpty(studyVersion)) {
        String studyVer = studyVersion.replace(".0", "");
        participantPropertiesDraftBOList =
            studyMetaDataDao.getParticipantPropertiesByStudyVersion(studyId, studyVer);
      } else {
        participantPropertiesBOList = studyMetaDataDao.getParticipantProperties(studyId);
      }

      if (null != participantPropertiesBOList && !participantPropertiesBOList.isEmpty()) {
        for (ParticipantPropertiesBO pbo : participantPropertiesBOList) {
          if (pbo.getDataType().equalsIgnoreCase("date")
              && pbo.getDataSource().equalsIgnoreCase("ExternalSystem")) {
            ParticipantPropertyMetaData participantPropertyMetaData =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData.setPropertyId(pbo.getShortTitle());
            participantPropertyMetaData.setPropertyName(pbo.getPropertyName());
            participantPropertyMetaData.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData.setStatus("active");
            } else {
              participantPropertyMetaData.setStatus("deactivated");
            }
            participantPropertyMetaData.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData);

            ParticipantPropertyMetaData participantPropertyMetaData1 =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData1.setPropertyId(pbo.getShortTitle() + "ExternalId");
            participantPropertyMetaData1.setPropertyName(pbo.getShortTitle() + " External Id");
            participantPropertyMetaData1.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData1.setPropertyDataType("string");
            participantPropertyMetaData1.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData1.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData1.setStatus("active");
            } else {
              participantPropertyMetaData1.setStatus("deactivated");
            }
            participantPropertyMetaData1.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData1);

            ParticipantPropertyMetaData participantPropertyMetaData2 =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData2.setPropertyId(pbo.getShortTitle() + "EntryDate");
            participantPropertyMetaData2.setPropertyName(pbo.getShortTitle() + " Entry Date");
            participantPropertyMetaData2.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData2.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData2.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData2.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData2.setStatus("active");
            } else {
              participantPropertyMetaData2.setStatus("deactivated");
            }
            participantPropertyMetaData2.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData2);
          } else {
            ParticipantPropertyMetaData participantPropertyMetaData =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData.setPropertyId(pbo.getShortTitle());
            participantPropertyMetaData.setPropertyName(pbo.getPropertyName());
            participantPropertyMetaData.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData.setStatus("active");
            } else {
              participantPropertyMetaData.setStatus("deactivated");
            }
            participantPropertyMetaData.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData);
          }
        }
        participantPropertiesResponseBean.setMessage("success");
      } else if (null != participantPropertiesDraftBOList
          && !participantPropertiesDraftBOList.isEmpty()) {
        for (ParticipantPropertiesDraftBO pbo : participantPropertiesDraftBOList) {
          if (pbo.getDataType().equalsIgnoreCase("date")
              && pbo.getDataSource().equalsIgnoreCase("ExternalSystem")) {
            ParticipantPropertyMetaData participantPropertyMetaData =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData.setPropertyId(pbo.getShortTitle());
            participantPropertyMetaData.setPropertyName(pbo.getPropertyName());
            participantPropertyMetaData.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData.setStatus("active");
            } else {
              participantPropertyMetaData.setStatus("deactivated");
            }
            participantPropertyMetaData.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData);

            ParticipantPropertyMetaData participantPropertyMetaData1 =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData1.setPropertyId(pbo.getShortTitle() + "ExternalId");
            participantPropertyMetaData1.setPropertyName(pbo.getShortTitle() + " External Id");
            participantPropertyMetaData1.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData1.setPropertyDataType("string");
            participantPropertyMetaData1.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData1.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData1.setStatus("active");
            } else {
              participantPropertyMetaData1.setStatus("deactivated");
            }
            participantPropertyMetaData1.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData1);

            ParticipantPropertyMetaData participantPropertyMetaData2 =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData2.setPropertyId(pbo.getShortTitle() + "EntryDate");
            participantPropertyMetaData2.setPropertyName(pbo.getShortTitle() + " Entry Date");
            participantPropertyMetaData2.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData2.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData2.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData2.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData2.setStatus("active");
            } else {
              participantPropertyMetaData2.setStatus("deactivated");
            }
            participantPropertyMetaData2.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData2);
          } else {
            ParticipantPropertyMetaData participantPropertyMetaData =
                new ParticipantPropertyMetaData();
            participantPropertyMetaData.setPropertyId(pbo.getShortTitle());
            participantPropertyMetaData.setPropertyName(pbo.getPropertyName());
            participantPropertyMetaData.setPropertyType(pbo.getPropertyType());
            participantPropertyMetaData.setPropertyDataType(pbo.getDataType());
            participantPropertyMetaData.setShouldRefresh(pbo.getRefreshedValue());
            participantPropertyMetaData.setDataSource(pbo.getDataSource());
            if (pbo.getStatus()) {
              participantPropertyMetaData.setStatus("active");
            } else {
              participantPropertyMetaData.setStatus("deactivated");
            }
            participantPropertyMetaData.setVersion(pbo.getVersion().toString());
            participantPropertyMetadataList.add(participantPropertyMetaData);
          }
        }
        participantPropertiesResponseBean.setMessage("success");
      }

      participantPropertiesMetadata.setStudyId(studyId);
      if (StringUtils.isNotEmpty(studyVersion)) {
        participantPropertiesMetadata.setStudyVersion(Float.valueOf(studyVersion).toString());
      } else {
        Float latestStudyVersion = studyMetaDataDao.getStudylatestVersion(studyId);
        participantPropertiesMetadata.setStudyVersion(
            null != latestStudyVersion ? latestStudyVersion.toString() : "");
      }
      participantPropertiesResponseBean.setMetadata(participantPropertiesMetadata);
      participantPropertiesResponseBean.setParticipantProperties(participantPropertyMetadataList);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - getParticipantProperties() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - getParticipantProperties() :: Ends");
    return participantPropertiesResponseBean;
  }
}
