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
package com.hphc.mystudies.dao;

import com.hphc.mystudies.bean.AnchorDateBean;
import com.hphc.mystudies.bean.ComprehensionDetailsBean;
import com.hphc.mystudies.bean.ConsentBean;
import com.hphc.mystudies.bean.ConsentDetailsBean;
import com.hphc.mystudies.bean.ConsentDocumentBean;
import com.hphc.mystudies.bean.ConsentDocumentResponse;
import com.hphc.mystudies.bean.CorrectAnswersBean;
import com.hphc.mystudies.bean.EligibilityBean;
import com.hphc.mystudies.bean.EligibilityConsentResponse;
import com.hphc.mystudies.bean.GatewayInfoResourceBean;
import com.hphc.mystudies.bean.GatewayInfoResponse;
import com.hphc.mystudies.bean.InfoBean;
import com.hphc.mystudies.bean.QuestionInfoBean;
import com.hphc.mystudies.bean.QuestionnaireActivityStepsBean;
import com.hphc.mystudies.bean.ResourcesBean;
import com.hphc.mystudies.bean.ResourcesResponse;
import com.hphc.mystudies.bean.ReviewBean;
import com.hphc.mystudies.bean.SettingsBean;
import com.hphc.mystudies.bean.SharingBean;
import com.hphc.mystudies.bean.StudyBean;
import com.hphc.mystudies.bean.StudyInfoResponse;
import com.hphc.mystudies.bean.StudyResponse;
import com.hphc.mystudies.bean.WithdrawalConfigBean;
import com.hphc.mystudies.dto.ActiveTaskDto;
import com.hphc.mystudies.dto.AnchorDateTypeDto;
import com.hphc.mystudies.dto.ComprehensionQuestionLangBO;
import com.hphc.mystudies.dto.ComprehensionResponseLangBo;
import com.hphc.mystudies.dto.ComprehensionTestQuestionDto;
import com.hphc.mystudies.dto.ComprehensionTestResponseDto;
import com.hphc.mystudies.dto.ConsentDto;
import com.hphc.mystudies.dto.ConsentInfoDto;
import com.hphc.mystudies.dto.ConsentInfoLangBO;
import com.hphc.mystudies.dto.ConsentMasterInfoDto;
import com.hphc.mystudies.dto.EligibilityDto;
import com.hphc.mystudies.dto.EligibilityTestDto;
import com.hphc.mystudies.dto.EligibilityTestLangBo;
import com.hphc.mystudies.dto.EnrollmentTokenDto;
import com.hphc.mystudies.dto.FormMappingDto;
import com.hphc.mystudies.dto.GatewayInfoDto;
import com.hphc.mystudies.dto.GatewayWelcomeInfoDto;
import com.hphc.mystudies.dto.ParticipantPropertiesBO;
import com.hphc.mystudies.dto.ParticipantPropertiesDraftBO;
import com.hphc.mystudies.dto.QuestionnairesDto;
import com.hphc.mystudies.dto.QuestionnairesStepsDto;
import com.hphc.mystudies.dto.QuestionsDto;
import com.hphc.mystudies.dto.ReferenceTablesDto;
import com.hphc.mystudies.dto.ResourcesDto;
import com.hphc.mystudies.dto.ResourcesLangBO;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyLanguageBO;
import com.hphc.mystudies.dto.StudyPageDto;
import com.hphc.mystudies.dto.StudyPageLanguageBO;
import com.hphc.mystudies.dto.StudySequenceDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.MultiLanguageCodes;
import com.hphc.mystudies.util.MultiLanguageConstants;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Provides study metadata business logic and model objects details.
 *
 * @author BTC
 */
public class StudyMetaDataDao {

  private static final Logger LOGGER = Logger.getLogger(StudyMetaDataDao.class);

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;
  String queryString = "";

  /**
   * Check Authorization for the provided authorization identifier
   *
   * @param authorization the Basic Authorization
   * @return {@link Boolean}
   * @throws DAOException
   * @author BTC
   */
  public boolean isValidAuthorizationId(String authorization) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
    boolean hasValidAuthorization = false;
    String bundleIdAndAppToken = null;
    try {
      byte[] decodedBytes = Base64.getDecoder().decode(authorization);
      bundleIdAndAppToken = new String(decodedBytes, StudyMetaDataConstants.TYPE_UTF8);
      final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
      final String bundleId = tokenizer.nextToken();
      final String appToken = tokenizer.nextToken();
      if (authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)) {
        hasValidAuthorization = true;
      }
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
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public GatewayInfoResponse gatewayAppResourcesInfo(String authorization) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - gatewayAppResourcesInfo() :: Starts");
    Session session = null;
    GatewayInfoResponse gatewayInfoResponse = new GatewayInfoResponse();
    GatewayInfoDto gatewayInfo = null;
    List<GatewayWelcomeInfoDto> gatewayWelcomeInfoList = null;
    List<ResourcesDto> resourcesList = null;
    String platformType = "";
    try {
      session = sessionFactory.openSession();
      gatewayInfo = (GatewayInfoDto) session.createQuery("from GatewayInfoDto GWID").uniqueResult();
      if (null != gatewayInfo) {

        gatewayWelcomeInfoList = session.createQuery("from GatewayWelcomeInfoDto GWWID").list();
        if (null != gatewayWelcomeInfoList && !gatewayWelcomeInfoList.isEmpty()) {
          List<InfoBean> infoBeanList = new ArrayList<>();
          for (GatewayWelcomeInfoDto gatewayWelcomeInfo : gatewayWelcomeInfoList) {

            InfoBean infoBean = new InfoBean();
            infoBean.setTitle(
                StringUtils.isEmpty(gatewayWelcomeInfo.getAppTitle())
                    ? ""
                    : gatewayWelcomeInfo.getAppTitle());
            infoBean.setImage(
                StringUtils.isEmpty(gatewayWelcomeInfo.getImagePath())
                    ? ""
                    : propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                        + gatewayWelcomeInfo.getImagePath()
                        + StudyMetaDataUtil.getMilliSecondsForImagePath());
            infoBean.setText(
                StringUtils.isEmpty(gatewayWelcomeInfo.getDescription())
                    ? ""
                    : gatewayWelcomeInfo.getDescription());
            if (infoBeanList.isEmpty()) {
              infoBean.setType(StudyMetaDataConstants.TYPE_VIDEO);
              infoBean.setVideoLink(
                  StringUtils.isEmpty(gatewayInfo.getVideoUrl()) ? "" : gatewayInfo.getVideoUrl());
            } else {
              infoBean.setType(StudyMetaDataConstants.TYPE_TEXT);
            }
            infoBeanList.add(infoBean);
          }
          gatewayInfoResponse.setInfo(infoBeanList);
        }
      }

      /*
       * Get the platform from the provided authorization credentials and fetch based
       * on the platform
       */
      platformType =
          StudyMetaDataUtil.platformType(
              authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
      if (StringUtils.isNotEmpty(platformType)) {
        resourcesList =
            session
                .createQuery(
                    "from ResourcesDto RDTO where RDTO.studyId in ( select SDTO.id"
                        + " from StudyDto SDTO where SDTO.platform like :platformType and SDTO.type= :type and SDTO.live=1) ORDER BY RDTO.sequenceNo")
                .setString(StudyMetaDataEnum.QF_TYPE.value(), StudyMetaDataConstants.STUDY_TYPE_GT)
                .setString("platformType", "%" + platformType + "%")
                .list();
        if (null != resourcesList && !resourcesList.isEmpty()) {
          List<GatewayInfoResourceBean> resourceBeanList = new ArrayList<>();
          for (ResourcesDto resource : resourcesList) {

            GatewayInfoResourceBean resourceBean = new GatewayInfoResourceBean();
            resourceBean.setTitle(
                StringUtils.isEmpty(resource.getTitle()) ? "" : resource.getTitle());
            if (!resource.isTextOrPdf()) {
              resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
              resourceBean.setContent(
                  StringUtils.isEmpty(resource.getRichText()) ? "" : resource.getRichText());
            } else {
              resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
              resourceBean.setContent(
                  StringUtils.isEmpty(resource.getPdfUrl())
                      ? ""
                      : propMap.get(StudyMetaDataConstants.FDA_SMD_RESOURCE_PDF_PATH)
                          + resource.getPdfUrl());
            }

            resourceBean.setResourcesId(
                resource.getId() == null ? "" : String.valueOf(resource.getId()));
            resourceBeanList.add(resourceBean);
          }
          gatewayInfoResponse.setResources(resourceBeanList);
        }
      }

      gatewayInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - gatewayAppResourcesInfo() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - gatewayAppResourcesInfo() :: Ends");
    return gatewayInfoResponse;
  }

  /**
   * Get all the configured studies from the WCP
   *
   * @param authorization the Basic Authorization
   * @return {@link StudyResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public StudyResponse studyList(
      String authorization, String applicationId, String orgId, String language)
      throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - studyList() :: Starts");
    Session session = null;
    StudyResponse studyResponse = new StudyResponse();
    List<StudyDto> studiesList = null;
    String platformType = "";
    try {
      platformType =
          StudyMetaDataUtil.platformType(
              authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
      if (StringUtils.isNotEmpty(platformType)) {
        session = sessionFactory.openSession();

        // Get all configured studies from the WCP by platform supported
        if (StringUtils.isNotBlank(language)
            && !MultiLanguageCodes.ENGLISH.getKey().equals(language)) {
          studiesList =
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.platform like :platformType"
                          + " and SDTO.appId=:applicationId and SDTO.orgId=:orgId"
                          + " and (SDTO.status= :status OR SDTO.live=1) and SDTO.multiLanguageFlag=:multiLanguageFlag "
                          + "and SDTO.selectedLanguages like :language")
                  .setString("platformType", "%" + platformType + "%")
                  .setString("applicationId", applicationId)
                  .setString("orgId", orgId)
                  .setBoolean("multiLanguageFlag", true)
                  .setString("language", "%" + language + "%")
                  .setString(
                      StudyMetaDataEnum.QF_STATUS.value(),
                      StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)
                  .list();
        } else {
          studiesList =
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.platform like :platformType"
                          + " and SDTO.appId=:applicationId and SDTO.orgId=:orgId"
                          + " and (SDTO.status= :status OR SDTO.live=1)")
                  .setString("platformType", "%" + platformType + "%")
                  .setString("applicationId", applicationId)
                  .setString("orgId", orgId)
                  .setString(
                      StudyMetaDataEnum.QF_STATUS.value(),
                      StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)
                  .list();
        }
        if (null != studiesList && !studiesList.isEmpty()) {
          List<StudyBean> studyBeanList = new ArrayList<>();
          for (StudyDto studyDto : studiesList) {
            StudyLanguageBO studyLanguageBO = null;
            StudyBean studyBean = new StudyBean();
            if (StringUtils.isNotBlank(language)
                && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                && studyDto.getMultiLanguageFlag() != null
                && studyDto.getMultiLanguageFlag()
                && studyDto.getSelectedLanguages().contains(language)) {
              studyLanguageBO =
                  (StudyLanguageBO)
                      session
                          .createQuery(
                              "from StudyLanguageBO SLBO where SLBO.studyLanguagePK.study_id= :studyId AND SLBO.studyLanguagePK.langCode= :langCode")
                          .setString("studyId", studyDto.getId().toString())
                          .setString("langCode", language)
                          .uniqueResult();
              studyBean.setTagline(
                  StringUtils.isEmpty(studyLanguageBO.getStudyTagline())
                      ? ""
                      : studyLanguageBO.getStudyTagline());
              studyBean.setTitle(
                  StringUtils.isEmpty(studyLanguageBO.getName()) ? "" : studyLanguageBO.getName());
              studyBean.setSponsorName(
                  StringUtils.isEmpty(studyLanguageBO.getResearchSponsor())
                      ? ""
                      : studyLanguageBO.getResearchSponsor());
            } else {
              studyBean.setTagline(
                  StringUtils.isEmpty(studyDto.getStudyTagline())
                      ? ""
                      : studyDto.getStudyTagline());
              studyBean.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
              studyBean.setSponsorName(
                  StringUtils.isEmpty(studyDto.getResearchSponsor())
                      ? ""
                      : studyDto.getResearchSponsor());
            }
            if (StringUtils.isNotEmpty(studyDto.getCategory())
                && StringUtils.isNotEmpty(studyDto.getResearchSponsor())) {
              List<ReferenceTablesDto> referenceTablesList =
                  session
                      .createQuery(
                          "from ReferenceTablesDto RTDTO"
                              + " where RTDTO.id IN ("
                              + studyDto.getCategory()
                              + ")")
                      .list();
              if (null != referenceTablesList && !referenceTablesList.isEmpty()) {
                for (ReferenceTablesDto reference : referenceTablesList) {
                  if (reference
                      .getCategory()
                      .equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)) {
                    if (StringUtils.isNotBlank(language)
                        && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)) {
                      studyBean.setCategory(
                          StudyMetaDataUtil.getMultiLanguageText(
                              language, "id_" + studyDto.getCategory()));
                    } else {
                      studyBean.setCategory(
                          StringUtils.isEmpty(reference.getValue()) ? "" : reference.getValue());
                    }
                  }
                }
              }
            }
            studyBean.setStudyVersion(
                studyDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : studyDto.getVersion().toString());
            switch (studyDto.getStatus()) {
              case StudyMetaDataConstants.STUDY_STATUS_ACTIVE:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PAUSED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
                break;
              default:
                break;
            }
            studyBean.setLogo(
                StringUtils.isEmpty(studyDto.getThumbnailImage())
                    ? ""
                    : propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                        + studyDto.getThumbnailImage()
                        + StudyMetaDataUtil.getMilliSecondsForImagePath());
            studyBean.setStudyId(
                StringUtils.isEmpty(studyDto.getCustomStudyId())
                    ? ""
                    : studyDto.getCustomStudyId());
            if (StringUtils.isNotBlank(language)
                && !MultiLanguageConstants.ENGLISH.equals(language)) {
              studyBean.setStudyLanguage(MultiLanguageCodes.getValue(language));
            } else {
              studyBean.setStudyLanguage("English");
            }
            SettingsBean settings = new SettingsBean();
            if (studyDto.getPlatform().contains(",")) {
              settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ALL);
            } else {
              switch (studyDto.getPlatform()) {
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_IOS);
                  break;
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID);
                  break;
                default:
                  break;
              }
            }
            if (StringUtils.isNotEmpty(studyDto.getAllowRejoin())
                && studyDto.getAllowRejoin().equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setRejoin(true);
            } else {
              settings.setRejoin(false);
            }
            if (StringUtils.isNotEmpty(studyDto.getEnrollingParticipants())
                && studyDto
                    .getEnrollingParticipants()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setEnrolling(true);
            } else {
              settings.setEnrolling(false);
            }
            studyBean.setSettings(settings);
            studyBeanList.add(studyBean);
          }
          studyResponse.setStudies(studyBeanList);
        }
        studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - studyList() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - studyList() :: Ends");
    return studyResponse;
  }

  /**
   * Get eligibility, consent and comprehension info for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link EligibilityConsentResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public EligibilityConsentResponse eligibilityConsentMetadata(String studyId, String language)
      throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - eligibilityConsentMetadata() :: Starts");
    Session session = null;
    EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
    EligibilityDto eligibilityDto = null;
    ConsentDto consentDto = null;
    List<ConsentInfoDto> consentInfoDtoList = null;
    List<ComprehensionTestQuestionDto> comprehensionQuestionList = null;
    List<ConsentMasterInfoDto> consentMasterInfoList = null;
    ConsentDetailsBean consent = new ConsentDetailsBean();
    StudySequenceDto studySequenceDto = null;
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    List<EligibilityTestDto> eligibilityTestList = null;
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId =:customStudyId and SDTO.live=1")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {
        studyVersionDto =
            (StudyVersionDto)
                session
                    .createQuery(
                        "from StudyVersionDto SVDTO"
                            + " where SVDTO.customStudyId =:customStudyId and ROUND(SVDTO.studyVersion, 1)=:studyVersion"
                            + " ORDER BY SVDTO.versionId DESC")
                    .setString(
                        StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyDto.getCustomStudyId())
                    .setFloat(StudyMetaDataEnum.QF_STUDY_VERSION.value(), studyDto.getVersion())
                    .setMaxResults(1)
                    .uniqueResult();

        studySequenceDto =
            (StudySequenceDto)
                session
                    .createQuery("from StudySequenceDto SSDTO" + " where SSDTO.studyId =:studyId ")
                    .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                    .uniqueResult();
        if (studySequenceDto != null) {

          StudyLanguageBO studyLanguage = null;
          if (StringUtils.isNotBlank(language)
              && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
              && studyDto.getMultiLanguageFlag() != null
              && studyDto.getMultiLanguageFlag()
              && studyDto.getSelectedLanguages().contains(language)) {
            studyLanguage =
                (StudyLanguageBO)
                    session
                        .createQuery(
                            "from StudyLanguageBO SLBO where SLBO.studyLanguagePK.study_id= :studyId AND SLBO.studyLanguagePK.langCode= :langCode")
                        .setString("studyId", studyDto.getId().toString())
                        .setString("langCode", language)
                        .uniqueResult();
          }

          if (studySequenceDto
              .getEligibility()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)) {

            eligibilityDto =
                (EligibilityDto)
                    session
                        .createQuery("from EligibilityDto EDTO" + " where EDTO.studyId =:studyId ")
                        .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                        .uniqueResult();

            if (eligibilityDto != null) {
              EligibilityBean eligibility = new EligibilityBean();
              if (null != eligibilityDto.getEligibilityMechanism()) {
                switch (eligibilityDto.getEligibilityMechanism()) {
                  case 1:
                    eligibility.setType(StudyMetaDataConstants.TYPE_TOKEN);
                    break;
                  case 2:
                    eligibility.setType(StudyMetaDataConstants.TYPE_BOTH);
                    break;
                  case 3:
                    eligibility.setType(StudyMetaDataConstants.TYPE_TEST);
                    break;
                  default:
                    eligibility.setType("");
                    break;
                }
              }

              if (studyLanguage != null) {
                eligibility.setTokenTitle(
                    StringUtils.isEmpty(studyLanguage.getInstructionalText())
                        ? ""
                        : studyLanguage.getInstructionalText());
              } else {
                eligibility.setTokenTitle(
                    StringUtils.isEmpty(eligibilityDto.getInstructionalText())
                        ? ""
                        : eligibilityDto.getInstructionalText());
              }

              eligibilityTestList =
                  session
                      .createQuery(
                          "from EligibilityTestDto ETDTO"
                              + " where ETDTO.eligibilityId=:id and ETDTO.status=true "
                              + "and ETDTO.active=true ORDER BY ETDTO.sequenceNo")
                      .setInteger("id", eligibilityDto.getId())
                      .list();
              if (eligibilityTestList != null && !eligibilityTestList.isEmpty()) {
                List<QuestionnaireActivityStepsBean> test = new ArrayList<>();

                List<HashMap<String, Object>> correctAnswers = new ArrayList<>();
                for (EligibilityTestDto eligibilityTest : eligibilityTestList) {

                  EligibilityTestLangBo eligibilityTestLang = null;
                  if (StringUtils.isNotBlank(language)
                      && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                      && studyDto.getMultiLanguageFlag() != null
                      && studyDto.getMultiLanguageFlag()
                      && studyDto.getSelectedLanguages().contains(language)) {

                    eligibilityTestLang =
                        (EligibilityTestLangBo)
                            session
                                .createQuery(
                                    "from EligibilityTestLangBo where eligibilityTestLangPK.id=:eligibilityTestId AND eligibilityTestLangPK.langCode= :langCode")
                                .setString("eligibilityTestId", eligibilityTest.getId().toString())
                                .setString("langCode", language)
                                .uniqueResult();
                  }

                  QuestionnaireActivityStepsBean questionStep =
                      new QuestionnaireActivityStepsBean();
                  questionStep.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                  questionStep.setResultType(StudyMetaDataConstants.QUESTION_BOOLEAN);
                  questionStep.setKey(eligibilityTest.getShortTitle());

                  if (eligibilityTestLang != null
                      && StringUtils.isNotBlank(eligibilityTestLang.getQuestion())) {

                    questionStep.setTitle(eligibilityTestLang.getQuestion());
                  } else {
                    questionStep.setTitle(eligibilityTest.getQuestion());
                  }

                  questionStep.setText(
                      StudyMetaDataUtil.getMultiLanguageText(
                          language, MultiLanguageConstants.ELIGIBILITY_TEXT));
                  questionStep.setSkippable(false);
                  questionStep.setGroupName("");
                  questionStep.setRepeatable(false);
                  questionStep.setRepeatableText("");
                  questionStep.setHealthDataKey("");
                  test.add(questionStep);

                  if (eligibilityTest.getResponseYesOption()) {
                    HashMap<String, Object> correctAnsHashMap = new HashMap<>();
                    correctAnsHashMap.put("key", eligibilityTest.getShortTitle());
                    correctAnsHashMap.put("answer", true);
                    correctAnswers.add(correctAnsHashMap);
                  }

                  if (eligibilityTest.getResponseNoOption()) {
                    HashMap<String, Object> correctAnsHashMap = new HashMap<>();
                    correctAnsHashMap.put("key", eligibilityTest.getShortTitle());
                    correctAnsHashMap.put("answer", false);
                    correctAnswers.add(correctAnsHashMap);
                  }
                }
                eligibility.setTest(test);
                eligibility.setCorrectAnswers(correctAnswers);
              }
              eligibilityConsentResponse.setEligibility(eligibility);
            }
          }

          consentDto =
              (ConsentDto)
                  session
                      .createQuery(
                          " from ConsentDto CDTO"
                              + " where CDTO.customStudyId =:customStudyId and ROUND(CDTO.version, 1)=:version AND CDTO.live=1")
                      .setString(
                          StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                          studyVersionDto.getCustomStudyId())
                      .setFloat(
                          StudyMetaDataEnum.QF_VERSION.value(), studyVersionDto.getConsentVersion())
                      .uniqueResult();
          if (null != consentDto) {
            consent.setVersion(
                consentDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : consentDto.getVersion().toString());

            SharingBean sharingBean = new SharingBean();

            // check whether share data permission is yes or no

            if (StringUtils.isNotEmpty(consentDto.getShareDataPermissions())
                && consentDto
                    .getShareDataPermissions()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              if (studyLanguage != null) {
                sharingBean.setTitle(
                    StringUtils.isEmpty(studyLanguage.geteConsentTitle())
                        ? ""
                        : studyLanguage.geteConsentTitle());
                sharingBean.setText(
                    StringUtils.isEmpty(studyLanguage.getTaglineDescription())
                        ? ""
                        : studyLanguage.getTaglineDescription());
                sharingBean.setLearnMore(
                    StringUtils.isEmpty(studyLanguage.getLearnMoreText())
                        ? ""
                        : studyLanguage.getLearnMoreText());
                sharingBean.setLongDesc(
                    StringUtils.isEmpty(studyLanguage.getLongDescription())
                        ? ""
                        : studyLanguage.getLongDescription());
                sharingBean.setShortDesc(
                    StringUtils.isEmpty(studyLanguage.getShortDescription())
                        ? ""
                        : studyLanguage.getShortDescription());
              } else {
                sharingBean.setTitle(
                    StringUtils.isEmpty(consentDto.getTitle()) ? "" : consentDto.getTitle());
                sharingBean.setText(
                    StringUtils.isEmpty(consentDto.getTaglineDescription())
                        ? ""
                        : consentDto.getTaglineDescription());
                sharingBean.setLearnMore(
                    StringUtils.isEmpty(consentDto.getLearnMoreText())
                        ? ""
                        : consentDto.getLearnMoreText());
                sharingBean.setLongDesc(
                    StringUtils.isEmpty(consentDto.getLongDescription())
                        ? ""
                        : consentDto.getLongDescription());
                sharingBean.setShortDesc(
                    StringUtils.isEmpty(consentDto.getShortDescription())
                        ? ""
                        : consentDto.getShortDescription());
              }
              if (consentDto.getAllowWithoutPermission() != null
                  && StudyMetaDataConstants.YES.equalsIgnoreCase(
                      consentDto.getAllowWithoutPermission())) {
                sharingBean.setAllowWithoutSharing(true);
              }
            }
            consent.setSharing(sharingBean);
          }

          consentMasterInfoList = session.createQuery("from ConsentMasterInfoDto CMIDTO").list();

          if (studySequenceDto
              .getConsentEduInfo()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)) {

            consentInfoDtoList =
                session
                    .createQuery(
                        "from ConsentInfoDto CIDTO"
                            + " where CIDTO.customStudyId =:customStudyId and ROUND(CIDTO.version, 1)=:version and CIDTO.live=1"
                            + " ORDER BY CIDTO.sequenceNo")
                    .setString(
                        StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                        studyVersionDto.getCustomStudyId())
                    .setFloat(
                        StudyMetaDataEnum.QF_VERSION.value(), studyVersionDto.getConsentVersion())
                    .list();
            if (null != consentInfoDtoList && !consentInfoDtoList.isEmpty()) {

              List<ConsentBean> consentBeanList = new ArrayList<>();
              for (ConsentInfoDto consentInfoDto : consentInfoDtoList) {

                ConsentInfoLangBO consentInfoLang = null;
                if (StringUtils.isNotBlank(language)
                    && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                    && studyDto.getMultiLanguageFlag() != null
                    && studyDto.getMultiLanguageFlag()
                    && studyDto.getSelectedLanguages().contains(language)) {

                  consentInfoLang =
                      (ConsentInfoLangBO)
                          session
                              .createQuery(
                                  "from ConsentInfoLangBO where consentInfoLangPK.id=:consentInfoId AND consentInfoLangPK.langCode= :langCode")
                              .setString("consentInfoId", consentInfoDto.getId().toString())
                              .setString("langCode", language)
                              .uniqueResult();
                }

                ConsentBean consentBean = new ConsentBean();
                if (consentInfoLang != null) {
                  consentBean.setText(
                      StringUtils.isEmpty(consentInfoLang.getBriefSummary())
                          ? ""
                          : consentInfoLang
                              .getBriefSummary()
                              .replaceAll("&#34;", "\"")
                              .replaceAll("&#39;", "'"));
                  consentBean.setTitle(
                      StringUtils.isEmpty(consentInfoLang.getDisplayTitle())
                          ? ""
                          : consentInfoLang
                              .getDisplayTitle()
                              .replaceAll("&#34;", "\"")
                              .replaceAll("&#39;", "'"));
                  consentBean.setHtml(
                      StringUtils.isEmpty(consentInfoLang.getElaborated())
                          ? ""
                          : consentInfoLang
                              .getElaborated()
                              .replaceAll("&#34;", "'")
                              .replaceAll("em>", "i>")
                              .replaceAll(
                                  "<a", "<a style='text-decoration:underline;color:blue;'"));
                } else {
                  consentBean.setText(
                      StringUtils.isEmpty(consentInfoDto.getBriefSummary())
                          ? ""
                          : consentInfoDto
                              .getBriefSummary()
                              .replaceAll("&#34;", "\"")
                              .replaceAll("&#39;", "'"));
                  consentBean.setTitle(
                      StringUtils.isEmpty(consentInfoDto.getDisplayTitle())
                          ? ""
                          : consentInfoDto
                              .getDisplayTitle()
                              .replaceAll("&#34;", "\"")
                              .replaceAll("&#39;", "'"));
                  consentBean.setHtml(
                      StringUtils.isEmpty(consentInfoDto.getElaborated())
                          ? ""
                          : consentInfoDto
                              .getElaborated()
                              .replaceAll("&#34;", "'")
                              .replaceAll("em>", "i>")
                              .replaceAll(
                                  "<a", "<a style='text-decoration:underline;color:blue;'"));
                }

                if (consentInfoDto.getConsentItemTitleId() != null) {
                  if (consentMasterInfoList != null && !consentMasterInfoList.isEmpty()) {
                    for (ConsentMasterInfoDto masterInfo : consentMasterInfoList) {
                      if (masterInfo.getId().intValue()
                          == consentInfoDto.getConsentItemTitleId().intValue()) {
                        consentBean.setType(masterInfo.getCode());
                        break;
                      }
                    }
                  }
                } else {
                  consentBean.setType(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM.toLowerCase());
                }
                consentBean.setDescription("");
                consentBean.setUrl(
                    StringUtils.isEmpty(consentInfoDto.getUrl()) ? "" : consentInfoDto.getUrl());

                if (StringUtils.isNotEmpty(consentInfoDto.getVisualStep())
                    && consentInfoDto
                        .getVisualStep()
                        .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                  consentBean.setVisualStep(true);
                } else {
                  consentBean.setVisualStep(false);
                }
                consentBeanList.add(consentBean);
              }
              consent.setVisualScreens(consentBeanList);
            }
          }

          if (studySequenceDto
                  .getComprehensionTest()
                  .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)
              && (consentDto != null
                  && consentDto.getNeedComprehensionTest() != null
                  && consentDto
                      .getNeedComprehensionTest()
                      .equalsIgnoreCase(StudyMetaDataConstants.YES))) {

            comprehensionQuestionList =
                session
                    .createQuery(
                        " from ComprehensionTestQuestionDto CTDTO"
                            + " where CTDTO.studyId =:studyId and CTDTO.status=true and CTDTO.active=true"
                            + " ORDER BY CTDTO.sequenceNo")
                    .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                    .list();
            if (null != comprehensionQuestionList && !comprehensionQuestionList.isEmpty()) {
              ComprehensionDetailsBean comprehensionDetailsBean = new ComprehensionDetailsBean();
              if (consentDto != null && consentDto.getComprehensionTestMinimumScore() != null) {
                comprehensionDetailsBean.setPassScore(
                    consentDto.getComprehensionTestMinimumScore());
              } else {
                comprehensionDetailsBean.setPassScore(0);
              }

              List<QuestionnaireActivityStepsBean> comprehensionList = new ArrayList<>();
              List<CorrectAnswersBean> correctAnswerBeanList = new ArrayList<>();
              for (ComprehensionTestQuestionDto comprehensionQuestionDto :
                  comprehensionQuestionList) {

                ComprehensionQuestionLangBO comprehensionQuestionLang = null;
                if (StringUtils.isNotBlank(language)
                    && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                    && studyDto.getMultiLanguageFlag() != null
                    && studyDto.getMultiLanguageFlag()
                    && studyDto.getSelectedLanguages().contains(language)) {
                  comprehensionQuestionLang =
                      (ComprehensionQuestionLangBO)
                          session
                              .createQuery(
                                  "from ComprehensionQuestionLangBO where comprehensionQuestionLangPK.id= :comprehensionQuestionId AND comprehensionQuestionLangPK.langCode= :langCode")
                              .setString(
                                  "comprehensionQuestionId",
                                  comprehensionQuestionDto.getId().toString())
                              .setString("langCode", language)
                              .uniqueResult();
                }

                QuestionnaireActivityStepsBean questionStep = new QuestionnaireActivityStepsBean();
                questionStep.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                questionStep.setResultType(StudyMetaDataConstants.QUESTION_TEXT_CHOICE);
                questionStep.setKey(comprehensionQuestionDto.getId().toString());
                if (comprehensionQuestionLang != null) {
                  questionStep.setTitle(comprehensionQuestionLang.getQuestionText());
                } else {
                  questionStep.setTitle(comprehensionQuestionDto.getQuestionText());
                }
                questionStep.setText(
                    StudyMetaDataUtil.getMultiLanguageText(
                        language, MultiLanguageConstants.COMPREHENSION_TEXT));
                questionStep.setSkippable(false);
                questionStep.setGroupName("");
                questionStep.setRepeatable(false);
                questionStep.setRepeatableText("");
                questionStep.setHealthDataKey("");

                List<ComprehensionTestResponseDto> comprehensionTestResponseList =
                    session
                        .createQuery(
                            " from ComprehensionTestResponseDto CTRDTO"
                                + " where CTRDTO.comprehensionTestQuestionId =:comprehensionTestQuestionId")
                        .setInteger("comprehensionTestQuestionId", comprehensionQuestionDto.getId())
                        .list();
                if (comprehensionTestResponseList != null
                    && !comprehensionTestResponseList.isEmpty()) {

                  CorrectAnswersBean correctAnswerBean = new CorrectAnswersBean();
                  Map<String, Object> questionFormat = new LinkedHashMap<>();
                  List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
                  StringBuilder sb = new StringBuilder();

                  for (ComprehensionTestResponseDto compResp : comprehensionTestResponseList) {

                    ComprehensionResponseLangBo comprehensionResponseLang = null;
                    if (StringUtils.isNotBlank(language)
                        && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                        && studyDto.getMultiLanguageFlag() != null
                        && studyDto.getMultiLanguageFlag()
                        && studyDto.getSelectedLanguages().contains(language)) {
                      comprehensionResponseLang =
                          (ComprehensionResponseLangBo)
                              session
                                  .createQuery(
                                      "from ComprehensionResponseLangBo where comprehensionResponseLangPK.id= :comprehensionResponseLangId AND comprehensionResponseLangPK.langCode= :langCode")
                                  .setString(
                                      "comprehensionResponseLangId", compResp.getId().toString())
                                  .setString("langCode", language)
                                  .uniqueResult();
                    }

                    if (compResp.getCorrectAnswer()) {
                      if (comprehensionResponseLang != null) {
                        sb.append(
                            StringUtils.isEmpty(sb)
                                ? comprehensionResponseLang.getResponseOption().trim()
                                : "&@##@&" + comprehensionResponseLang.getResponseOption().trim());
                      } else {
                        sb.append(
                            StringUtils.isEmpty(sb)
                                ? compResp.getResponseOption().trim()
                                : "&@##@&" + compResp.getResponseOption().trim());
                      }
                    }
                    LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
                    if (comprehensionResponseLang != null) {
                      textChoiceMap.put(
                          "text",
                          StringUtils.isEmpty(comprehensionResponseLang.getResponseOption().trim())
                              ? ""
                              : comprehensionResponseLang.getResponseOption().trim());
                      textChoiceMap.put(
                          "value",
                          StringUtils.isEmpty(comprehensionResponseLang.getResponseOption().trim())
                              ? ""
                              : comprehensionResponseLang.getResponseOption().trim());
                    } else {
                      textChoiceMap.put(
                          "text",
                          StringUtils.isEmpty(compResp.getResponseOption().trim())
                              ? ""
                              : compResp.getResponseOption().trim());
                      textChoiceMap.put(
                          "value",
                          StringUtils.isEmpty(compResp.getResponseOption().trim())
                              ? ""
                              : compResp.getResponseOption().trim());
                    }
                    textChoiceMap.put("detail", "");
                    textChoiceMap.put("exclusive", false);
                    textChoiceMapList.add(textChoiceMap);
                  }

                  questionFormat.put("textChoices", textChoiceMapList);

                  if (comprehensionQuestionDto.getStructureOfCorrectAns()) {
                    questionFormat.put("selectionStyle", "Multiple");
                  } else {
                    questionFormat.put("selectionStyle", "Single");
                  }

                  questionStep.setFormat(questionFormat);
                  if (StringUtils.isNotEmpty(sb.toString())) {
                    correctAnswerBean.setAnswer(sb.toString().split("&@##@&"));
                  }

                  correctAnswerBean.setKey(comprehensionQuestionDto.getId().toString());
                  correctAnswerBean.setEvaluation(
                      comprehensionQuestionDto.getStructureOfCorrectAns()
                          ? StudyMetaDataConstants.COMPREHENSION_RESPONSE_STRUCTURE_ALL
                          : StudyMetaDataConstants.COMPREHENSION_RESPONSE_STRUCTURE_ANY);
                  correctAnswerBeanList.add(correctAnswerBean);
                }

                comprehensionList.add(questionStep);
              }
              comprehensionDetailsBean.setQuestions(comprehensionList);
              comprehensionDetailsBean.setCorrectAnswers(correctAnswerBeanList);
              consent.setComprehension(comprehensionDetailsBean);
            }
          }

          if (consentDto != null) {
            ReviewBean reviewBean = new ReviewBean();
            if (consentDto
                .getConsentDocType()
                .equals(StudyMetaDataConstants.CONSENT_DOC_TYPE_NEW)) {
              if (studyLanguage != null) {
                reviewBean.setReviewHTML(
                    StringUtils.isEmpty(studyLanguage.getConsentDocContent())
                        ? ""
                        : studyLanguage
                            .getConsentDocContent()
                            .replaceAll("&#34;", "'")
                            .replaceAll("em>", "i>")
                            .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
              } else {
                reviewBean.setReviewHTML(
                    StringUtils.isEmpty(consentDto.getConsentDocContent())
                        ? ""
                        : consentDto
                            .getConsentDocContent()
                            .replaceAll("&#34;", "'")
                            .replaceAll("em>", "i>")
                            .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
              }
            }

            if (studyLanguage != null) {
              reviewBean.setReasonForConsent(
                  StringUtils.isNotEmpty(studyLanguage.getAgreementOfConsent())
                      ? studyLanguage.getAgreementOfConsent()
                      : StudyMetaDataConstants.REASON_FOR_CONSENT);
            } else {
              reviewBean.setReasonForConsent(
                  StringUtils.isNotEmpty(consentDto.getAggrementOfConsent())
                      ? consentDto.getAggrementOfConsent()
                      : StudyMetaDataConstants.REASON_FOR_CONSENT);
            }

            reviewBean.setConsentByLAR(
                StringUtils.isNotEmpty(consentDto.getConsentByLAR())
                    ? consentDto.getConsentByLAR()
                    : "No");
            reviewBean.setAdditionalSignature(
                StringUtils.isNotBlank(consentDto.getAdditionalSignature())
                    ? consentDto.getAdditionalSignature()
                    : "No");
            if (StringUtils.isNotBlank(consentDto.getAdditionalSignature())
                && StringUtils.equals(consentDto.getAdditionalSignature(), "Yes")) {
              if (studyLanguage != null) {
                if (StringUtils.isNotBlank(studyLanguage.getSignatureOne())
                    && StringUtils.isNotBlank(studyLanguage.getSignatureTwo())
                    && StringUtils.isNotBlank(studyLanguage.getSignatureThree())) {
                  String[] signatures = new String[3];
                  signatures[0] = studyLanguage.getSignatureOne();
                  signatures[1] = studyLanguage.getSignatureTwo();
                  signatures[2] = studyLanguage.getSignatureThree();
                  reviewBean.setSignatures(signatures);
                } else if (StringUtils.isNotBlank(studyLanguage.getSignatureOne())
                    && StringUtils.isNotBlank(studyLanguage.getSignatureTwo())) {
                  String[] signatures = new String[2];
                  signatures[0] = studyLanguage.getSignatureOne();
                  signatures[1] = studyLanguage.getSignatureTwo();
                  reviewBean.setSignatures(signatures);
                } else if (StringUtils.isNotBlank(studyLanguage.getSignatureOne())) {
                  String[] signatures = new String[1];
                  signatures[0] = studyLanguage.getSignatureOne();
                  reviewBean.setSignatures(signatures);
                }
              } else {
                if (StringUtils.isNotBlank(consentDto.getSignatureOne())
                    && StringUtils.isNotBlank(consentDto.getSignatureTwo())
                    && StringUtils.isNotBlank(consentDto.getSignatureThree())) {
                  String[] signatures = new String[3];
                  signatures[0] = consentDto.getSignatureOne();
                  signatures[1] = consentDto.getSignatureTwo();
                  signatures[2] = consentDto.getSignatureThree();
                  reviewBean.setSignatures(signatures);
                } else if (StringUtils.isNotBlank(consentDto.getSignatureOne())
                    && StringUtils.isNotBlank(consentDto.getSignatureTwo())) {
                  String[] signatures = new String[2];
                  signatures[0] = consentDto.getSignatureOne();
                  signatures[1] = consentDto.getSignatureTwo();
                  reviewBean.setSignatures(signatures);
                } else if (StringUtils.isNotBlank(consentDto.getSignatureOne())) {
                  String[] signatures = new String[1];
                  signatures[0] = consentDto.getSignatureOne();
                  reviewBean.setSignatures(signatures);
                }
              }
            }
            consent.setReview(reviewBean);
          }
          eligibilityConsentResponse.setConsent(consent);

          eligibilityConsentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
        }
      } else {
        eligibilityConsentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - eligibilityConsentMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - eligibilityConsentMetadata() :: Ends");
    return eligibilityConsentResponse;
  }

  /**
   * Get consent document by passing the consent version or the activity id and activity version for
   * the provided study identifier
   *
   * @param studyId the study identifier
   * @param consentVersion the consent version
   * @param activityId the activity identifier
   * @param activityVersion the activity version
   * @return {@link ConsentDocumentResponse}
   * @throws DAOException
   * @author BTC
   */
  public ConsentDocumentResponse consentDocument(
      String studyId,
      String consentVersion,
      String activityId,
      String activityVersion,
      String language)
      throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - consentDocument() :: Starts");
    Session session = null;
    ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
    ConsentDto consent = null;
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    String studyVersionQuery = "from StudyVersionDto SVDTO where SVDTO.customStudyId=:studyId";
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId =:customStudyId and SDTO.live=1")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto == null) {
        studyDto =
            (StudyDto)
                session
                    .createQuery(
                        "from StudyDto SDTO"
                            + " where SDTO.customStudyId =:customStudyId and SDTO.status='Pre-launch(Published)'")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .uniqueResult();
      }

      if (studyDto != null) {
        if (StringUtils.isNotEmpty(consentVersion)) {
          studyVersionQuery += " and ROUND(SVDTO.consentVersion, 1)=" + consentVersion;
        } else if (StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)) {
          studyVersionQuery += " and ROUND(SVDTO.activityVersion, 1)=" + activityVersion;
        }

        // Get study version details by version identifier in descending
        // order
        studyVersionQuery += " ORDER BY SVDTO.versionId DESC";

        if (!studyDto
            .getStatus()
            .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
          studyVersionDto =
              (StudyVersionDto)
                  session
                      .createQuery(studyVersionQuery)
                      .setString("studyId", studyId)
                      .setMaxResults(1)
                      .uniqueResult();
        } else {
          studyVersionDto = new StudyVersionDto();
          studyVersionDto.setConsentVersion(0f);
        }
        if (studyVersionDto != null) {
          if (!studyDto
              .getStatus()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
            consent =
                (ConsentDto)
                    session
                        .createQuery(
                            " from ConsentDto CDTO"
                                + " where CDTO.customStudyId =:customStudyId and ROUND(CDTO.version, 1)=:version and CDTO.live=1")
                        .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                        .setFloat(
                            StudyMetaDataEnum.QF_VERSION.value(),
                            studyVersionDto.getConsentVersion())
                        .uniqueResult();
          } else {
            consent =
                (ConsentDto)
                    session
                        .createQuery("from ConsentDto CDTO where CDTO.studyId =:studyId")
                        .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                        .uniqueResult();
          }

          StudyLanguageBO studyLanguage = null;
          if (StringUtils.isNotBlank(language)
              && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
              && studyDto.getMultiLanguageFlag() != null
              && studyDto.getMultiLanguageFlag()
              && studyDto.getSelectedLanguages().contains(language)) {
            studyLanguage =
                (StudyLanguageBO)
                    session
                        .createQuery(
                            "from StudyLanguageBO SLBO where SLBO.studyLanguagePK.study_id= :studyId AND SLBO.studyLanguagePK.langCode= :langCode")
                        .setString("studyId", studyDto.getId().toString())
                        .setString("langCode", language)
                        .uniqueResult();
          }

          if (consent != null) {
            ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
            consentDocumentBean.setType("text/html");
            consentDocumentBean.setVersion(
                (consent.getVersion() == null)
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : String.valueOf(consent.getVersion()));
            if (studyLanguage != null) {
              consentDocumentBean.setContent(
                  StringUtils.isEmpty(studyLanguage.getConsentDocContent())
                      ? ""
                      : studyLanguage
                          .getConsentDocContent()
                          .replaceAll("&#34;", "'")
                          .replaceAll("em>", "i>")
                          .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
            } else {
              consentDocumentBean.setContent(
                  StringUtils.isEmpty(consent.getConsentDocContent())
                      ? ""
                      : consent
                          .getConsentDocContent()
                          .replaceAll("&#34;", "'")
                          .replaceAll("em>", "i>")
                          .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
            }
            consentDocumentResponse.setConsent(consentDocumentBean);
          }
          consentDocumentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
        }
      } else {
        consentDocumentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - consentDocument() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - consentDocument() :: Ends");
    return consentDocumentResponse;
  }

  /**
   * Get resources metadata for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link ResourcesResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ResourcesResponse resourcesForStudy(String studyId, String language) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - resourcesForStudy() :: Starts");
    Session session = null;
    ResourcesResponse resourcesResponse = new ResourcesResponse();
    List<ResourcesDto> resourcesDtoList = null;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId =:customStudyId and SDTO.live=1")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {

        resourcesDtoList =
            session
                .createQuery(
                    "from ResourcesDto RDTO"
                        + " where RDTO.studyId =:studyId  and RDTO.status=true and RDTO.action=true"
                        + " ORDER BY RDTO.sequenceNo")
                .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                .list();
        if (null != resourcesDtoList && !resourcesDtoList.isEmpty()) {

          List<ResourcesBean> resourcesBeanList = new ArrayList<>();
          for (ResourcesDto resourcesDto : resourcesDtoList) {

            ResourcesLangBO resourcesLang = null;
            if (StringUtils.isNotBlank(language)
                && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                && studyDto.getMultiLanguageFlag() != null
                && studyDto.getMultiLanguageFlag()
                && studyDto.getSelectedLanguages().contains(language)) {
              resourcesLang =
                  (ResourcesLangBO)
                      session
                          .createQuery(
                              "from ResourcesLangBO where resourcesLangPK.id= :resourceId AND resourcesLangPK.langCode= :langCode")
                          .setString("resourceId", resourcesDto.getId().toString())
                          .setString("langCode", language)
                          .uniqueResult();
            }

            ResourcesBean resourcesBean = new ResourcesBean();
            resourcesBean.setAudience(
                resourcesDto.isResourceType()
                    ? StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_LIMITED
                    : StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_ALL);

            String richText = "";
            String pdfURL = "";
            if (resourcesLang != null) {
              resourcesBean.setTitle(
                  StringUtils.isEmpty(resourcesLang.getTitle()) ? "" : resourcesLang.getTitle());
              resourcesBean.setNotificationText(
                  StringUtils.isEmpty(resourcesLang.getResourceText())
                      ? ""
                      : resourcesLang.getResourceText());
              richText = resourcesLang.getRichText();
              pdfURL = resourcesLang.getPdfUrl();
            } else {
              resourcesBean.setTitle(
                  StringUtils.isEmpty(resourcesDto.getTitle()) ? "" : resourcesDto.getTitle());
              resourcesBean.setNotificationText(
                  StringUtils.isEmpty(resourcesDto.getResourceText())
                      ? ""
                      : resourcesDto.getResourceText());
              richText = resourcesDto.getRichText();
              pdfURL = resourcesDto.getPdfUrl();
            }

            if (!resourcesDto.isTextOrPdf()) {
              resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
              resourcesBean.setContent(StringUtils.isEmpty(richText) ? "" : richText);
            } else {
              resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
              resourcesBean.setContent(
                  StringUtils.isEmpty(pdfURL)
                      ? ""
                      : propMap.get(StudyMetaDataConstants.FDA_SMD_RESOURCE_PDF_PATH) + pdfURL);
            }
            resourcesBean.setResourcesId(
                resourcesDto.getId() == null ? "" : String.valueOf(resourcesDto.getId()));

            if (!resourcesDto.isResourceVisibility()) {
              Map<String, Object> availability = new LinkedHashMap<>();
              availability.put(
                  "availableDate",
                  StringUtils.isEmpty(resourcesDto.getStartDate())
                      ? ""
                      : resourcesDto.getStartDate());
              availability.put(
                  "expiryDate",
                  StringUtils.isEmpty(resourcesDto.getEndDate()) ? "" : resourcesDto.getEndDate());

              if (resourcesDto.getTimePeriodFromDays() != null) {
                availability.put(
                    "startDays",
                    resourcesDto.isxDaysSign()
                        ? Integer.parseInt("-" + resourcesDto.getTimePeriodFromDays())
                        : resourcesDto.getTimePeriodFromDays());
              } else {
                availability.put("startDays", 0);
              }

              if (resourcesDto.getTimePeriodToDays() != null) {
                availability.put(
                    "endDays",
                    resourcesDto.isyDaysSign()
                        ? Integer.parseInt("-" + resourcesDto.getTimePeriodToDays())
                        : resourcesDto.getTimePeriodToDays());
              } else {
                availability.put("endDays", 0);
              }
              // Phase 2a anchordate
              if (resourcesDto.getAnchorDateId() != null) {
                availability.put(
                    "availabilityType", StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE);
                String searchQuery = "";
                searchQuery = "from AnchorDateTypeDto a where a.id=:id";
                AnchorDateTypeDto anchorDateTypeDto =
                    (AnchorDateTypeDto)
                        session
                            .createQuery(searchQuery)
                            .setInteger("id", resourcesDto.getAnchorDateId())
                            .uniqueResult();
                if (anchorDateTypeDto != null) {
                  if (null != anchorDateTypeDto.getParticipantProperty()
                      && anchorDateTypeDto.getParticipantProperty()) {
                    query =
                        session
                            .createQuery(
                                "From ParticipantPropertiesBO PPBO WHERE PPBO.customStudyId =:studyId and PPBO.active=1 and PPBO.anchorDateId=:id")
                            .setString("studyId", studyId)
                            .setInteger("id", anchorDateTypeDto.getId());
                    ParticipantPropertiesBO participantPropertiesBO =
                        (ParticipantPropertiesBO) query.uniqueResult();
                    if (null != participantPropertiesBO) {
                      availability.put(
                          "sourceType", StudyMetaDataConstants.ANCHOR_TYPE_PARTICIPANTPROPERTY);
                      Map<String, Object> propertyMetadata = new LinkedHashMap<>();
                      propertyMetadata.put(
                          "propertyType", participantPropertiesBO.getPropertyType());
                      propertyMetadata.put("propertyId", participantPropertiesBO.getShortTitle());
                      propertyMetadata.put(
                          "propertyDataFormat", participantPropertiesBO.getDataType());
                      propertyMetadata.put(
                          "shouldRefresh", participantPropertiesBO.getRefreshedValue());
                      propertyMetadata.put("dataSource", participantPropertiesBO.getDataSource());
                      if (participantPropertiesBO.getStatus()) {
                        propertyMetadata.put("status", "active");
                      } else {
                        propertyMetadata.put("status", "deactivated");
                      }
                      if (participantPropertiesBO.getDataType().equalsIgnoreCase("date")
                          && participantPropertiesBO
                              .getDataSource()
                              .equalsIgnoreCase("ExternalSystem")) {
                        propertyMetadata.put(
                            "externalPropertyId",
                            participantPropertiesBO.getShortTitle() + "ExternalId");
                        propertyMetadata.put(
                            "dateOfEntryId", participantPropertiesBO.getShortTitle() + "EntryDate");
                      } else {
                        propertyMetadata.put("externalPropertyId", "");
                        propertyMetadata.put("dateOfEntryId", "");
                      }
                      availability.put("propertyMetadata", propertyMetadata);
                      availability.put("sourceKey", "");
                      availability.put("sourceActivityId", "");
                      availability.put("sourceFormKey", "");
                    }
                  } else if (!anchorDateTypeDto
                      .getName()
                      .replace(" ", "")
                      .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
                    availability.put(
                        "sourceType", StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
                    availability.put("propertyMetadata", new LinkedHashMap<>());
                    searchQuery =
                        "select s.step_short_title,qr.short_title"
                            + " from questionnaires qr,questions q, questionnaires_steps s"
                            + " where s.questionnaires_id=qr.id"
                            + " and s.instruction_form_id=q.id and s.step_type='Question'"
                            + " and qr.custom_study_id=:studyId"
                            + " and qr.schedule_type= :schedule_type and qr.frequency = :frequency and q.anchor_date_id=:id";
                    List<?> result =
                        session
                            .createSQLQuery(searchQuery)
                            .setString("studyId", studyId)
                            .setString("schedule_type", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                            .setString("frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                            .setInteger("id", resourcesDto.getAnchorDateId())
                            .list();
                    if (null != result && !result.isEmpty()) {
                      Object[] objects = (Object[]) result.get(0);
                      availability.put("sourceKey", (String) objects[0]);
                      availability.put("sourceActivityId", (String) objects[1]);
                      availability.put("sourceFormKey", "");
                    } else {
                      String query = "";
                      query =
                          "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                              + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                              + " where q.id=fm.questionId and f.formId=fm.formId"
                              + " and f.formId=qsf.instructionFormId and qsf.stepType='Form'"
                              + " and qsf.questionnairesId=qq.id and q.anchorDateId=:id "
                              + "and qq.customStudyId=:studyId and qq.scheduleType=:schedule_type and qq.frequency =:frequency";
                      List<?> result1 =
                          session
                              .createQuery(query)
                              .setInteger("id", resourcesDto.getAnchorDateId())
                              .setString("studyId", studyId)
                              .setString(
                                  "schedule_type", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                              .setString(
                                  "frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                              .list();
                      if (null != result1 && !result1.isEmpty()) {
                        // for(int i=0;i<result1.size();i++) {
                        Object[] objects = (Object[]) result1.get(0);
                        availability.put("sourceKey", (String) objects[0]);
                        availability.put("sourceActivityId", (String) objects[2]);
                        availability.put("sourceFormKey", (String) objects[1]);
                        // }
                      }
                    }
                  } else {

                    availability.put(
                        "sourceType", StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
                    availability.put("propertyMetadata", new LinkedHashMap<>());
                    availability.put("sourceKey", "");
                    availability.put("sourceActivityId", "");
                    availability.put("sourceFormKey", "");
                  }
                }

              } else {
                availability.put("availabilityType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR);
              }
              // phase 2a anchordate
              resourcesBean.setAvailability(availability);
            }
            resourcesBeanList.add(resourcesBean);
          }
          resourcesResponse.setResources(resourcesBeanList);
        }
        resourcesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        resourcesResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - resourcesForStudy() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - resourcesForStudy() :: Ends");
    return resourcesResponse;
  }

  /**
   * Get study metadata for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link StudyInfoResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public StudyInfoResponse studyInfo(String studyId, String language) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - studyInfo() :: Starts");
    Session session = null;
    StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
    List<StudyPageDto> studyPageDtoList = null;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId =:customStudyId and SDTO.live=1")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto == null) {
        studyDto =
            (StudyDto)
                session
                    .createQuery(
                        "from StudyDto SDTO"
                            + " where SDTO.customStudyId =:customStudyId and SDTO.status='Pre-launch(Published)'")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .uniqueResult();
      }

      if (studyDto != null) {

        studyInfoResponse.setStudyWebsite(
            StringUtils.isEmpty(studyDto.getStudyWebsite()) ? "" : studyDto.getStudyWebsite());

        List<InfoBean> infoList = new ArrayList<>();
        studyPageDtoList =
            session
                .createQuery("from StudyPageDto SPDTO where SPDTO.studyId =:studyId")
                .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                .list();

        StudyLanguageBO studyLanguage = null;
        Map<Integer, StudyPageLanguageBO> studyPageLanguageMap = null;
        if (StringUtils.isNotBlank(language)
            && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
            && studyDto.getMultiLanguageFlag() != null
            && studyDto.getMultiLanguageFlag()
            && studyDto.getSelectedLanguages().contains(language)) {
          studyLanguage =
              (StudyLanguageBO)
                  session
                      .createQuery(
                          "from StudyLanguageBO SLBO where SLBO.studyLanguagePK.study_id= :studyId AND SLBO.studyLanguagePK.langCode= :langCode")
                      .setInteger("studyId", studyDto.getId())
                      .setString("langCode", language)
                      .uniqueResult();
          if (studyLanguage != null) {
            List<StudyPageLanguageBO> studyPageLanguageList =
                session
                    .createQuery(
                        "from StudyPageLanguageBO SPLBO where SPLBO.studyId= :studyId AND SPLBO.langCode= :langCode")
                    .setString("studyId", studyDto.getId().toString())
                    .setString("langCode", language)
                    .list();
            if (!studyPageLanguageList.isEmpty()) {
              studyPageLanguageMap = new HashMap<>();
              int count = 1;
              for (StudyPageLanguageBO studyPageLanguage : studyPageLanguageList) {
                studyPageLanguageMap.put(count, studyPageLanguage);
                count++;
              }
            }
          }
        }

        if (null != studyPageDtoList && !studyPageDtoList.isEmpty()) {
          int count = 1;
          for (StudyPageDto studyPageInfo : studyPageDtoList) {
            InfoBean info = new InfoBean();

            if (infoList.isEmpty()) {
              info.setType(StudyMetaDataConstants.TYPE_VIDEO);
              info.setVideoLink(
                  StringUtils.isEmpty(studyDto.getMediaLink()) ? "" : studyDto.getMediaLink());
            } else {
              info.setType(StudyMetaDataConstants.TYPE_TEXT);
              info.setVideoLink("");
            }

            info.setImage(
                StringUtils.isEmpty(studyPageInfo.getImagePath())
                    ? ""
                    : propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_PAGE_PATH)
                        + studyPageInfo.getImagePath()
                        + StudyMetaDataUtil.getMilliSecondsForImagePath());
            if (studyPageLanguageMap != null) {
              info.setTitle(
                  StringUtils.isEmpty(studyPageLanguageMap.get(count).getTitle())
                      ? ""
                      : studyPageLanguageMap.get(count).getTitle());
              info.setText(
                  StringUtils.isEmpty(studyPageLanguageMap.get(count).getDescription())
                      ? ""
                      : studyPageLanguageMap.get(count).getDescription());
            } else {
              info.setTitle(
                  StringUtils.isEmpty(studyPageInfo.getTitle()) ? "" : studyPageInfo.getTitle());
              info.setText(
                  StringUtils.isEmpty(studyPageInfo.getDescription())
                      ? ""
                      : studyPageInfo.getDescription());
            }

            infoList.add(info);
            count++;
          }
        } else {

          InfoBean info = new InfoBean();

          if (infoList.isEmpty()) {
            info.setType(StudyMetaDataConstants.TYPE_VIDEO);
            info.setVideoLink(
                StringUtils.isEmpty(studyDto.getMediaLink()) ? "" : studyDto.getMediaLink());
          } else {
            info.setType(StudyMetaDataConstants.TYPE_TEXT);
            info.setVideoLink("");
          }

          info.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
          info.setImage(
              StringUtils.isEmpty(studyDto.getThumbnailImage())
                  ? ""
                  : propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                      + studyDto.getThumbnailImage()
                      + StudyMetaDataUtil.getMilliSecondsForImagePath());
          info.setText(StringUtils.isEmpty(studyDto.getFullName()) ? "" : studyDto.getFullName());
          infoList.add(info);
        }
        studyInfoResponse.setInfo(infoList);

        WithdrawalConfigBean withdrawConfig = new WithdrawalConfigBean();
        switch (studyDto.getRetainParticipant()) {
          case StudyMetaDataConstants.YES:
            withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_NO_ACTION);
            break;
          case StudyMetaDataConstants.NO:
            withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_DELETE_DATA);
            break;
          case StudyMetaDataConstants.ALL:
            withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_ASK_USER);
            break;
          default:
            break;
        }
        if (studyLanguage != null) {
          withdrawConfig.setMessage(
              StringUtils.isEmpty(studyLanguage.getAllowRejoinText())
                  ? ""
                  : studyLanguage.getAllowRejoinText());
        } else {
          withdrawConfig.setMessage(
              StringUtils.isEmpty(studyDto.getAllowRejoinText())
                  ? ""
                  : studyDto.getAllowRejoinText());
        }

        studyInfoResponse.setWithdrawalConfig(withdrawConfig);

        if (!studyDto
            .getStatus()
            .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
          List<QuestionnairesDto> questionnairesList =
              session
                  .createQuery(
                      "from QuestionnairesDto QDTO where QDTO.customStudyId=:studyId and QDTO.active=true and QDTO.status=true and QDTO.live=1")
                  .setString("studyId", studyDto.getCustomStudyId())
                  .list();
          if (questionnairesList != null && !questionnairesList.isEmpty()) {

            List<Integer> questionnaireIdsList = new ArrayList<>();
            Map<Integer, QuestionnairesDto> questionnaireMap = new TreeMap<>();
            Map<String, QuestionnairesStepsDto> stepsMap = new TreeMap<>();
            Map<Integer, QuestionsDto> questionsMap = null;
            Map<Integer, FormMappingDto> formMappingMap = new TreeMap<>();

            for (QuestionnairesDto questionnaire : questionnairesList) {
              questionnaireIdsList.add(questionnaire.getId());
              questionnaireMap.put(questionnaire.getId(), questionnaire);
            }

            if (!questionnaireIdsList.isEmpty()) {

              List<Integer> questionIdsList = new ArrayList<>();
              List<Integer> formIdsList = new ArrayList<>();
              List<QuestionnairesStepsDto> questionnairesStepsList =
                  session
                      .createQuery(
                          "from QuestionnairesStepsDto QSDTO"
                              + " where QSDTO.active=true and QSDTO.status=true"
                              + " and QSDTO.questionnairesId in (:idList)"
                              + " and QSDTO.stepType in (:stepTypeList)")
                      .setParameterList("idList", questionnaireIdsList)
                      .setParameterList(
                          "stepTypeList",
                          Arrays.asList(
                              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM))
                      .list();
              if (questionnairesStepsList != null && !questionnairesStepsList.isEmpty()) {

                for (QuestionnairesStepsDto stepsDto : questionnairesStepsList) {
                  if (stepsDto
                      .getStepType()
                      .equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)) {
                    questionIdsList.add(stepsDto.getInstructionFormId());
                    stepsMap.put(
                        stepsDto.getInstructionFormId()
                            + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                        stepsDto);
                  } else {
                    formIdsList.add(stepsDto.getInstructionFormId());
                    stepsMap.put(
                        stepsDto.getInstructionFormId()
                            + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                        stepsDto);
                  }
                }

                if (!questionIdsList.isEmpty()) {
                  List<QuestionsDto> questionnsList =
                      session
                          .createQuery(
                              "from QuestionsDto QDTO"
                                  + " where QDTO.active=true and QDTO.status=true"
                                  + " and QDTO.id in (:idList)"
                                  + " and QDTO.responseType=10 and QDTO.useAnchorDate=true")
                          .setParameterList("idList", questionIdsList)
                          .setMaxResults(1)
                          .list();
                  if (questionnsList != null && !questionnsList.isEmpty()) {

                    questionsMap = new TreeMap<>();
                    for (QuestionsDto question : questionnsList) {
                      questionsMap.put(question.getId(), question);
                    }
                  }
                }

                if (questionsMap == null && !formIdsList.isEmpty()) {

                  List<Integer> formQuestionsList = new ArrayList<>();
                  List<FormMappingDto> formMappingList =
                      session
                          .createQuery(
                              "from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId"
                                  + " from FormDto FDTO where FDTO.formId in (:idList)"
                                  + " and FDTO.active=true) and FMDTO.active=true"
                                  + " ORDER BY FMDTO.formId, FMDTO.sequenceNo")
                          .setParameterList("idList", formIdsList)
                          .list();
                  if (formMappingList != null && !formMappingList.isEmpty()) {

                    for (FormMappingDto formMapping : formMappingList) {
                      formQuestionsList.add(formMapping.getQuestionId());
                      formMappingMap.put(formMapping.getQuestionId(), formMapping);
                    }

                    if (!formQuestionsList.isEmpty()) {
                      List<QuestionsDto> questionnsList =
                          session
                              .createQuery(
                                  "from QuestionsDto QDTO"
                                      + " where QDTO.active=true and QDTO.status=true"
                                      + " and QDTO.id in (:formQuestionsList)"
                                      + " and QDTO.responseType=10 and QDTO.useAnchorDate=true")
                              .setParameterList("formQuestionsList", formQuestionsList)
                              .setMaxResults(1)
                              .list();
                      if (questionnsList != null && !questionnsList.isEmpty()) {

                        questionsMap = new TreeMap<>();
                        for (QuestionsDto question : questionnsList) {
                          questionsMap.put(question.getId(), question);
                        }
                      }
                    }
                  }
                }

                if (questionsMap != null) {
                  AnchorDateBean anchorDate = new AnchorDateBean();
                  anchorDate.setType(StudyMetaDataConstants.ANCHORDATE_TYPE_QUESTION);
                  for (Map.Entry<Integer, QuestionsDto> map : questionsMap.entrySet()) {
                    QuestionsDto questionDto = map.getValue();
                    if (questionDto != null) {
                      QuestionnairesStepsDto questionnairesSteps;

                      if (StringUtils.isNotEmpty(questionDto.getShortTitle())) {
                        FormMappingDto formMapping = formMappingMap.get(questionDto.getId());
                        questionnairesSteps =
                            stepsMap.get(
                                formMapping.getFormId()
                                    + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM);
                      } else {
                        questionnairesSteps =
                            stepsMap.get(
                                questionDto.getId()
                                    + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                      }

                      if (questionnairesSteps != null) {
                        QuestionnairesDto questionnairesDto =
                            questionnaireMap.get(questionnairesSteps.getQuestionnairesId());

                        if (questionnairesDto != null) {
                          QuestionInfoBean questionInfoBean = new QuestionInfoBean();
                          questionInfoBean.setActivityId(questionnairesDto.getShortTitle());
                          questionInfoBean.setActivityVersion(
                              questionnairesDto.getVersion().toString());

                          if (questionnairesSteps
                              .getStepType()
                              .equalsIgnoreCase(
                                  StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)) {
                            questionInfoBean.setKey(questionDto.getShortTitle());
                          } else {
                            questionInfoBean.setKey(questionnairesSteps.getStepShortTitle());
                          }
                          anchorDate.setQuestionInfo(questionInfoBean);
                        }
                      }
                    }
                  }
                  studyInfoResponse.setAnchorDate(anchorDate);
                }
              }
            }
          }
        }
        studyInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        studyInfoResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - studyInfo() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - studyInfo() :: Ends");
    return studyInfoResponse;
  }

  /**
   * Check study for the provided study identifier
   *
   * @param studyId the study identifier
   * @return {@link Boolean}
   * @throws DAOException
   * @author BTC
   */
  public boolean isValidStudy(String studyId) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Starts");
    Session session = null;
    boolean isValidStudy = false;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId= :customStudyId"
                          + " ORDER BY SDTO.id DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      isValidStudy = (studyDto == null) ? false : true;
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Ends");
    return isValidStudy;
  }

  /**
   * Check activity for the provided activity version, study and activity identifier
   *
   * @param activityId the activity identifier
   * @param studyId the study identifier
   * @param activityVersion the activity version
   * @return {@link Boolean}
   * @throws DAOException
   * @author BTC
   */
  public boolean isValidActivity(String activityId, String studyId, String activityVersion)
      throws DAOException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Starts");
    Session session = null;
    boolean isValidActivity = false;
    ActiveTaskDto activeTaskDto = null;
    QuestionnairesDto questionnaireDto = null;
    try {
      session = sessionFactory.openSession();
      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO where ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version and ATDTO.customStudyId= :customStudyId"
                          + " ORDER BY ATDTO.id DESC")
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      isValidActivity = (activeTaskDto == null) ? false : true;

      if (!isValidActivity) {
        questionnaireDto =
            (QuestionnairesDto)
                session
                    .createQuery(
                        "from QuestionnairesDto QDTO where QDTO.shortTitle= :shortTitle"
                            + " and ROUND(QDTO.version, 1)= :version and QDTO.customStudyId= :customStudyId"
                            + " ORDER BY QDTO.id DESC")
                    .setString(
                        StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                        StudyMetaDataUtil.replaceSingleQuotes(activityId))
                    .setFloat(
                        StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .setMaxResults(1)
                    .uniqueResult();
        isValidActivity = (questionnaireDto == null) ? false : true;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidActivity() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Ends");
    return isValidActivity;
  }

  /**
   * Check whether activity is questionnaire for the provided study and activity identifier
   *
   * @param activityId the activity identifier
   * @param studyId the study identifier
   * @param activityVersion the activity version
   * @return {@link Boolean}
   * @throws DAOException
   * @author BTC
   */
  public boolean isActivityTypeQuestionnaire(
      String activityId, String studyId, String activityVersion) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Starts");
    Session session = null;
    boolean isActivityTypeQuestionnaire = true;
    ActiveTaskDto activeTaskDto = null;
    try {
      session = sessionFactory.openSession();
      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO where ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version and ATDTO.customStudyId= :customStudyId"
                          + " ORDER BY ATDTO.id DESC")
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      if (activeTaskDto != null) {
        isActivityTypeQuestionnaire = false;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Ends");
    return isActivityTypeQuestionnaire;
  }

  /**
   * Get the consent document display title
   *
   * @param displaytitle the display title
   * @return consent document display title code
   * @throws DAOException
   * @author BTC
   */
  public String getconsentDocumentDisplayTitle(String displaytitle) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - getconsentDocumentDisplayTitle() :: Starts");
    String consentTitle = "";
    try {
      switch (displaytitle) {
        case "overview":
          consentTitle = "Overview";
          break;
        case "dataGathering":
          consentTitle = "Data Gathering";
          break;
        case "privacy":
          consentTitle = "Privacy";
          break;
        case "dataUse":
          consentTitle = "Data Use";
          break;
        case "timeCommitment":
          consentTitle = "Time Commitment";
          break;
        case "studySurvey":
          consentTitle = "Study Survey";
          break;
        case "studyTasks":
          consentTitle = "Study Tasks";
          break;
        case "withdrawing":
          consentTitle = "Withdrawing";
          break;
        case "customService":
          consentTitle = "Custom Service";
          break;
        default:
          consentTitle = displaytitle;
          break;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - getconsentDocumentDisplayTitle() :: ERROR", e);
    }
    LOGGER.info("INFO: StudyMetaDataDao - getconsentDocumentDisplayTitle() :: Ends");
    return consentTitle;
  }

  /**
   * Check study for the provided study identifier
   *
   * @param token the study identifier
   * @return {@link Boolean}
   * @throws DAOException
   * @author BTC
   */
  public boolean isValidToken(String token) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - isValidToken() :: Starts");
    Session session = null;
    boolean isValidStudy = false;
    EnrollmentTokenDto tokenDto = null;
    try {
      session = sessionFactory.openSession();
      tokenDto =
          (EnrollmentTokenDto)
              session
                  .createQuery(
                      "from EnrollmentTokenDto where enrollmentToken= :enrollmentToken ORDER BY id DESC")
                  .setString("enrollmentToken", token)
                  .setMaxResults(1)
                  .uniqueResult();
      if (null != tokenDto) {
        isValidStudy = true;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - isValidToken() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - isValidToken() :: Ends");
    return isValidStudy;
  }

  /**
   * Get all the configured studies from the WCP
   *
   * @param studyId
   * @return {@link StudyResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public StudyResponse study(String studyId, String language) throws DAOException {
    LOGGER.info("INFO: StudyMetaDataDao - study() :: Starts");
    Session session = null;
    StudyResponse studyResponse = new StudyResponse();
    List<StudyDto> studiesList = null;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        session = sessionFactory.openSession();

        studiesList =
            session
                .createQuery(
                    "from StudyDto SDTO where SDTO.customStudyId =:studyId and (SDTO.status= :status OR SDTO.live=1)")
                .setString("studyId", studyId)
                .setString(
                    StudyMetaDataEnum.QF_STATUS.value(),
                    StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)
                .list();
        if (null != studiesList && !studiesList.isEmpty()) {
          List<StudyBean> studyBeanList = new ArrayList<>();
          for (StudyDto studyDto : studiesList) {
            StudyLanguageBO studyLanguageBO = null;
            if (StringUtils.isNotBlank(language)
                && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)
                && studyDto.getMultiLanguageFlag() != null
                && studyDto.getMultiLanguageFlag()
                && studyDto.getSelectedLanguages().contains(language)) {
              studyLanguageBO =
                  (StudyLanguageBO)
                      session
                          .createQuery(
                              "from StudyLanguageBO SLBO where SLBO.studyLanguagePK.study_id= :studyId AND SLBO.studyLanguagePK.langCode= :langCode")
                          .setString("studyId", studyDto.getId().toString())
                          .setString("langCode", language)
                          .uniqueResult();
            }

            StudyBean studyBean = new StudyBean();
            studyBean.setStudyVersion(
                studyDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : studyDto.getVersion().toString());
            if (studyLanguageBO != null) {
              studyBean.setTagline(
                  StringUtils.isEmpty(studyLanguageBO.getStudyTagline())
                      ? ""
                      : studyLanguageBO.getStudyTagline());
              studyBean.setTitle(
                  StringUtils.isEmpty(studyLanguageBO.getName()) ? "" : studyLanguageBO.getName());
              studyBean.setSponsorName(
                  StringUtils.isEmpty(studyLanguageBO.getResearchSponsor())
                      ? ""
                      : studyLanguageBO.getResearchSponsor());
            } else {
              studyBean.setTagline(
                  StringUtils.isEmpty(studyDto.getStudyTagline())
                      ? ""
                      : studyDto.getStudyTagline());
              studyBean.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
              studyBean.setSponsorName(
                  StringUtils.isEmpty(studyDto.getResearchSponsor())
                      ? ""
                      : studyDto.getResearchSponsor());
            }
            if (StringUtils.isNotEmpty(studyDto.getCategory())
                && StringUtils.isNotEmpty(studyDto.getResearchSponsor())) {
              List<ReferenceTablesDto> referenceTablesList =
                  session
                      .createQuery(
                          "from ReferenceTablesDto RTDTO where RTDTO.id IN ("
                              + studyDto.getCategory()
                              + ")")
                      .list();
              if (null != referenceTablesList && !referenceTablesList.isEmpty()) {
                for (ReferenceTablesDto reference : referenceTablesList) {
                  if (reference
                      .getCategory()
                      .equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)) {
                    if (StringUtils.isNotBlank(language)
                        && !StringUtils.equals(language, MultiLanguageConstants.ENGLISH)) {
                      studyBean.setCategory(
                          StudyMetaDataUtil.getMultiLanguageText(
                              language, "id_" + studyDto.getCategory()));
                    } else {
                      studyBean.setCategory(
                          StringUtils.isEmpty(reference.getValue()) ? "" : reference.getValue());
                    }
                  }
                }
              }
            }
            if (StringUtils.isNotBlank(language)
                && !MultiLanguageConstants.ENGLISH.equals(language)) {
              studyBean.setStudyLanguage(MultiLanguageCodes.getValue(language));
            } else {
              studyBean.setStudyLanguage("English");
            }
            switch (studyDto.getStatus()) {
              case StudyMetaDataConstants.STUDY_STATUS_ACTIVE:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PAUSED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
                break;
              default:
                break;
            }

            studyBean.setLogo(
                StringUtils.isEmpty(studyDto.getThumbnailImage())
                    ? ""
                    : propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                        + studyDto.getThumbnailImage()
                        + StudyMetaDataUtil.getMilliSecondsForImagePath());
            studyBean.setStudyId(
                StringUtils.isEmpty(studyDto.getCustomStudyId())
                    ? ""
                    : studyDto.getCustomStudyId());

            SettingsBean settings = new SettingsBean();
            if (studyDto.getPlatform().contains(",")) {
              settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ALL);
            } else {
              switch (studyDto.getPlatform()) {
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_IOS);
                  break;
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID);
                  break;
                default:
                  break;
              }
            }
            if (StringUtils.isNotEmpty(studyDto.getAllowRejoin())
                && studyDto.getAllowRejoin().equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setRejoin(true);
            } else {
              settings.setRejoin(false);
            }
            if (StringUtils.isNotEmpty(studyDto.getEnrollingParticipants())
                && studyDto
                    .getEnrollingParticipants()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setEnrolling(true);
            } else {
              settings.setEnrolling(false);
            }
            studyBean.setSettings(settings);
            studyBeanList.add(studyBean);
          }
          studyResponse.setStudies(studyBeanList);
        }
        studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - study() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - study() :: Ends");
    return studyResponse;
  }

  public List<ParticipantPropertiesBO> getParticipantProperties(String studyId) throws Exception {
    LOGGER.info("INFO: StudyMetaDataDao - getParticipantProperties() :: Starts");
    Session session = null;
    List<ParticipantPropertiesBO> participantPropertiesBOList = null;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        session = sessionFactory.openSession();
        query =
            session
                .createQuery(
                    "From ParticipantPropertiesBO PBO WHERE PBO.customStudyId = :studyId "
                        + "and PBO.active=1 and PBO.live=1  order by PBO.createdDate DESC")
                .setString("studyId", studyId);
        participantPropertiesBOList = query.list();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - getParticipantProperties() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - getParticipantProperties() :: Ends");
    return participantPropertiesBOList;
  }

  public List<ParticipantPropertiesDraftBO> getParticipantPropertiesByStudyVersion(
      String studyId, String studyVersion) throws Exception {
    LOGGER.info("INFO: StudyMetaDataDao - getParticipantPropertiesByStudyVersion() :: Starts");
    Session session = null;
    List<ParticipantPropertiesDraftBO> participantPropertiesBOList = null;
    try {
      if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(studyVersion)) {
        session = sessionFactory.openSession();
        query =
            session
                .createQuery(
                    "From ParticipantPropertiesDraftBO PBO WHERE PBO.customStudyId =:studyId "
                        + "and PBO.studyVersion like :studyVersion and PBO.active=1 and PBO.live=1 order by PBO.createdDate DESC")
                .setString("studyId", studyId)
                .setString("studyVersion", studyVersion);
        participantPropertiesBOList = query.list();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - getParticipantPropertiesByStudyVersion() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - getParticipantPropertiesByStudyVersion() :: Ends");
    return participantPropertiesBOList;
  }

  public Float getStudylatestVersion(String studyId) {
    LOGGER.info("INFO: StudyMetaDataDao - getStudylatestVersion() :: Starts");
    Session session = null;
    StudyDto studyDto = null;
    Float version = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO where SDTO.customStudyId =:customStudyId and SDTO.live=1")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto == null) {
        studyDto =
            (StudyDto)
                session
                    .createQuery(
                        "from StudyDto SDTO"
                            + " where SDTO.customStudyId =:customStudyId and SDTO.status='Pre-launch(Published)'")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .uniqueResult();
      }

      if (null != studyDto) {
        version = studyDto.getVersion();
      }

    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - getStudylatestVersion() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: StudyMetaDataDao - getStudylatestVersion() :: Ends");
    return version;
  }
}
