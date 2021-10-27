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

import com.hphc.mystudies.bean.ActiveTaskActivityMetaDataResponse;
import com.hphc.mystudies.bean.ActiveTaskActivityStepsBean;
import com.hphc.mystudies.bean.ActiveTaskActivityStructureBean;
import com.hphc.mystudies.bean.ActivitiesBean;
import com.hphc.mystudies.bean.ActivityAnchorDateBean;
import com.hphc.mystudies.bean.ActivityAnchorEndBean;
import com.hphc.mystudies.bean.ActivityAnchorStartBean;
import com.hphc.mystudies.bean.ActivityFrequencyAnchorRunsBean;
import com.hphc.mystudies.bean.ActivityFrequencyBean;
import com.hphc.mystudies.bean.ActivityFrequencyScheduleBean;
import com.hphc.mystudies.bean.ActivityMetadataBean;
import com.hphc.mystudies.bean.ActivityResponse;
import com.hphc.mystudies.bean.DestinationBean;
import com.hphc.mystudies.bean.FetalKickCounterFormatBean;
import com.hphc.mystudies.bean.ParticipantPropertyBean;
import com.hphc.mystudies.bean.QuestionnaireActivityMetaDataResponse;
import com.hphc.mystudies.bean.QuestionnaireActivityStepsBean;
import com.hphc.mystudies.bean.SpatialSpanMemoryFormatBean;
import com.hphc.mystudies.bean.TowerOfHanoiFormatBean;
import com.hphc.mystudies.bean.appendix.QuestionnaireActivityStructureBean;
import com.hphc.mystudies.dto.ActiveTaskAttrtibutesValuesDto;
import com.hphc.mystudies.dto.ActiveTaskCustomFrequenciesDto;
import com.hphc.mystudies.dto.ActiveTaskDto;
import com.hphc.mystudies.dto.ActiveTaskFrequencyDto;
import com.hphc.mystudies.dto.ActiveTaskLangBO;
import com.hphc.mystudies.dto.ActiveTaskListDto;
import com.hphc.mystudies.dto.ActiveTaskMasterAttributeDto;
import com.hphc.mystudies.dto.AnchorDateTypeDto;
import com.hphc.mystudies.dto.FormLangBO;
import com.hphc.mystudies.dto.FormMappingDto;
import com.hphc.mystudies.dto.InstructionsDto;
import com.hphc.mystudies.dto.InstructionsLangBO;
import com.hphc.mystudies.dto.ParticipantPropertiesBO;
import com.hphc.mystudies.dto.QuestionLangBO;
import com.hphc.mystudies.dto.QuestionReponseTypeDto;
import com.hphc.mystudies.dto.QuestionResponseSubTypeDto;
import com.hphc.mystudies.dto.QuestionResponsetypeMasterInfoDto;
import com.hphc.mystudies.dto.QuestionnaireLangDto;
import com.hphc.mystudies.dto.QuestionnairesCustomFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesDto;
import com.hphc.mystudies.dto.QuestionnairesFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesStepsDto;
import com.hphc.mystudies.dto.QuestionsDto;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.MultiLanguageConstants;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Provides activity metadata business logic and model objects details.
 *
 * @author BTC
 * @since Jan 4, 2018 3:23:22 PM
 */
public class ActivityMetaDataDao {

  private static final Logger LOGGER = Logger.getLogger(ActivityMetaDataDao.class);

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;

  /**
   * Get all the activities for the provided study identifier and authorization credentials
   *
   * @param studyId the study identifier
   * @param authorization the Basic Authorization
   * @return {@link ActivityResponse}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActivityResponse studyActivityList(String studyId, String authorization, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - studyActivityList() :: Starts");
    Session session = null;
    ActivityResponse activityResponse = new ActivityResponse();
    List<ActiveTaskDto> activeTaskDtoList = null;
    List<QuestionnairesDto> questionnairesList = null;
    List<ActivitiesBean> activitiesBeanList = new ArrayList<>();
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    String deviceType = "";
    try {
      deviceType =
          StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
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

        activeTaskDtoList =
            session
                .createQuery(
                    "from ActiveTaskDto ATDTO"
                        + " where ATDTO.action=true and ATDTO.customStudyId=:customStudyId and (ATDTO.live=:live OR ATDTO.active=:active)"
                        + " ORDER BY ATDTO.createdDate DESC")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setInteger(StudyMetaDataEnum.QF_ACTIVE.value(), 0)
                .list();
        if (null != activeTaskDtoList && !activeTaskDtoList.isEmpty()) {
          for (ActiveTaskDto activeTaskDto : activeTaskDtoList) {
            boolean isSupporting = true;

            // Allow spatial span memory and tower of hanoi active
            // tasks only for iOS platform
            if (deviceType.equalsIgnoreCase(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)
                && !activeTaskDto.getTaskTypeId().equals(1)) {
              isSupporting = false;
            }

            if (isSupporting) {
              ActivitiesBean activityBean = new ActivitiesBean();

              if (StringUtils.isNotBlank(language)
                  && !MultiLanguageConstants.ENGLISH.equals(language)) {
                ActiveTaskLangBO activeTaskLangBO =
                    this.getActiveTaskLangById(activeTaskDto.getId(), language);
                if (activeTaskLangBO != null) {
                  activityBean.setTitle(
                      StringUtils.isEmpty(activeTaskLangBO.getDisplayName())
                          ? ""
                          : activeTaskLangBO.getDisplayName());
                  activityBean.setLastModified(
                      StringUtils.isEmpty(activeTaskLangBO.getModifiedOn())
                          ? ""
                          : StudyMetaDataUtil.getFormattedDateTimeZone(
                              activeTaskLangBO.getModifiedOn(),
                              StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                              StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
                }
              } else {
                activityBean.setTitle(
                    StringUtils.isEmpty(activeTaskDto.getDisplayName())
                        ? ""
                        : activeTaskDto.getDisplayName());
                activityBean.setLastModified(
                    StringUtils.isEmpty(activeTaskDto.getModifiedDate())
                        ? ""
                        : StudyMetaDataUtil.getFormattedDateTimeZone(
                            activeTaskDto.getModifiedDate(),
                            StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                            StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
              }

              activityBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
              activityBean.setState(
                  (activeTaskDto.getActive() != null && activeTaskDto.getActive() > 0)
                      ? StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE
                      : StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);

              activityBean.setActivityVersion(
                  (activeTaskDto.getVersion() == null || activeTaskDto.getVersion() < 1.0f)
                      ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                      : activeTaskDto.getVersion().toString());
              activityBean.setBranching(false);

              ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
              frequencyDetails =
                  this.getFrequencyRunsDetailsForActiveTasks(
                      activeTaskDto, frequencyDetails, session);
              frequencyDetails.setType(
                  StringUtils.isEmpty(activeTaskDto.getFrequency())
                      ? ""
                      : activeTaskDto.getFrequency());
              frequencyDetails.setAddNewRuns(activeTaskDto.getAddNewRuns());
              activityBean.setFrequency(frequencyDetails);

              activityBean =
                  this.getTimeDetailsByActivityIdForActiveTask(
                      activeTaskDto, activityBean, session);

              // For deleted task modified date time will be the
              // end date time of active task
              if (activeTaskDto.getActive() == null || activeTaskDto.getActive().equals(0)) {
                activityBean.setEndTime(
                    StudyMetaDataUtil.getFormattedDateTimeZone(
                        activeTaskDto.getModifiedDate(),
                        StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                        StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
              }

              activityBean.setActivityId(activeTaskDto.getShortTitle());

              if (null != activeTaskDto.getTaskTypeId()) {
                if (1 == activeTaskDto.getTaskTypeId().intValue()) {
                  activityBean.setTaskSubType(
                      StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER);
                } else if (2 == activeTaskDto.getTaskTypeId().intValue()) {
                  activityBean.setTaskSubType(StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI);
                } else if (3 == activeTaskDto.getTaskTypeId().intValue()) {
                  activityBean.setTaskSubType(
                      StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY);
                }
              }
              /** Phase2 a code for anchor date * */
              if (activeTaskDto.getScheduleType() != null
                  && !activeTaskDto.getScheduleType().isEmpty()) {
                activityBean.setSchedulingType(activeTaskDto.getScheduleType());
                if (activeTaskDto
                    .getScheduleType()
                    .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
                  activityBean =
                      this.getAnchordateDetailsByActivityIdForActivetask(
                          activeTaskDto, activityBean, session);
                }
              }
              /** Phase2a code for anchor date * */
              activitiesBeanList.add(activityBean);
            }
          }
        }

        questionnairesList =
            session
                .createQuery(
                    "from QuestionnairesDto QDTO"
                        + " where QDTO.status=true and QDTO.customStudyId=:customStudyId and (QDTO.live=:live OR QDTO.active=:active)"
                        + " ORDER BY QDTO.createdDate DESC")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setBoolean(StudyMetaDataEnum.QF_ACTIVE.value(), false)
                .list();
        if (questionnairesList != null && !questionnairesList.isEmpty()) {

          for (QuestionnairesDto questionaire : questionnairesList) {

            ActivitiesBean activityBean = new ActivitiesBean();

            if (StringUtils.isNotBlank(language)
                && !MultiLanguageConstants.ENGLISH.equals(language)) {
              QuestionnaireLangDto questionnaireLangDto = this.getQuestionnairesLangById(questionaire.getId(), language);
              if (questionnaireLangDto!=null) {
                activityBean.setTitle(
                    StringUtils.isEmpty(questionnaireLangDto.getTitle()) ? "" : questionnaireLangDto.getTitle());
              }
            } else {
              activityBean.setTitle(
                  StringUtils.isEmpty(questionaire.getTitle()) ? "" : questionaire.getTitle());
            }
            activityBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);
            activityBean.setState(
                questionaire.getActive()
                    ? StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE
                    : StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);
            ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
            frequencyDetails =
                this.getFrequencyRunsDetailsForQuestionaires(
                    questionaire, frequencyDetails, session);
            frequencyDetails.setType(
                StringUtils.isEmpty(questionaire.getFrequency())
                    ? ""
                    : questionaire.getFrequency());
            frequencyDetails.setAddNewRuns(questionaire.getAddNewRuns());

            activityBean.setFrequency(frequencyDetails);
            activityBean.setActivityId(questionaire.getShortTitle());
            activityBean.setActivityVersion(
                (questionaire.getVersion() == null || questionaire.getVersion() < 1.0f)
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : questionaire.getVersion().toString());
            activityBean.setBranching(
                (questionaire.getBranching() == null || !questionaire.getBranching())
                    ? false
                    : true);
            activityBean.setLastModified(
                StringUtils.isEmpty(questionaire.getModifiedDate())
                    ? ""
                    : StudyMetaDataUtil.getFormattedDateTimeZone(
                        questionaire.getModifiedDate(),
                        StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                        StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            activityBean =
                this.getTimeDetailsByActivityIdForQuestionnaire(
                    questionaire, activityBean, session);

            // For deleted task modified date time will be the end
            // date time of questionnaire
            if (!questionaire.getActive()) {
              activityBean.setEndTime(
                  StudyMetaDataUtil.getFormattedDateTimeZone(
                      questionaire.getModifiedDate(),
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }
            /** Phase2 a code for anchor date * */
            if (questionaire.getScheduleType() != null
                && !questionaire.getScheduleType().isEmpty()) {
              activityBean.setSchedulingType(questionaire.getScheduleType());
              if (questionaire
                  .getScheduleType()
                  .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
                activityBean =
                    this.getAnchordateDetailsByActivityIdForQuestionnaire(
                        questionaire, activityBean, session);
              }
            }
            /** Phase2a code for anchor date * */
            activitiesBeanList.add(activityBean);
          }
        }

        activityResponse.setActivities(activitiesBeanList);
        activityResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activityResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyActivityList() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: ActivityMetaDataDao - studyActivityList() :: Ends");
    return activityResponse;
  }

  /**
   * Get the activity metadata for the provided activity version, study and activity identifier
   *
   * @param studyId the study identifier
   * @param activityId the activity identifier
   * @param activityVersion the activity version
   * @return {@link ActiveTaskActivityMetaDataResponse}
   * @throws DAOException
   * @author BTC
   */
  public ActiveTaskActivityMetaDataResponse studyActiveTaskActivityMetadata(
      String studyId, String activityId, String activityVersion, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: Starts");
    Session session = null;
    ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse =
        new ActiveTaskActivityMetaDataResponse();
    ActiveTaskActivityStructureBean activeTaskactivityStructureBean =
        new ActiveTaskActivityStructureBean();
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
        activeTaskactivityStructureBean =
            this.activeTaskMetadata(studyId, activityId, session, activityVersion, language);
        activeTaskActivityMetaDataResponse.setActivity(activeTaskactivityStructureBean);
        activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: Ends");
    return activeTaskActivityMetaDataResponse;
  }

  /**
   * Get the questionnaire metadata for the provided activity version, study and activity identifier
   *
   * @param studyId the study identifier
   * @param activityId the activity identifier
   * @param activityVersion the activity version
   * @return {@link QuestionnaireActivityMetaDataResponse}
   * @throws DAOException
   * @author BTC
   */
  public QuestionnaireActivityMetaDataResponse studyQuestionnaireActivityMetadata(
      String studyId, String activityId, String activityVersion, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: Starts");
    Session session = null;
    QuestionnaireActivityMetaDataResponse activityMetaDataResponse =
        new QuestionnaireActivityMetaDataResponse();
    QuestionnaireActivityStructureBean activityStructureBean =
        new QuestionnaireActivityStructureBean();
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
        activityStructureBean =
            this.questionnaireMetadata(studyId, activityId, session, activityVersion, language);
        activityMetaDataResponse.setActivity(activityStructureBean);
        activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.info("INFO: ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: Ends");
    return activityMetaDataResponse;
  }

  public ActiveTaskLangBO getActiveTaskLangById(int id, String language) {
    LOGGER.info("StudyActiveTasksDAOImpl - getActiveTaskLangById() - Starts");
    Session session = null;
    ActiveTaskLangBO activeTaskLangBO = null;
    try {
      session = sessionFactory.openSession();
      activeTaskLangBO =
          (ActiveTaskLangBO)
              session
                  .createQuery(
                      "from ActiveTaskLangBO ATLB where ATLB.activeTaskLangPK.id=:id and ATLB.activeTaskLangPK.langCode=:language")
                  .setInteger("id", id)
                  .setString("language", language)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("StudyActiveTasksDAOImpl - getActiveTaskLangById() - ERROR", e);
    } finally {
      if (null != session && session.isOpen()) {
        session.close();
      }
    }
    LOGGER.info("StudyActiveTasksDAOImpl - getActiveTaskLangById() - Ends");
    return activeTaskLangBO;
  }

  public QuestionnaireLangDto getQuestionnairesLangById(int id, String language) {
    LOGGER.info("StudyActiveTasksDAOImpl - getQuestionnairesLangById() - Starts");
    Session session = null;
    QuestionnaireLangDto questionnaireLangDto = null;
    try {
      session = sessionFactory.openSession();
      questionnaireLangDto =
          (QuestionnaireLangDto)
              session
                  .createQuery(
                      "from QuestionnaireLangDto QLD where QLD.questionnaireLangPK.id=:id and QLD.questionnaireLangPK.langCode=:language")
                  .setInteger("id", id)
                  .setString("language", language)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("StudyActiveTasksDAOImpl - getQuestionnairesLangById() - ERROR", e);
    } finally {
      if (null != session && session.isOpen()) {
        session.close();
      }
    }
    LOGGER.info("StudyActiveTasksDAOImpl - getQuestionnairesLangById() - Ends");
    return questionnaireLangDto;
  }
  /**
   * Get active task metadata for the provided study and activity identifier
   *
   * @param studyId the study identifier
   * @param activityId the activity identifier
   * @param session {@link Session}
   * @param activityVersion the activity version
   * @return {@link ActiveTaskActivityStructureBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActiveTaskActivityStructureBean activeTaskMetadata(
      String studyId, String activityId, Session session, String activityVersion, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Starts");
    ActiveTaskActivityStructureBean activeTaskActivityStructureBean =
        new ActiveTaskActivityStructureBean();
    ActiveTaskDto activeTaskDto = null;
    List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();
    ActiveTaskListDto taskDto = null;
    try {

      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO"
                          + " where ATDTO.action=true and ATDTO.customStudyId= :customStudyId"
                          + " and ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version"
                          + " ORDER BY ATDTO.id DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setMaxResults(1)
                  .uniqueResult();
      if (activeTaskDto != null) {

        List<Integer> taskMasterAttrIdList = new ArrayList<>();
        List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList;
        List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;

        activeTaskActivityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);

        ActivityMetadataBean metadata = new ActivityMetadataBean();
        metadata.setActivityId(activeTaskDto.getShortTitle());

        ActivitiesBean activityBean = new ActivitiesBean();
        activityBean =
            this.getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);
        metadata.setStartDate(activityBean.getStartTime());
        metadata.setEndDate(activityBean.getEndTime());
        metadata.setLastModified(
            StringUtils.isEmpty(activeTaskDto.getModifiedDate())
                ? ""
                : StudyMetaDataUtil.getFormattedDateTimeZone(
                    activeTaskDto.getModifiedDate(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        metadata.setName(
            StringUtils.isEmpty(activeTaskDto.getShortTitle())
                ? ""
                : activeTaskDto.getShortTitle());
        metadata.setStudyId(studyId);
        metadata.setVersion(
            activeTaskDto.getVersion() == null
                ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                : activeTaskDto.getVersion().toString());
        if (activeTaskDto.getActive() == null || activeTaskDto.getActive() == 0) {
          metadata.setEndDate(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  activeTaskDto.getModifiedDate(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }
        activeTaskActivityStructureBean.setMetadata(metadata);

        query =
            session.createQuery(
                "from ActiveTaskAttrtibutesValuesDto ATAVDTO"
                    + " where ATAVDTO.activeTaskId= :id"
                    + " and ATAVDTO.activeTaskMasterAttrId in (select ATMADTO.masterId"
                    + " from ActiveTaskMasterAttributeDto ATMADTO"
                    + " where ATMADTO.attributeType= :attributeType) ORDER BY ATAVDTO.activeTaskMasterAttrId");
        query.setInteger("id", activeTaskDto.getId());
        query.setString(
            "attributeType", StudyMetaDataConstants.ACTIVE_TASK_ATTRIBUTE_TYPE_CONFIGURE);
        activeTaskAttrtibuteValuesList = query.list();
        if (activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty()) {

          for (ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList) {
            taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
          }

          if (!taskMasterAttrIdList.isEmpty()) {
            activeTaskMaterList =
                session
                    .createQuery(
                        " from ActiveTaskMasterAttributeDto ATMADTO"
                            + " where ATMADTO.masterId in (:idList)")
                    .setParameterList("idList", taskMasterAttrIdList)
                    .list();

            if (activeTaskMaterList != null && !activeTaskMaterList.isEmpty()) {
              query =
                  session.createQuery(
                      "from ActiveTaskListDto ATDTO where ATDTO.activeTaskListId= :activeTaskListId");
              query.setInteger("activeTaskListId", activeTaskMaterList.get(0).getTaskTypeId());
              taskDto = (ActiveTaskListDto) query.uniqueResult();
            }
          }
        }

        Boolean attributeListFlag =
            activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty();
        Boolean masterAttributeListFlag =
            activeTaskMaterList != null && !activeTaskMaterList.isEmpty();
        Boolean taskListFlag = taskDto != null;
        if (attributeListFlag && masterAttributeListFlag && taskListFlag) {

          ActiveTaskActivityStepsBean activeTaskActiveTaskStep = new ActiveTaskActivityStepsBean();
          FetalKickCounterFormatBean fetalKickCounterFormat = new FetalKickCounterFormatBean();
          SpatialSpanMemoryFormatBean spatialSpanMemoryFormat = new SpatialSpanMemoryFormatBean();
          TowerOfHanoiFormatBean towerOfHanoiFormat = new TowerOfHanoiFormatBean();
          boolean skipLoopFlag = false;
          for (ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList) {
            if (!skipLoopFlag) {
              for (ActiveTaskMasterAttributeDto masterAttributeDto : activeTaskMaterList) {
                if (!skipLoopFlag
                    && attributeDto
                        .getActiveTaskMasterAttrId()
                        .equals(masterAttributeDto.getMasterId())
                    && taskDto.getActiveTaskListId().equals(masterAttributeDto.getTaskTypeId())) {
                  activeTaskActiveTaskStep.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
                  activeTaskActiveTaskStep.setResultType(
                      StringUtils.isEmpty(taskDto.getType()) ? "" : taskDto.getType());
                  activeTaskActiveTaskStep.setKey(activeTaskDto.getShortTitle());

                  if (StringUtils.isNotBlank(language)
                      && !MultiLanguageConstants.ENGLISH.equals(language)) {
                    ActiveTaskLangBO activeTaskLangBO =
                        this.getActiveTaskLangById(activeTaskDto.getId(), language);
                    if (activeTaskLangBO != null) {
                      activeTaskActiveTaskStep.setText(
                          StringUtils.isEmpty(activeTaskLangBO.getInstruction())
                              ? ""
                              : activeTaskLangBO.getInstruction());
                    }
                  } else {
                    activeTaskActiveTaskStep.setText(
                        StringUtils.isEmpty(activeTaskDto.getInstruction())
                            ? ""
                            : activeTaskDto.getInstruction());
                  }

                  switch (taskDto.getType()) {
                    case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
                      fetalKickCounterFormat =
                          (FetalKickCounterFormatBean)
                              this.fetalKickCounterDetails(
                                  attributeDto, masterAttributeDto, fetalKickCounterFormat);
                      activeTaskActiveTaskStep.setFormat(fetalKickCounterFormat);

                      if (attributeDto
                          .getActiveTaskMasterAttrId()
                          .equals(
                              activeTaskMaterList
                                  .get(activeTaskMaterList.size() - 1)
                                  .getMasterId())) {
                        skipLoopFlag = true;
                      }
                      break;
                    case StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY:
                      spatialSpanMemoryFormat =
                          (SpatialSpanMemoryFormatBean)
                              this.spatialSpanMemoryDetails(
                                  attributeDto, masterAttributeDto, spatialSpanMemoryFormat);
                      activeTaskActiveTaskStep.setFormat(spatialSpanMemoryFormat);

                      if (attributeDto
                          .getActiveTaskMasterAttrId()
                          .equals(
                              activeTaskMaterList
                                  .get(activeTaskMaterList.size() - 1)
                                  .getMasterId())) {
                        skipLoopFlag = true;
                      }
                      break;
                    case StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI:
                      towerOfHanoiFormat.setNumberOfDisks(
                          StringUtils.isEmpty(attributeDto.getAttributeVal())
                              ? 0
                              : Integer.parseInt(attributeDto.getAttributeVal()));
                      skipLoopFlag = true;
                      activeTaskActiveTaskStep.setFormat(towerOfHanoiFormat);
                      break;
                    default:
                      break;
                  }
                }
              }
            }
          }

          steps.add(activeTaskActiveTaskStep);
          activeTaskActivityStructureBean.setSteps(steps);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - activeTaskMetadata() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Ends");
    return activeTaskActivityStructureBean;
  }

  /**
   * Get questionnaire metadata for the provided study and activity identifier
   *
   * @param studyId the study identifier
   * @param activityId the activity identifier
   * @param session {@link Session}
   * @param activityVersion the activity version
   * @return {@link QuestionnaireActivityStructureBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public QuestionnaireActivityStructureBean questionnaireMetadata(
      String studyId, String activityId, Session session, String activityVersion, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - questionnaireMetadata() :: Starts");
    QuestionnaireActivityStructureBean activityStructureBean =
        new QuestionnaireActivityStructureBean();
    Map<String, Integer> sequenceNoMap = new HashMap<>();
    Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap = new HashMap<>();
    TreeMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap = new TreeMap<>();
    QuestionnairesDto questionnaireDto = null;
    List<QuestionnairesStepsDto> questionaireStepsList = null;
    List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
    List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList = null;
    try {
      questionnaireDto =
          (QuestionnairesDto)
              session
                  .createQuery(
                      "from QuestionnairesDto QDTO"
                          + " where QDTO.customStudyId= :customStudyId and QDTO.shortTitle= :shortTitle"
                          + " and QDTO.status=true and QDTO.live=1 and ROUND(QDTO.version, 1)= :version ORDER BY QDTO.id DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setMaxResults(1)
                  .uniqueResult();
      if (questionnaireDto != null) {
        activityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);

        ActivityMetadataBean metadata = new ActivityMetadataBean();
        metadata.setActivityId(questionnaireDto.getShortTitle());

        ActivitiesBean activityBean = new ActivitiesBean();
        activityBean =
            this.getTimeDetailsByActivityIdForQuestionnaire(
                questionnaireDto, activityBean, session);

        metadata.setStartDate(activityBean.getStartTime());
        metadata.setEndDate(activityBean.getEndTime());
        metadata.setLastModified(
            StringUtils.isEmpty(questionnaireDto.getModifiedDate())
                ? ""
                : StudyMetaDataUtil.getFormattedDateTimeZone(
                    questionnaireDto.getModifiedDate(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        metadata.setName(
            StringUtils.isEmpty(questionnaireDto.getShortTitle())
                ? ""
                : questionnaireDto.getShortTitle());
        metadata.setStudyId(studyId);
        metadata.setVersion(
            questionnaireDto.getVersion() == null
                ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                : questionnaireDto.getVersion().toString());

        if (!questionnaireDto.getActive()) {
          metadata.setEndDate(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  questionnaireDto.getModifiedDate(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }

        activityStructureBean.setMetadata(metadata);
        query =
            session.createQuery(
                "from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId=:id"
                    + " and QSDTO.status=true ORDER BY QSDTO.sequenceNo");
        query.setInteger("id", questionnaireDto.getId());
        questionaireStepsList = query.list();
        if (questionaireStepsList != null && !questionaireStepsList.isEmpty()) {
          List<Integer> instructionIdList = new ArrayList<>();
          List<Integer> questionIdList = new ArrayList<>();
          List<Integer> formIdList = new ArrayList<>();
          for (int i = 0; i < questionaireStepsList.size(); i++) {
            if (!questionnaireDto.getBranching()) {
              if ((questionaireStepsList.size() - 1) == i) {
                questionaireStepsList.get(i).setDestinationStep(0);
              } else {
                questionaireStepsList
                    .get(i)
                    .setDestinationStep(questionaireStepsList.get(i + 1).getStepId());
              }
            }
          }
          questionaireStepsList = this.getDestinationStepType(questionaireStepsList);
          for (QuestionnairesStepsDto questionnairesStep : questionaireStepsList) {

            switch (questionnairesStep.getStepType()) {
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
                instructionIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                    questionnairesStep);
                break;
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
                questionIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                    questionnairesStep);
                break;
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
                formIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                    questionnairesStep);
                break;
              default:
                break;
            }
          }

          questionResponseTypeMasterInfoList =
              session.createQuery("from QuestionResponsetypeMasterInfoDto").list();

          if (!instructionIdList.isEmpty()) {
            List<InstructionsDto> instructionsDtoList =
                session
                    .createQuery(
                        "from InstructionsDto IDTO where IDTO.id in (:idList) and IDTO.status=true")
                    .setParameterList("idList", instructionIdList)
                    .list();
            if (instructionsDtoList != null && !instructionsDtoList.isEmpty()) {
              stepsSequenceTreeMap =
                  (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                      this.getStepsInfoForQuestionnaires(
                          StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                          instructionsDtoList,
                          null,
                          null,
                          sequenceNoMap,
                          stepsSequenceTreeMap,
                          session,
                          questionnaireStepDetailsMap,
                          null,
                          questionaireStepsList,
                          questionnaireDto,
                          language);
            }
          }

          if (!questionIdList.isEmpty()) {
            List<QuestionsDto> questionsList =
                session
                    .createQuery(
                        " from QuestionsDto QDTO where QDTO.id in (:idList) and QDTO.status=true")
                    .setParameterList("idList", questionIdList)
                    .list();
            if (questionsList != null && !questionsList.isEmpty()) {
              stepsSequenceTreeMap =
                  (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                      this.getStepsInfoForQuestionnaires(
                          StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                          null,
                          questionsList,
                          null,
                          sequenceNoMap,
                          stepsSequenceTreeMap,
                          session,
                          questionnaireStepDetailsMap,
                          questionResponseTypeMasterInfoList,
                          questionaireStepsList,
                          questionnaireDto,
                          language);
            }
          }

          if (!formIdList.isEmpty()) {
            for (Integer formId : formIdList) {
              query =
                  session.createQuery(
                      "from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId"
                          + " from FormDto FDTO where FDTO.formId=:formId)"
                          + " and FMDTO.active=true ORDER BY FMDTO.sequenceNo ");
              List<FormMappingDto> formList = query.setInteger("formId", formId).list();
              if (formList != null && !formList.isEmpty()) {
                stepsSequenceTreeMap =
                    (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                        this.getStepsInfoForQuestionnaires(
                            StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                            null,
                            null,
                            formList,
                            sequenceNoMap,
                            stepsSequenceTreeMap,
                            session,
                            questionnaireStepDetailsMap,
                            questionResponseTypeMasterInfoList,
                            questionaireStepsList,
                            questionnaireDto,
                            language);
              }
            }
          }

          for (Integer key : stepsSequenceTreeMap.keySet()) {
            steps.add(stepsSequenceTreeMap.get(key));
          }

          activityStructureBean.setSteps(steps);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - questionnaireMetadata() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - questionnaireMetadata() :: Ends");
    return activityStructureBean;
  }

  /**
   * Get the active task frequency details for the provided frequency type
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param frequencyDetails {@link ActivityFrequencyBean}
   * @param session {@link Session}
   * @return {@link ActivityFrequencyBean}
   * @throws DAOException
   * @author BTC
   */
  public ActivityFrequencyBean getFrequencyRunsDetailsForActiveTasks(
      ActiveTaskDto activeTask, ActivityFrequencyBean frequencyDetails, Session session)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Starts");
    List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
    List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean = new ArrayList<>();
    try {
      switch (activeTask.getFrequency()) {
        case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
          runDetailsBean =
              this.getActiveTaskFrequencyDetailsForDaily(activeTask, runDetailsBean, session);
          break;
        case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          runDetailsBean =
              this.getActiveTaskFrequencyDetailsForManuallySchedule(
                  activeTask, runDetailsBean, session);
          break;
        default:
          break;
      }
      frequencyDetails.setRuns(runDetailsBean);
      // set AnchorRuns
      /** Phase2a code start * */
      anchorRunDetailsBean =
          this.getAcivetaskFrequencyAncorDetailsForManuallySchedule(
              activeTask, anchorRunDetailsBean, session);
      frequencyDetails.setAnchorRuns(anchorRunDetailsBean);
      /** Phase2a code End * */
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Ends");
    return frequencyDetails;
  }

  /**
   * Get the active task frequency details for one time frequency
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForOneTime(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: Starts");
    try {
      if (activeTask != null) {
        ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
        oneTimeBean.setStartTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                activeTask.getActiveTaskLifetimeStart(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        oneTimeBean.setEndTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                activeTask.getActiveTaskLifetimeEnd(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        runDetailsBean.add(oneTimeBean);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the active task frequency details for daily frequency
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @param session {@link Session}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForDaily(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: Starts");
    try {
      if (activeTask.getScheduleType() != null
          && !activeTask.getScheduleType().isEmpty()
          && activeTask.getScheduleType().equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
        List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList =
            session
                .createQuery(
                    "from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId= :activeTaskId"
                        + " ORDER BY ATFDTO.frequencyTime ")
                .setInteger(StudyMetaDataEnum.QF_ACTIVE_TASK_ID.value(), activeTask.getId())
                .list();
        if (activeTaskDailyFrequencyList != null && !activeTaskDailyFrequencyList.isEmpty()) {
          for (int i = 0; i < activeTaskDailyFrequencyList.size(); i++) {
            ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
            String activeTaskStartTime;
            String activeTaskEndTime;
            activeTaskStartTime = activeTaskDailyFrequencyList.get(i).getFrequencyTime();

            if (i == (activeTaskDailyFrequencyList.size() - 1)) {
              activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
            } else {
              activeTaskEndTime =
                  StudyMetaDataUtil.addSeconds(
                      StudyMetaDataUtil.getCurrentDate()
                          + " "
                          + activeTaskDailyFrequencyList.get(i + 1).getFrequencyTime(),
                      -1);
              activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
            }

            dailyBean.setStartTime(activeTaskStartTime);
            dailyBean.setEndTime(activeTaskEndTime);
            runDetailsBean.add(dailyBean);
          }
        }
      } else {
        if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
            && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())) {
          List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto ATFDTO"
                          + " where ATFDTO.activeTaskId= :activeTaskId ORDER BY ATFDTO.frequencyTime ")
                  .setInteger(StudyMetaDataEnum.QF_ACTIVE_TASK_ID.value(), activeTask.getId())
                  .list();
          if (activeTaskDailyFrequencyList != null && !activeTaskDailyFrequencyList.isEmpty()) {
            for (int i = 0; i < activeTaskDailyFrequencyList.size(); i++) {
              ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
              String activeTaskStartTime;
              String activeTaskEndTime;
              activeTaskStartTime = activeTaskDailyFrequencyList.get(i).getFrequencyTime();

              if (i == (activeTaskDailyFrequencyList.size() - 1)) {
                activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
              } else {
                activeTaskEndTime =
                    StudyMetaDataUtil.addSeconds(
                        StudyMetaDataUtil.getCurrentDate()
                            + " "
                            + activeTaskDailyFrequencyList.get(i + 1).getFrequencyTime(),
                        -1);
                activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
              }

              dailyBean.setStartTime(activeTaskStartTime);
              dailyBean.setEndTime(activeTaskEndTime);
              runDetailsBean.add(dailyBean);
            }
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the active task frequency details for weekly frequency
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForWeekly(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: Starts");
    try {
      if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
          && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())
          && StringUtils.isNotEmpty(activeTask.getDayOfTheWeek())) {
        Integer repeatCount =
            (activeTask.getRepeatActiveTask() == null || activeTask.getRepeatActiveTask() == 0)
                ? 1
                : activeTask.getRepeatActiveTask();
        String activeTaskDay = activeTask.getDayOfTheWeek();
        String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
          String activeTaskEndDate;
          String day = "";
          String weekEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          if (activeTaskDay.equalsIgnoreCase(StudyMetaDataUtil.getDayByDate(activeTaskStartDate))) {
            day = activeTaskDay;
          }

          if (!activeTaskDay.equalsIgnoreCase(day)) {
            while (!activeTaskDay.equalsIgnoreCase(day)) {
              activeTaskStartDate = StudyMetaDataUtil.addDaysToDate(activeTaskStartDate, 1);
              day = StudyMetaDataUtil.getDayByDate(activeTaskStartDate);
            }
          }

          weekEndDate = StudyMetaDataUtil.addWeeksToDate(activeTaskStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))) {
            flag = true;
          }

          if (flag) {
            activeTaskEndDate = weekEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))) {
              activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
              skipLoop = true;
            }

            weeklyBean.setStartTime(activeTaskStartDate);
            weeklyBean.setEndTime(activeTaskEndDate);
            runDetailsBean.add(weeklyBean);

            if (skipLoop) {
              break;
            }
          }

          activeTaskStartDate = weekEndDate;
          activeTaskDay = day;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the active task frequency details for monthly frequency
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForMonthly(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: Starts");
    try {
      if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
          && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())) {
        Integer repeatCount =
            (activeTask.getRepeatActiveTask() == null || activeTask.getRepeatActiveTask() == 0)
                ? 1
                : activeTask.getRepeatActiveTask();
        String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
          String activeTaskEndDate;
          String monthEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          monthEndDate = StudyMetaDataUtil.addMonthsToDate(activeTaskStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))) {
            flag = true;
          }

          if (flag) {
            activeTaskEndDate = monthEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))) {
              activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
              skipLoop = true;
            }

            monthlyBean.setStartTime(activeTaskStartDate);
            monthlyBean.setEndTime(activeTaskEndDate);
            runDetailsBean.add(monthlyBean);

            if (skipLoop) {
              break;
            }
          }

          activeTaskStartDate = monthEndDate;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the active task frequency details for manually schedule frequency
   *
   * @param activeTask {@link ActiveTaskDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @param session {@link Session}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForManuallySchedule(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: Starts");
    try {
      List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from ActiveTaskCustomFrequenciesDto ATCFDTO"
                      + " where ATCFDTO.activeTaskId="
                      + activeTask.getId())
              .list();
      if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
        for (ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList) {
          ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
          String startDate =
              customFrequencyDto.getFrequencyStartDate()
                  + " "
                  + customFrequencyDto.getFrequencyTime();
          String endDate =
              customFrequencyDto.getFrequencyEndDate()
                  + " "
                  + customFrequencyDto.getFrequencyTime();
          if (StringUtils.isNotEmpty(customFrequencyDto.getFrequencyStartDate())) {
            manuallyScheduleBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    startDate,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          if (StringUtils.isNotEmpty(customFrequencyDto.getFrequencyEndDate())) {
            manuallyScheduleBean.setEndTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    endDate,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          runDetailsBean.add(manuallyScheduleBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: ERROR", e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire frequency details for the provided frequency type
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param frequencyDetails {@link ActivityFrequencyBean}
   * @param session {@link Session}
   * @return {@link ActivityFrequencyBean}
   * @throws DAOException
   * @author BTC
   */
  public ActivityFrequencyBean getFrequencyRunsDetailsForQuestionaires(
      QuestionnairesDto questionaire, ActivityFrequencyBean frequencyDetails, Session session)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Starts");
    List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
    List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean = new ArrayList<>();
    try {
      switch (questionaire.getFrequency()) {
        case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
          runDetailsBean =
              this.getQuestionnaireFrequencyDetailsForDaily(questionaire, runDetailsBean, session);
          break;
        case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          runDetailsBean =
              this.getQuestionnaireFrequencyDetailsForManuallySchedule(
                  questionaire, runDetailsBean, session);
          /** Phase2a code start * */
          anchorRunDetailsBean =
              this.getQuestionnaireFrequencyAncorDetailsForManuallySchedule(
                  questionaire, anchorRunDetailsBean, session);
          frequencyDetails.setAnchorRuns(anchorRunDetailsBean);
          /** Phase2a code End * */
          break;
        default:
          break;
      }
      frequencyDetails.setRuns(runDetailsBean);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Ends");
    return frequencyDetails;
  }

  /**
   * Get the questionnaire frequency details for one time frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForOneTime(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: Starts");
    try {
      if (questionaire != null) {
        ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
        oneTimeBean.setStartTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                questionaire.getStudyLifetimeStart(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        oneTimeBean.setEndTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                questionaire.getStudyLifetimeEnd(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        runDetailsBean.add(oneTimeBean);
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire frequency details for daily frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @param session {@link Session}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForDaily(
      QuestionnairesDto questionaire,
      List<ActivityFrequencyScheduleBean> runDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: Starts");
    List<QuestionnairesFrequenciesDto> dailyFrequencyList = null;
    try {
      if (questionaire.getScheduleType() != null
          && !questionaire.getScheduleType().isEmpty()
          && questionaire
              .getScheduleType()
              .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
        dailyFrequencyList =
            session
                .createQuery(
                    "from QuestionnairesFrequenciesDto QFDTO"
                        + " where QFDTO.questionnairesId= :questionnairesId ORDER BY QFDTO.frequencyTime")
                .setInteger(StudyMetaDataEnum.QF_QUESTIONNAIRE_ID.value(), questionaire.getId())
                .list();
        if (dailyFrequencyList != null && !dailyFrequencyList.isEmpty()) {
          for (int i = 0; i < dailyFrequencyList.size(); i++) {
            ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
            String activeTaskStartTime;
            String activeTaskEndTime;
            activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();

            if (i == (dailyFrequencyList.size() - 1)) {
              activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
            } else {
              activeTaskEndTime =
                  StudyMetaDataUtil.addSeconds(
                      StudyMetaDataUtil.getCurrentDate()
                          + " "
                          + dailyFrequencyList.get(i + 1).getFrequencyTime(),
                      -1);
              activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
            }

            dailyBean.setStartTime(activeTaskStartTime);
            dailyBean.setEndTime(activeTaskEndTime);
            runDetailsBean.add(dailyBean);
          }
        }
      } else {
        if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
            && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())) {
          dailyFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QFDTO"
                          + " where QFDTO.questionnairesId= :questionnairesId ORDER BY QFDTO.frequencyTime")
                  .setInteger(StudyMetaDataEnum.QF_QUESTIONNAIRE_ID.value(), questionaire.getId())
                  .list();
          if (dailyFrequencyList != null && !dailyFrequencyList.isEmpty()) {
            for (int i = 0; i < dailyFrequencyList.size(); i++) {
              ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
              String activeTaskStartTime;
              String activeTaskEndTime;
              activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();

              if (i == (dailyFrequencyList.size() - 1)) {
                activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
              } else {
                activeTaskEndTime =
                    StudyMetaDataUtil.addSeconds(
                        StudyMetaDataUtil.getCurrentDate()
                            + " "
                            + dailyFrequencyList.get(i + 1).getFrequencyTime(),
                        -1);
                activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
              }

              dailyBean.setStartTime(activeTaskStartTime);
              dailyBean.setEndTime(activeTaskEndTime);
              runDetailsBean.add(dailyBean);
            }
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire frequency details for weekly frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForWeekly(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: Starts");
    try {
      if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
          && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())
          && StringUtils.isNotEmpty(questionaire.getDayOfTheWeek())) {
        Integer repeatCount =
            (questionaire.getRepeatQuestionnaire() == null
                    || questionaire.getRepeatQuestionnaire() == 0)
                ? 1
                : questionaire.getRepeatQuestionnaire();
        String questionaireDay = questionaire.getDayOfTheWeek();
        String questionaireStartDate = questionaire.getStudyLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
          String questionaireEndDate;
          String day = "";
          String weekEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          if (questionaireDay.equalsIgnoreCase(
              StudyMetaDataUtil.getDayByDate(questionaireStartDate))) {
            day = questionaireDay;
          }

          if (!questionaireDay.equalsIgnoreCase(day)) {
            while (!questionaireDay.equalsIgnoreCase(day)) {
              questionaireStartDate = StudyMetaDataUtil.addDaysToDate(questionaireStartDate, 1);
              day = StudyMetaDataUtil.getDayByDate(questionaireStartDate);
            }
          }

          weekEndDate = StudyMetaDataUtil.addWeeksToDate(questionaireStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))) {
            flag = true;
          }

          if (flag) {
            questionaireEndDate = weekEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            questionaire.getStudyLifetimeEnd())))) {
              questionaireEndDate = questionaire.getStudyLifetimeEnd();
              skipLoop = true;
            }

            weeklyBean.setStartTime(questionaireStartDate);
            weeklyBean.setEndTime(questionaireEndDate);
            runDetailsBean.add(weeklyBean);

            if (skipLoop) {
              break;
            }
          }

          questionaireStartDate = weekEndDate;
          questionaireDay = day;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire frequency details for monthly frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForMonthly(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: Starts");
    try {
      if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
          && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())) {
        Integer repeatCount =
            (questionaire.getRepeatQuestionnaire() == null
                    || questionaire.getRepeatQuestionnaire() == 0)
                ? 1
                : questionaire.getRepeatQuestionnaire();
        String questionaireStartDate = questionaire.getStudyLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
          String questionaireEndDate;
          String monthEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          monthEndDate = StudyMetaDataUtil.addMonthsToDate(questionaireStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))) {
            flag = true;
          }

          if (flag) {
            questionaireEndDate = monthEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            questionaire.getStudyLifetimeEnd())))) {
              questionaireEndDate = questionaire.getStudyLifetimeEnd();
              skipLoop = true;
            }
            monthlyBean.setStartTime(questionaireStartDate);
            monthlyBean.setEndTime(questionaireEndDate);
            runDetailsBean.add(monthlyBean);

            if (skipLoop) {
              break;
            }
          }

          questionaireStartDate = monthEndDate;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire frequency details for manually schedule frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param runDetailsBean {@link List<ActivityFrequencyScheduleBean>}
   * @param session {@link Session}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForManuallySchedule(
      QuestionnairesDto questionaire,
      List<ActivityFrequencyScheduleBean> runDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: Starts");
    try {

      query =
          session.createQuery(
              "from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId=:id");
      query.setInteger("id", questionaire.getId());
      List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList = query.list();
      if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
        for (QuestionnairesCustomFrequenciesDto customFrequencyDto :
            manuallyScheduleFrequencyList) {
          ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
          if (StringUtils.isNotEmpty(customFrequencyDto.getFrequencyEndDate())) {
            manuallyScheduleBean.setEndTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    customFrequencyDto.getFrequencyEndDate()
                        + " "
                        + customFrequencyDto.getFrequencyTime(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          if (StringUtils.isNotEmpty(customFrequencyDto.getFrequencyStartDate())) {
            manuallyScheduleBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    customFrequencyDto.getFrequencyStartDate()
                        + " "
                        + customFrequencyDto.getFrequencyTime(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          runDetailsBean.add(manuallyScheduleBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: Ends");
    return runDetailsBean;
  }

  /**
   * Get the questionnaire step details for the provided type
   *
   * @param type the questionnaire step type
   * @param instructionsDtoList {@link List<InstructionsDto>}
   * @param questionsDtoList {@link List<QuestionsDto>}
   * @param formsList {@link List<FormMappingDto>}
   * @param sequenceNoMap {@link Map<String, Integer>}
   * @param stepsSequenceTreeMap {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @param session {@link Session}
   * @param questionnaireStepDetailsMap {@link Map<String, QuestionnairesStepsDto>}
   * @param questionResponseTypeMasterInfoList
   * @param questionaireStepsList {@link List<QuestionResponsetypeMasterInfoDto>}
   * @param questionnaireDto {@link QuestionnairesDto}
   * @return {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @throws DAOException
   * @author BTC
   */
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getStepsInfoForQuestionnaires(
      String type,
      List<InstructionsDto> instructionsDtoList,
      List<QuestionsDto> questionsDtoList,
      List<FormMappingDto> formsList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Session session,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      List<QuestionnairesStepsDto> questionaireStepsList,
      QuestionnairesDto questionnaireDto,
      String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: Starts");
    TreeMap<Integer, QuestionnaireActivityStepsBean> stepsOrderSequenceTreeMap = new TreeMap<>();
    try {
      switch (type) {
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getInstructionDetailsForQuestionnaire(
                      instructionsDtoList,
                      sequenceNoMap,
                      stepsSequenceTreeMap,
                      questionnaireStepDetailsMap,
                      language);
          break;
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getQuestionDetailsForQuestionnaire(
                      questionsDtoList,
                      sequenceNoMap,
                      stepsSequenceTreeMap,
                      session,
                      questionnaireStepDetailsMap,
                      questionResponseTypeMasterInfoList,
                      questionaireStepsList,
                      questionnaireDto,
                      language);
          break;
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getFormDetailsForQuestionnaire(
                      formsList,
                      sequenceNoMap,
                      session,
                      stepsSequenceTreeMap,
                      questionnaireStepDetailsMap,
                      questionResponseTypeMasterInfoList,
                      language);
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: Ends");
    return stepsOrderSequenceTreeMap;
  }

  /**
   * Get instruction metadata
   *
   * @param instructionsDtoList {@link List<InstructionsDto>}
   * @param sequenceNoMap {@link Map<String, Integer>}
   * @param stepsSequenceTreeMap {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @param questionnaireStepDetailsMap {@link Map<String, QuestionnairesStepsDto>}
   * @return {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @throws DAOException
   * @author BTC
   */
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getInstructionDetailsForQuestionnaire(
      List<InstructionsDto> instructionsDtoList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: Starts");
    try {
      if (instructionsDtoList != null && !instructionsDtoList.isEmpty()) {
        for (InstructionsDto instructionsDto : instructionsDtoList) {
          QuestionnairesStepsDto instructionStepDetails =
              questionnaireStepDetailsMap.get(
                  (instructionsDto.getId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)
                      .toString());
          QuestionnaireActivityStepsBean instructionBean = new QuestionnaireActivityStepsBean();

          instructionBean.setType(
              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
          instructionBean.setResultType("");
          instructionBean.setKey(
              StringUtils.isEmpty(instructionStepDetails.getStepShortTitle())
                  ? ""
                  : instructionStepDetails.getStepShortTitle());

          if (StringUtils.isNotBlank(language) && !"en".equals(language)) {
            InstructionsLangBO instructionsLangBO =
                this.getInstructionLangBo(instructionsDto.getId(), language);
            if (instructionsLangBO != null) {
              instructionBean.setTitle(
                  StringUtils.isEmpty(instructionsLangBO.getInstructionTitle())
                      ? ""
                      : instructionsLangBO.getInstructionTitle());

              instructionBean.setText(
                  StringUtils.isEmpty(instructionsLangBO.getInstructionText())
                      ? ""
                      : instructionsLangBO.getInstructionText());
            }
          } else {
            instructionBean.setTitle(
                StringUtils.isEmpty(instructionsDto.getInstructionTitle())
                    ? ""
                    : instructionsDto.getInstructionTitle());

            instructionBean.setText(
                StringUtils.isEmpty(instructionsDto.getInstructionText())
                    ? ""
                    : instructionsDto.getInstructionText());
          }
          instructionBean.setSkippable(
              (StringUtils.isEmpty(instructionStepDetails.getSkiappable())
                      || instructionStepDetails
                          .getSkiappable()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          instructionBean.setGroupName("");
          instructionBean.setRepeatable(false);
          instructionBean.setRepeatableText(
              instructionStepDetails.getRepeatableText() == null
                  ? ""
                  : instructionStepDetails.getRepeatableText());

          List<DestinationBean> destinations = new ArrayList<>();
          DestinationBean dest = new DestinationBean();
          dest.setCondition("");
          dest.setDestination(
              (instructionStepDetails.getDestinationStepType() == null
                      || instructionStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : instructionStepDetails.getDestinationStepType());
          destinations.add(dest);
          instructionBean.setDestinations(destinations);

          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (instructionsDto.getId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)
                      .toString()),
              instructionBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  /**
   * Get question metadata
   *
   * @param questionsDtoList {@link List<QuestionsDto>}
   * @param sequenceNoMap {@link Map<String, Integer>}
   * @param stepsSequenceTreeMap {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @param session {@link Session}
   * @param questionnaireStepDetailsMap {@link Map<String, QuestionnairesStepsDto>}
   * @param questionResponseTypeMasterInfoList {@link List<QuestionResponsetypeMasterInfoDto>}
   * @param questionaireStepsList {@link List<QuestionnairesStepsDto>}
   * @param questionnaireDto {@link QuestionnairesDto}
   * @return {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getQuestionDetailsForQuestionnaire(
      List<QuestionsDto> questionsDtoList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Session session,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      List<QuestionnairesStepsDto> questionaireStepsList,
      QuestionnairesDto questionnaireDto,
      String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: Starts");
    List<QuestionResponseSubTypeDto> destinationConditionList = null;
    Transaction transaction = null;
    try {
      if (questionsDtoList != null && !questionsDtoList.isEmpty()) {
        for (QuestionsDto questionsDto : questionsDtoList) {
          QuestionnairesStepsDto questionStepDetails =
              questionnaireStepDetailsMap.get(
                  (questionsDto.getId() + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                      .toString());
          QuestionnaireActivityStepsBean questionBean = new QuestionnaireActivityStepsBean();

          questionBean.setType(
              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
          if (questionsDto.getResponseType() != null) {
            for (QuestionResponsetypeMasterInfoDto masterInfo :
                questionResponseTypeMasterInfoList) {
              if (masterInfo.getId().equals(questionsDto.getResponseType())) {
                questionBean.setResultType(masterInfo.getResponseTypeCode());
                questionBean.setFormat(
                    this.getQuestionaireQuestionFormatByType(
                        questionsDto, masterInfo.getResponseTypeCode(), session, language));
                break;
              }
            }
          } else {
            questionBean.setResultType("");
          }

          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            QuestionLangBO questionLangBO = this.getQuestionLangBo(questionsDto.getId(), language);
            if (questionLangBO != null) {
              questionBean.setText(
                  StringUtils.isEmpty(questionLangBO.getDescription())
                      ? ""
                      : questionLangBO.getDescription());
              questionBean.setTitle(
                  StringUtils.isEmpty(questionLangBO.getQuestion())
                      ? ""
                      : questionLangBO.getQuestion());
            }
          } else {
            questionBean.setText(
                StringUtils.isEmpty(questionsDto.getDescription())
                    ? ""
                    : questionsDto.getDescription());
            questionBean.setTitle(
                StringUtils.isEmpty(questionsDto.getQuestion()) ? "" : questionsDto.getQuestion());
          }

          questionBean.setKey(
              StringUtils.isEmpty(questionStepDetails.getStepShortTitle())
                  ? ""
                  : questionStepDetails.getStepShortTitle());
          questionBean.setSkippable(
              !StringUtils.isEmpty(questionStepDetails.getSkiappable())
                  && !questionStepDetails
                  .getSkiappable()
                  .equalsIgnoreCase(StudyMetaDataConstants.NO));
          questionBean.setGroupName("");
          questionBean.setRepeatable(false);
          questionBean.setRepeatableText(
              questionStepDetails.getRepeatableText() == null
                  ? ""
                  : questionStepDetails.getRepeatableText());

          List<DestinationBean> destinationsList = new ArrayList<>();

          // Choice based branching allowed only for textchoice,
          // textscale, imagechoice, boolean, valuePicker response types
          /* if (!questionsDto.getResponseType().equals(4)) { */
          if (questionsDto.getResponseType() != null) {
            destinationConditionList =
                session
                    .createQuery(
                        "from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId= :responseTypeId")
                    .setInteger(StudyMetaDataEnum.QF_RESPONSE_TYPE_ID.value(), questionsDto.getId())
                    .list();
            if (destinationConditionList != null && !destinationConditionList.isEmpty()) {
              for (QuestionResponseSubTypeDto destinationDto : destinationConditionList) {
                DestinationBean destination = new DestinationBean();
                if (questionBean
                    .getResultType()
                    .equalsIgnoreCase(StudyMetaDataConstants.QUESTION_BOOLEAN)) {
                  destination.setCondition(
                      StringUtils.isEmpty(destinationDto.getValue())
                          ? ""
                          : destinationDto.getValue().toLowerCase());
                } else {
                  destination.setCondition(
                      StringUtils.isEmpty(destinationDto.getValue())
                          ? ""
                          : destinationDto.getValue());
                }

                if (questionnaireDto.getBranching()) {
                  if (destinationDto.getDestinationStepId() != null
                      && destinationDto.getDestinationStepId().intValue() > 0) {
                    destination =
                        this.getDestinationStepTypeForResponseSubType(
                            destination, destinationDto, questionaireStepsList);
                  } else if (destinationDto.getDestinationStepId() != null
                      && destinationDto.getDestinationStepId().equals(0)) {
                    destination.setDestination("");
                  } else {
                    destination.setDestination(
                        (questionStepDetails.getDestinationStepType() == null
                                || questionStepDetails.getDestinationStepType().isEmpty())
                            ? ""
                            : questionStepDetails.getDestinationStepType());
                  }
                } else {
                  destination.setDestination(
                      (questionStepDetails.getDestinationStepType() == null
                              || questionStepDetails.getDestinationStepType().isEmpty())
                          ? ""
                          : questionStepDetails.getDestinationStepType());
                }
                destinationsList.add(destination);
              }
            }
          }

          if (Arrays.asList(StudyMetaDataConstants.CB_RESPONSE_TYPE.split(","))
                  .contains(questionBean.getResultType())
              && questionnaireDto.getBranching()) {
            QuestionReponseTypeDto reponseType =
                (QuestionReponseTypeDto)
                    session
                        .createQuery(
                            "from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId=:id"
                                + " ORDER BY QRTDTO.responseTypeId DESC")
                        .setInteger("id", questionsDto.getId())
                        .setMaxResults(1)
                        .uniqueResult();
            if (reponseType != null
                && StringUtils.isNotEmpty(reponseType.getFormulaBasedLogic())
                && reponseType
                    .getFormulaBasedLogic()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              boolean isValueOfXSaved = false;
              if (destinationConditionList != null
                  && !destinationConditionList.isEmpty()
                  && destinationConditionList.size() == 2) {
                if (StringUtils.isNotEmpty(destinationConditionList.get(0).getValueOfX())
                    && StringUtils.isNotEmpty(destinationConditionList.get(1).getValueOfX())
                    && StringUtils.isNotEmpty(destinationConditionList.get(0).getOperator())
                    && StringUtils.isNotEmpty(destinationConditionList.get(1).getOperator())) {
                  isValueOfXSaved = true;
                  for (int i = 0; i < destinationConditionList.size(); i++) {
                    destinationsList
                        .get(i)
                        .setCondition(
                            StringUtils.isEmpty(destinationConditionList.get(i).getValueOfX())
                                ? ""
                                : destinationConditionList.get(i).getValueOfX());
                    destinationsList
                        .get(i)
                        .setOperator(
                            StringUtils.isEmpty(destinationConditionList.get(i).getOperator())
                                ? ""
                                : destinationConditionList.get(i).getOperator());
                  }
                }
              }

              if (!isValueOfXSaved) {
                destinationsList =
                    this.getConditionalBranchingDestinations(
                        reponseType, destinationsList, questionBean);

                transaction = session.beginTransaction();
                for (int i = 0; i < destinationsList.size(); i++) {
                  QuestionResponseSubTypeDto destinationDto = destinationConditionList.get(i);
                  destinationDto.setValueOfX(destinationsList.get(i).getCondition());
                  destinationDto.setOperator(destinationsList.get(i).getOperator());
                  session.save(destinationDto);
                }
                transaction.commit();
              }
            }
          }

          DestinationBean destination = new DestinationBean();
          destination.setCondition("");
          destination.setDestination(
              (questionStepDetails.getDestinationStepType() == null
                      || questionStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : questionStepDetails.getDestinationStepType());
          destinationsList.add(destination);

          // other type add destination if there start
          QuestionReponseTypeDto otherReponseSubType =
              (QuestionReponseTypeDto)
                  session
                      .createQuery(
                          "from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId=:id ORDER BY QRTDTO.responseTypeId DESC")
                      .setInteger("id", questionsDto.getId())
                      .setMaxResults(1)
                      .uniqueResult();

          if (otherReponseSubType != null
              && otherReponseSubType.getOtherType() != null
              && StringUtils.isNotEmpty(otherReponseSubType.getOtherType())
              && otherReponseSubType.getOtherType().equals("on")) {
            DestinationBean otherDestination = new DestinationBean();
            otherDestination.setCondition(
                StringUtils.isEmpty(otherReponseSubType.getOtherValue())
                    ? ""
                    : otherReponseSubType.getOtherValue());

            if (questionnaireDto.getBranching()) {
              if (otherReponseSubType.getOtherDestinationStepId() != null
                  && otherReponseSubType.getOtherDestinationStepId().intValue() > 0) {
                for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
                  if (otherReponseSubType
                      .getOtherDestinationStepId()
                      .equals(stepsDto.getStepId())) {
                    otherDestination.setDestination(
                        StringUtils.isEmpty(stepsDto.getStepShortTitle())
                            ? ""
                            : stepsDto.getStepShortTitle());
                    break;
                  }
                }
              } else if (otherReponseSubType.getOtherDestinationStepId() != null
                  && otherReponseSubType.getOtherDestinationStepId().equals(0)) {
                otherDestination.setDestination("");
              } else {
                otherDestination.setDestination(
                    (questionStepDetails.getDestinationStepType() == null
                            || questionStepDetails.getDestinationStepType().isEmpty())
                        ? ""
                        : questionStepDetails.getDestinationStepType());
              }
            } else {
              otherDestination.setDestination(
                  (questionStepDetails.getDestinationStepType() == null
                          || questionStepDetails.getDestinationStepType().isEmpty())
                      ? ""
                      : questionStepDetails.getDestinationStepType());
            }
            destinationsList.add(otherDestination);
          }
          // other type add destination if there end
          questionBean.setDestinations(destinationsList);

          questionBean.setHealthDataKey("");
          if (StringUtils.isNotEmpty(questionsDto.getAllowHealthKit())
              && StudyMetaDataConstants.YES.equalsIgnoreCase(questionsDto.getAllowHealthKit())
              && StringUtils.isNotEmpty(questionsDto.getHealthkitDatatype())) {
            questionBean.setHealthDataKey(questionsDto.getHealthkitDatatype().trim());
          }
          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (questionsDto.getId() + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                      .toString()),
              questionBean);
        }
      }
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      LOGGER.error("ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  /**
   * Get form metadata
   *
   * @param formsList {@link List<FormMappingDto>}
   * @param sequenceNoMap {@link Map<String, Integer>}
   * @param session {@link Session}
   * @param stepsSequenceTreeMap {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @param questionnaireStepDetailsMap {@link Map<String, QuestionnairesStepsDto>}
   * @param questionResponseTypeMasterInfoList {@link List<QuestionResponsetypeMasterInfoDto>}
   * @return {@link SortedMap<Integer, QuestionnaireActivityStepsBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getFormDetailsForQuestionnaire(
      List<FormMappingDto> formsList,
      Map<String, Integer> sequenceNoMap,
      Session session,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: Starts");
    try {
      if (formsList != null && !formsList.isEmpty()) {
        List<Integer> formQuestionIdsList = new ArrayList<>();
        TreeMap<Integer, Integer> formQuestionMap = new TreeMap<>();
        for (FormMappingDto formDto : formsList) {
          formQuestionIdsList.add(formDto.getQuestionId());
          formQuestionMap.put(formDto.getSequenceNo(), formDto.getQuestionId());
        }

        if (!formQuestionIdsList.isEmpty()) {
          QuestionnairesStepsDto formStepDetails =
              questionnaireStepDetailsMap.get(
                  (formsList.get(0).getFormId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                      .toString());
          QuestionnaireActivityStepsBean formBean = new QuestionnaireActivityStepsBean();
          List<QuestionnaireActivityStepsBean> formSteps = new ArrayList<>();
          HashMap<Integer, QuestionnaireActivityStepsBean> formStepsMap = new HashMap<>();

          formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
          formBean.setResultType(StudyMetaDataConstants.RESULT_TYPE_GROUPED);
          formBean.setKey(
              StringUtils.isEmpty(formStepDetails.getStepShortTitle())
                  ? ""
                  : formStepDetails.getStepShortTitle());
          formBean.setTitle("");
          formBean.setText("");
          formBean.setSkippable(
              !StringUtils.isEmpty(formStepDetails.getSkiappable())
                  && !formStepDetails
                  .getSkiappable()
                  .equalsIgnoreCase(StudyMetaDataConstants.NO));
          formBean.setGroupName("");
          formBean.setRepeatable(
              formStepDetails.getRepeatable() != null
                  && !StudyMetaDataConstants.NO.equalsIgnoreCase(
                  formStepDetails.getRepeatable()));

          if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
            FormLangBO formLangBO = this.getFormLangBo(formStepDetails.getInstructionFormId(), language);
            if (formLangBO!=null) {
              formBean.setRepeatableText(
                  formLangBO.getRepeatableText() == null ? "" : formLangBO.getRepeatableText());
            }
          } else {
            formBean.setRepeatableText(
                formStepDetails.getRepeatableText() == null
                    ? ""
                    : formStepDetails.getRepeatableText());
          }

          List<DestinationBean> destinations = new ArrayList<>();
          DestinationBean dest = new DestinationBean();
          dest.setCondition("");
          dest.setDestination(
              (formStepDetails.getDestinationStepType() == null
                      || formStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : formStepDetails.getDestinationStepType());
          destinations.add(dest);
          formBean.setDestinations(destinations);

          List<QuestionsDto> formQuestionsList;
          formQuestionsList =
              session
                  .createQuery("from QuestionsDto QDTO where QDTO.id in (:idList)")
                  .setParameterList("idList", formQuestionIdsList)
                  .list();
          if (formQuestionsList != null && !formQuestionsList.isEmpty()) {
            for (QuestionsDto formQuestionDto : formQuestionsList) {
              QuestionnaireActivityStepsBean formQuestionBean =
                  new QuestionnaireActivityStepsBean();
              formQuestionBean.setType(
                  StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
              if (formQuestionDto.getResponseType() != null) {
                for (QuestionResponsetypeMasterInfoDto masterInfo :
                    questionResponseTypeMasterInfoList) {
                  if (masterInfo.getId().equals(formQuestionDto.getResponseType())) {
                    formQuestionBean.setResultType(masterInfo.getResponseTypeCode());
                    formQuestionBean.setFormat(
                        this.getQuestionaireQuestionFormatByType(
                            formQuestionDto, masterInfo.getResponseTypeCode(), session, language));
                    break;
                  }
                }
              } else {
                formQuestionBean.setResultType("");
              }
              formQuestionBean.setKey(
                  StringUtils.isEmpty(formQuestionDto.getShortTitle())
                      ? ""
                      : formQuestionDto.getShortTitle());
              if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
                QuestionLangBO questionLangBO = this.getQuestionLangBo(formQuestionDto.getId(), language);
                formQuestionBean.setTitle(
                    StringUtils.isEmpty(questionLangBO.getQuestion())
                        ? ""
                        : questionLangBO.getQuestion());
                formQuestionBean.setText(
                    StringUtils.isEmpty(questionLangBO.getDescription())
                        ? ""
                        : questionLangBO.getDescription());
              } else {
                formQuestionBean.setTitle(
                    StringUtils.isEmpty(formQuestionDto.getQuestion())
                        ? ""
                        : formQuestionDto.getQuestion());
                formQuestionBean.setText(
                    StringUtils.isEmpty(formQuestionDto.getDescription())
                        ? ""
                        : formQuestionDto.getDescription());
              }
              formQuestionBean.setSkippable(
                  !StringUtils.isEmpty(formQuestionDto.getSkippable())
                      && !formQuestionDto
                      .getSkippable()
                      .equalsIgnoreCase(StudyMetaDataConstants.NO));
              formQuestionBean.setGroupName("");
              formQuestionBean.setRepeatable(false);
              formQuestionBean.setRepeatableText("");
              formQuestionBean.setHealthDataKey("");

              if (StringUtils.isNotEmpty(formQuestionDto.getAllowHealthKit())
                  && StudyMetaDataConstants.YES.equalsIgnoreCase(
                      formQuestionDto.getAllowHealthKit())
                  && StringUtils.isNotEmpty(formQuestionDto.getHealthkitDatatype())) {
                formQuestionBean.setHealthDataKey(formQuestionDto.getHealthkitDatatype().trim());
              }

              formStepsMap.put(formQuestionDto.getId(), formQuestionBean);
            }
          }

          for (Integer key : formQuestionMap.keySet()) {
            formSteps.add(formStepsMap.get(formQuestionMap.get(key)));
          }
          formBean.setSteps(formSteps);

          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (formsList.get(0).getFormId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                      .toString()),
              formBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  /**
   * Get the fetal kick counter metadata
   *
   * @param attributeValues {@link ActiveTaskAttrtibutesValuesDto}
   * @param masterAttributeValue {@link ActiveTaskMasterAttributeDto}
   * @param fetalKickCounterFormat {@link FetalKickCounterFormatBean}
   * @return {@link FetalKickCounterFormatBean}
   * @throws DAOException
   * @author BTC
   */
  public Object fetalKickCounterDetails(
      ActiveTaskAttrtibutesValuesDto attributeValues,
      ActiveTaskMasterAttributeDto masterAttributeValue,
      FetalKickCounterFormatBean fetalKickCounterFormat)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - fetalKickCounterDetails() :: Starts");
    try {
      if (masterAttributeValue.getOrderByTaskType().equals(1)) {
        if (StringUtils.isNotEmpty(attributeValues.getAttributeVal())) {
          if (attributeValues
              .getAttributeVal()
              .equals(StudyMetaDataConstants.FETAL_MAX_DURATION_WCP)) {
            attributeValues.setAttributeVal(StudyMetaDataConstants.FETAL_MAX_DURATION);
          } else {
            String[] durationArray = attributeValues.getAttributeVal().split(":");
            fetalKickCounterFormat.setDuration(
                (Integer.parseInt(durationArray[0]) * 3600)
                    + (Integer.parseInt(durationArray[1]) * 60));
          }
        } else {
          attributeValues.setAttributeVal(StudyMetaDataConstants.FETAL_MAX_DURATION);
        }
      } else {
        fetalKickCounterFormat.setKickCount(
            (StringUtils.isEmpty(attributeValues.getAttributeVal())
                    || "0".equals(attributeValues.getAttributeVal()))
                ? StudyMetaDataConstants.MAX_KICK_COUNT
                : Integer.parseInt(attributeValues.getAttributeVal()));
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - fetalKickCounterDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - fetalKickCounterDetails() :: Ends");
    return fetalKickCounterFormat;
  }

  /**
   * Get the spatial span memory metadata
   *
   * @param attributeValues {@link ActiveTaskAttrtibutesValuesDto}
   * @param masterAttributeValue {@link ActiveTaskMasterAttributeDto}
   * @param spatialSpanMemoryFormat {@link SpatialSpanMemoryFormatBean}
   * @return {@link SpatialSpanMemoryFormatBean}
   * @throws DAOException
   * @author BTC
   */
  public Object spatialSpanMemoryDetails(
      ActiveTaskAttrtibutesValuesDto attributeValues,
      ActiveTaskMasterAttributeDto masterAttributeValue,
      SpatialSpanMemoryFormatBean spatialSpanMemoryFormat)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Starts");
    try {
      switch (masterAttributeValue.getAttributeName().trim()) {
        case StudyMetaDataConstants.SSM_INITIAL:
          spatialSpanMemoryFormat.setInitialSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MINIMUM:
          spatialSpanMemoryFormat.setMinimumSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAXIMUM:
          spatialSpanMemoryFormat.setMaximumSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_PLAY_SPEED:
          spatialSpanMemoryFormat.setPlaySpeed(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0f
                  : Float.parseFloat(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAX_TEST:
          spatialSpanMemoryFormat.setMaximumTests(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAX_CONSECUTIVE_FAILURES:
          spatialSpanMemoryFormat.setMaximumConsecutiveFailures(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_REQUIRE_REVERSAL:
          spatialSpanMemoryFormat.setRequireReversal(
              StringUtils.isNotEmpty(attributeValues.getAttributeVal())
                  && attributeValues
                      .getAttributeVal()
                      .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y));
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - spatialSpanMemoryDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Ends");
    return spatialSpanMemoryFormat;
  }

  /**
   * Get the active task options
   *
   * @return {@link String[]}
   * @author BTC
   */
  public String[] activeTaskOptions() {
    LOGGER.info("INFO: ActivityMetaDataDao - activeTaskOptions() :: Starts");
    String[] activeTaskOptionsArray = new String[8];
    try {
      activeTaskOptionsArray[0] = "excludeInstructions";
      activeTaskOptionsArray[1] = "excludeConclusion";
      activeTaskOptionsArray[2] = "excludeAccelerometer";
      activeTaskOptionsArray[3] = "excludeDeviceMotion";
      activeTaskOptionsArray[4] = "excludePedometer";
      activeTaskOptionsArray[5] = "excludeLocation";
      activeTaskOptionsArray[6] = "excludeHeartRate";
      activeTaskOptionsArray[7] = "excludeAudio";
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - activeTaskOptions() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - activeTaskOptions() :: Ends");
    return activeTaskOptionsArray;
  }

  /**
   * Get the question metadata for the provided question result type
   *
   * @param questionDto {@link QuestionsDto}
   * @param questionResultType the question result type
   * @param session {@link Session}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> getQuestionaireQuestionFormatByType(
      QuestionsDto questionDto, String questionResultType, Session session, String language)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    QuestionReponseTypeDto reponseType = null;
    try {
      if (StringUtils.isNotEmpty(questionResultType)) {
        reponseType =
            (QuestionReponseTypeDto)
                session
                    .createQuery(
                        "from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId=:id ORDER BY QRTDTO.responseTypeId DESC")
                    .setInteger("id", questionDto.getId())
                    .setMaxResults(1)
                    .uniqueResult();
        QuestionLangBO questionLangBO = null;
        if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
          questionLangBO = this.getQuestionLangBo(questionDto.getId(), language);
        }
        switch (questionResultType) {
          case StudyMetaDataConstants.QUESTION_SCALE:
            questionFormat = this.formatQuestionScaleDetails(reponseType, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
            questionFormat =
                this.formatQuestionContinuousScaleDetails(reponseType, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT_SCALE:
            questionFormat =
                this.formatQuestionTextScaleDetails(
                    questionDto, reponseType, session, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_VALUE_PICKER:
            questionFormat =
                this.formatQuestionValuePickerDetails(
                    questionDto, session, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_IMAGE_CHOICE:
            questionFormat =
                this.formatQuestionImageChoiceDetails(
                    questionDto, session, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT_CHOICE:
            questionFormat =
                this.formatQuestionTextChoiceDetails(
                    questionDto, reponseType, session, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_NUMERIC:
            questionFormat =
                this.formatQuestionNumericDetails(reponseType, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_DATE:
            questionFormat = this.formatQuestionDateDetails(reponseType);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT:
            questionFormat = this.formatQuestionTextDetails(reponseType, language, questionLangBO);
            break;
          case StudyMetaDataConstants.QUESTION_EMAIL:
            if (StringUtils.isNotBlank(language)
                && !MultiLanguageConstants.ENGLISH.equals(language)) {
              if (questionLangBO != null) {
                questionFormat.put(
                    "placeholder",
                    (StringUtils.isEmpty(questionLangBO.getPlaceholderText()))
                        ? ""
                        : questionLangBO.getPlaceholderText());
              } else {
                questionFormat.put("placeholder", "");
              }
            } else {
              questionFormat.put(
                  "placeholder",
                  (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))
                      ? ""
                      : reponseType.getPlaceholder());
            }
            break;
          case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
            questionFormat.put(
                "default",
                (reponseType == null || StringUtils.isEmpty(reponseType.getDefalutTime()))
                    ? 0
                    : this.getTimeInSeconds(reponseType.getDefalutTime()));
            questionFormat.put(
                "step",
                (reponseType == null || reponseType.getStep() == null)
                    ? 1
                    : this.getTimeIntervalStep(reponseType.getStep()));
            break;
          case StudyMetaDataConstants.QUESTION_HEIGHT:
            questionFormat.put(
                "measurementSystem",
                (reponseType == null || reponseType.getMeasurementSystem() == null)
                    ? ""
                    : reponseType.getMeasurementSystem());
            if (StringUtils.isNotBlank(language)
                && !MultiLanguageConstants.ENGLISH.equals(language)) {
              if (questionLangBO != null) {
                questionFormat.put(
                    "placeholder",
                    (StringUtils.isEmpty(questionLangBO.getPlaceholderText()))
                        ? ""
                        : questionLangBO.getPlaceholderText());
              } else {
                questionFormat.put("placeholder", "");
              }
            } else {
              questionFormat.put(
                  "placeholder",
                  (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))
                      ? ""
                      : reponseType.getPlaceholder());
            }
            break;
          case StudyMetaDataConstants.QUESTION_LOCATION:
            questionFormat.put(
                "useCurrentLocation",
                reponseType != null
                    && (reponseType.getUseCurrentLocation() != null
                        && reponseType.getUseCurrentLocation()));
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: Ends");
    return questionFormat;
  }

  /**
   * Get the scale response type metadata from the provided question response type details
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> formatQuestionScaleDetails(
      QuestionReponseTypeDto reponseType, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionScaleDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxValue",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMaxValue()))
              ? 10000
              : Integer.parseInt(reponseType.getMaxValue()));
      questionFormat.put(
          "minValue",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMinValue()))
              ? -10000
              : Integer.parseInt(reponseType.getMinValue()));
      questionFormat.put(
          "step",
          (reponseType == null || reponseType.getStep() == null)
              ? 1
              : this.getScaleStepSize(
                  reponseType.getStep(),
                  (Integer) questionFormat.get("maxValue"),
                  (Integer) questionFormat.get("minValue")));
      questionFormat.put(
          "default",
          (reponseType == null || reponseType.getDefaultValue() == null)
              ? (Integer) questionFormat.get("minValue")
              : this.getScaleDefaultValue(
                  reponseType.getStep(),
                  (Integer) questionFormat.get("maxValue"),
                  (Integer) questionFormat.get("minValue"),
                  Integer.parseInt(reponseType.getDefaultValue())));
      questionFormat.put(
          "vertical",
          (reponseType == null || reponseType.getVertical() == null || !reponseType.getVertical())
              ? false
              : true);

      if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
        if (questionLangBO != null) {
          questionFormat.put(
              "maxDesc",
              (StringUtils.isEmpty(questionLangBO.getMaxDescription()))
                  ? ""
                  : questionLangBO.getMaxDescription());
          questionFormat.put(
              "minDesc",
              (StringUtils.isEmpty(questionLangBO.getMinDescription()))
                  ? ""
                  : questionLangBO.getMinDescription());
        } else {
          questionFormat.put("maxDesc", "");
          questionFormat.put("minDesc", "");
        }
      } else {
        questionFormat.put(
            "maxDesc",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMaxDescription()))
                ? ""
                : reponseType.getMaxDescription());
        questionFormat.put(
            "minDesc",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMinDescription()))
                ? ""
                : reponseType.getMinDescription());
      }
      questionFormat.put(
          "maxImage",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMaxImage()))
              ? ""
              : this.getBase64Image(
                  propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                      + reponseType.getMaxImage()));
      questionFormat.put(
          "minImage",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMinImage()))
              ? ""
              : this.getBase64Image(
                  propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                      + reponseType.getMinImage()));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionScaleDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionScaleDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the continuous scale response type metadata from the provided question response type
   * details
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> formatQuestionContinuousScaleDetails(
      QuestionReponseTypeDto reponseType, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxValue",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMaxValue()))
              ? 10000
              : Double.parseDouble(reponseType.getMaxValue()));
      questionFormat.put(
          "minValue",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMinValue()))
              ? -10000
              : Double.parseDouble(reponseType.getMinValue()));
      questionFormat.put(
          "default",
          (reponseType == null || reponseType.getDefaultValue() == null)
              ? (Double) questionFormat.get("minValue")
              : Double.parseDouble(reponseType.getDefaultValue()));
      questionFormat.put(
          "maxFractionDigits",
          (reponseType == null || reponseType.getMaxFractionDigits() == null)
              ? 0
              : reponseType.getMaxFractionDigits());
      questionFormat.put(
          "vertical",
          (reponseType == null || reponseType.getVertical() == null || !reponseType.getVertical())
              ? false
              : true);

      if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
        if (questionLangBO != null) {
          questionFormat.put(
              "maxDesc",
              (StringUtils.isEmpty(questionLangBO.getMaxDescription()))
                  ? ""
                  : questionLangBO.getMaxDescription());
          questionFormat.put(
              "minDesc",
              (StringUtils.isEmpty(questionLangBO.getMinDescription()))
                  ? ""
                  : questionLangBO.getMinDescription());
        } else {
          questionFormat.put("maxDesc", "");
          questionFormat.put("minDesc", "");
        }
      } else {
        questionFormat.put(
            "maxDesc",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMaxDescription()))
                ? ""
                : reponseType.getMaxDescription());
        questionFormat.put(
            "minDesc",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMinDescription()))
                ? ""
                : reponseType.getMinDescription());
      }
      questionFormat.put(
          "maxImage",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMaxImage()))
              ? ""
              : this.getBase64Image(
                  propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                      + reponseType.getMaxImage()));
      questionFormat.put(
          "minImage",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMinImage()))
              ? ""
              : this.getBase64Image(
                  propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                      + reponseType.getMinImage()));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the text scale response type metadata from the provided question and response type details
   *
   * @param questionDto {@link QuestionsDto}
   * @param reponseType {@link QuestionReponseTypeDto}
   * @param session {@link Session}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionTextScaleDetails(
      QuestionsDto questionDto,
      QuestionReponseTypeDto reponseType,
      Session session,
      String language,
      QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextScaleDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> textChoicesList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId= :responseTypeId")
              .setInteger(StudyMetaDataEnum.QF_RESPONSE_TYPE_ID.value(), questionDto.getId())
              .list();
      if (responseSubTypeList != null && !responseSubTypeList.isEmpty()) {
        int i = 0;
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> textScaleMap = new LinkedHashMap<>();
          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            if (questionLangBO != null) {
              try {
                String displayText = questionLangBO.getDisplayText();
                String[] dispArray = displayText.split("\\|");
                if (dispArray.length > i) {
                  textScaleMap.put("text", dispArray[i]);
                } else {
                  textScaleMap.put("text", "");
                }
              } catch (IndexOutOfBoundsException e) {
                textScaleMap.put("text", "");
              }
            } else {
              textScaleMap.put("text", "");
            }
          } else {
            textScaleMap.put(
                "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          }
          textScaleMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          textScaleMap.put(
              "detail", StringUtils.isEmpty(subType.getDetail()) ? "" : subType.getDetail());
          textScaleMap.put(
              "exclusive",
              (subType.getExclusive() == null
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))
                  ? true
                  : false);
          textChoicesList.add(textScaleMap);
          i++;
        }
      }
      questionFormat.put("textChoices", textChoicesList);
      questionFormat.put(
          "default",
          (reponseType == null || reponseType.getStep() == null) ? 1 : reponseType.getStep());
      questionFormat.put(
          "vertical",
          (reponseType == null || reponseType.getVertical() == null || !reponseType.getVertical())
              ? false
              : true);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextScaleDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextScaleDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the value picker response type metadata from the provided question details
   *
   * @param questionDto {@link QuestionsDto}
   * @param session {@link Session}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionValuePickerDetails(
      QuestionsDto questionDto, Session session, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionValuePickerDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> valuePickerList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId= :responseTypeId")
              .setInteger(StudyMetaDataEnum.QF_RESPONSE_TYPE_ID.value(), questionDto.getId())
              .list();
      if (responseSubTypeList != null && !responseSubTypeList.isEmpty()) {
        int i = 0;
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> valuePickerMap = new LinkedHashMap<>();

          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            if (questionLangBO != null) {
              try {
                String displayText = questionLangBO.getDisplayText();
                String[] dispArray = displayText.split("\\|");
                if (dispArray.length > i) {
                  valuePickerMap.put("text", dispArray[i]);
                } else {
                  valuePickerMap.put("text", "");
                }
              } catch (IndexOutOfBoundsException e) {
                valuePickerMap.put("text", "");
              }
            } else {
              valuePickerMap.put("text", "");
            }
          } else {
            valuePickerMap.put(
                "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          }
          valuePickerMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          valuePickerMap.put(
              "detail", StringUtils.isEmpty(subType.getDetail()) ? "" : subType.getDetail());
          valuePickerMap.put(
              "exclusive",
              (StringUtils.isEmpty(subType.getExclusive())
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))
                  ? true
                  : false);
          valuePickerList.add(valuePickerMap);
          i++;
        }
      }
      questionFormat.put("textChoices", valuePickerList);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionValuePickerDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionValuePickerDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the image choice response type metadata from the provided question details
   *
   * @param questionDto {@link QuestionsDto}
   * @param session {@link Session}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionImageChoiceDetails(
      QuestionsDto questionDto, Session session, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> imageChoicesList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId=:id")
              .setInteger("id", questionDto.getId())
              .list();
      if (responseSubTypeList != null && !responseSubTypeList.isEmpty()) {
        int i = 0;
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> imageChoiceMap = new LinkedHashMap<>();
          imageChoiceMap.put(
              "image",
              StringUtils.isEmpty(subType.getImage())
                  ? ""
                  : this.getBase64Image(
                      propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + subType.getImage()));
          imageChoiceMap.put(
              "selectedImage",
              StringUtils.isEmpty(subType.getSelectedImage())
                  ? ""
                  : this.getBase64Image(
                      propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + subType.getSelectedImage()));

          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            if (questionLangBO != null) {
              try {
                String displayText = questionLangBO.getDisplayText();
                String[] dispArray = displayText.split("\\|");
                if (dispArray.length > i) {
                  imageChoiceMap.put("text", dispArray[i]);
                } else {
                  imageChoiceMap.put("text", "");
                }
              } catch (IndexOutOfBoundsException e) {
                imageChoiceMap.put("text", "");
              }
            } else {
              imageChoiceMap.put("text", "");
            }
          } else {
            imageChoiceMap.put(
                "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          }
          imageChoiceMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          imageChoicesList.add(imageChoiceMap);
          i++;
        }
      }
      questionFormat.put("imageChoices", imageChoicesList);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the text choice response type metadata from the provided question and response type details
   *
   * @param questionDto {@link QuestionsDto}
   * @param reponseType {@link QuestionReponseTypeDto}
   * @param session {@link Session}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionTextChoiceDetails(
      QuestionsDto questionDto,
      QuestionReponseTypeDto reponseType,
      Session session,
      String language,
      QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId=:id")
              .setInteger("id", questionDto.getId())
              .list();
      if (responseSubTypeList != null && !responseSubTypeList.isEmpty()) {
        int i = 0;
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();

          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            if (questionLangBO != null) {
              try {
                String displayText = questionLangBO.getDisplayText();
                String[] dispArray = displayText.split("\\|");
                if (dispArray.length > i) {
                  textChoiceMap.put("text", dispArray[i]);
                } else {
                  textChoiceMap.put("text", "");
                }

                String descText = questionLangBO.getTextChoiceDescription();
                String[] descArray = descText.split("\\|");
                if (descArray.length > i) {
                  textChoiceMap.put("detail", descArray[i]);
                } else {
                  textChoiceMap.put("detail", "");
                }
              } catch (IndexOutOfBoundsException e) {
                textChoiceMap.put("text", "");
                textChoiceMap.put("detail", "");
              }
            } else {
              textChoiceMap.put("text", "");
              textChoiceMap.put("detail", "");
            }
          } else {
            textChoiceMap.put(
                "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
            textChoiceMap.put(
                "detail",
                StringUtils.isEmpty(subType.getDescription()) ? "" : subType.getDescription());
          }
          textChoiceMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          textChoiceMap.put(
              "exclusive",
              (subType.getExclusive() == null
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          textChoiceMapList.add(textChoiceMap);
          i++;
        }
      }
      // other type add destination if there start
      QuestionReponseTypeDto otherReponseSubType =
          (QuestionReponseTypeDto)
              session
                  .createQuery(
                      "from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId=:id ORDER BY QRTDTO.responseTypeId DESC")
                  .setInteger("id", questionDto.getId())
                  .setMaxResults(1)
                  .uniqueResult();

      if (otherReponseSubType != null
          && otherReponseSubType.getOtherType() != null
          && StringUtils.isNotEmpty(otherReponseSubType.getOtherType())
          && otherReponseSubType.getOtherType().equals("on")) {
        LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
        textChoiceMap.put(
            "text",
            StringUtils.isEmpty(otherReponseSubType.getOtherText())
                ? ""
                : otherReponseSubType.getOtherText());
        textChoiceMap.put(
            "value",
            StringUtils.isEmpty(otherReponseSubType.getOtherValue())
                ? ""
                : otherReponseSubType.getOtherValue());
        if (StringUtils.isNotBlank(language)
            && !MultiLanguageConstants.ENGLISH.equals(language)) {
          textChoiceMap.put(
              "detail",
              StringUtils.isEmpty(questionLangBO.getOtherDescription())
                  ? ""
                  : questionLangBO.getOtherDescription());
        } else {
          textChoiceMap.put(
              "detail",
              StringUtils.isEmpty(otherReponseSubType.getOtherDescription())
                  ? ""
                  : otherReponseSubType.getOtherDescription());
        }
        textChoiceMap.put(
            "exclusive",
            otherReponseSubType.getOtherExclusive() != null
                && !otherReponseSubType
                .getOtherExclusive()
                .equalsIgnoreCase(StudyMetaDataConstants.NO));
        if (StringUtils.isNotEmpty(otherReponseSubType.getOtherIncludeText())
            && otherReponseSubType.getOtherIncludeText().equals(StudyMetaDataConstants.YES)) {
          LinkedHashMap<String, Object> textChoiceOtherMap = new LinkedHashMap<>();

          if (StringUtils.isNotBlank(language)
              && !MultiLanguageConstants.ENGLISH.equals(language)) {
            textChoiceOtherMap.put(
                "placeholder",
                StringUtils.isEmpty(questionLangBO.getOtherPlaceholderText())
                    ? ""
                    : questionLangBO.getOtherPlaceholderText());
          } else {
            textChoiceOtherMap.put(
                "placeholder",
                StringUtils.isEmpty(otherReponseSubType.getOtherPlaceholderText())
                    ? ""
                    : otherReponseSubType.getOtherPlaceholderText());
          }
          textChoiceOtherMap.put(
              "isMandatory",
              otherReponseSubType.getOtherParticipantFill() != null
                  && !otherReponseSubType
                  .getOtherParticipantFill()
                  .equalsIgnoreCase(StudyMetaDataConstants.NO));
          textChoiceOtherMap.put(
              "textfieldReq",
              otherReponseSubType.getOtherIncludeText() != null
                  && !otherReponseSubType
                  .getOtherIncludeText()
                  .equalsIgnoreCase(StudyMetaDataConstants.NO));
          textChoiceMap.put("other", textChoiceOtherMap);
        } else {
          LinkedHashMap<String, Object> textChoiceOtherMap = new LinkedHashMap<>();
          textChoiceOtherMap.put("placeholder", "");
          textChoiceOtherMap.put("isMandatory", false);
          textChoiceOtherMap.put("textfieldReq", false);
          textChoiceMap.put("other", textChoiceOtherMap);
        }

        textChoiceMapList.add(textChoiceMap);
      }
      // other type add destination if there end
      questionFormat.put("textChoices", textChoiceMapList);
      questionFormat.put(
          "selectionStyle",
          (reponseType == null || StringUtils.isEmpty(reponseType.getSelectionStyle()))
              ? ""
              : reponseType.getSelectionStyle()); // Single/Multiple
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the numeric response type metadata from the provided question response type details
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> formatQuestionNumericDetails(
      QuestionReponseTypeDto reponseType, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionNumericDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "style",
          (reponseType == null || StringUtils.isEmpty(reponseType.getStyle()))
              ? StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER
              : reponseType.getStyle());
      questionFormat.put(
          "unit",
          (reponseType == null || StringUtils.isEmpty(reponseType.getUnit()))
              ? ""
              : reponseType.getUnit());
      if (questionFormat
          .get("style")
          .toString()
          .equalsIgnoreCase(StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER)) {
        questionFormat.put(
            "minValue",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMinValue()))
                ? 0
                : Integer.parseInt(reponseType.getMinValue()));
        questionFormat.put(
            "maxValue",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMaxValue()))
                ? 10000
                : Integer.parseInt(reponseType.getMaxValue()));
      } else {
        questionFormat.put(
            "minValue",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMinValue()))
                ? 0d
                : Double.parseDouble(reponseType.getMinValue()));
        questionFormat.put(
            "maxValue",
            (reponseType == null || StringUtils.isEmpty(reponseType.getMaxValue()))
                ? 10000d
                : Double.parseDouble(reponseType.getMaxValue()));
      }

      if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
        if (questionLangBO != null) {
          questionFormat.put(
              "placeholder",
              (StringUtils.isEmpty(questionLangBO.getPlaceholderText()))
                  ? ""
                  : questionLangBO.getPlaceholderText());
        } else {
          questionFormat.put("placeholder", "");
        }
      } else {
        questionFormat.put(
            "placeholder",
            (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))
                ? ""
                : reponseType.getPlaceholder());
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionNumericDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionNumericDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the date response type metadata from the provided question response type details
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> formatQuestionDateDetails(QuestionReponseTypeDto reponseType)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionDateDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    String dateFormat = "";
    try {
      questionFormat.put(
          "style",
          (reponseType == null || StringUtils.isEmpty(reponseType.getStyle()))
              ? ""
              : reponseType.getStyle());
      if (reponseType != null
          && StringUtils.isNotEmpty(reponseType.getStyle())
          && reponseType
              .getStyle()
              .equalsIgnoreCase(
                  StudyMetaDataConstants.QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATE)) {
        dateFormat = StudyMetaDataConstants.SDF_DATE_PATTERN;
      } else {
        dateFormat = StudyMetaDataConstants.SDF_DATE_TIME_PATTERN;
      }
      questionFormat.put(
          "minDate",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMinDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getMinDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
      questionFormat.put(
          "maxDate",
          (reponseType == null || StringUtils.isEmpty(reponseType.getMaxDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getMaxDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
      questionFormat.put(
          "default",
          (reponseType == null || StringUtils.isEmpty(reponseType.getDefaultDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getDefaultDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)); // Date
      questionFormat.put(
          "dateRange",
          (reponseType == null || StringUtils.isEmpty(reponseType.getSelectionStyle())
              ? StudyMetaDataConstants.DATE_RANGE_CUSTOM
              : this.getDateRangeType(reponseType.getSelectionStyle())));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionDateDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionDateDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the text response type metadata from the provided question response type details
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> formatQuestionTextDetails(
      QuestionReponseTypeDto reponseType, String language, QuestionLangBO questionLangBO)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxLength",
          (reponseType == null || reponseType.getMaxLength() == null)
              ? 0
              : reponseType.getMaxLength());
      questionFormat.put(
          "validationRegex",
          (reponseType == null || StringUtils.isEmpty(reponseType.getValidationRegex()))
              ? ""
              : reponseType.getValidationRegex());
      questionFormat.put(
          "multipleLines",
          (reponseType == null
                  || reponseType.getMultipleLines() == null
                  || !reponseType.getMultipleLines())
              ? false
              : true);

      if (StringUtils.isNotBlank(language) && !MultiLanguageConstants.ENGLISH.equals(language)) {
        if (questionLangBO != null) {
          questionFormat.put(
              "placeholder",
              (StringUtils.isEmpty(questionLangBO.getPlaceholderText()))
                  ? ""
                  : questionLangBO.getPlaceholderText());
          questionFormat.put(
              "invalidMessage",
              (StringUtils.isEmpty(questionLangBO.getInvalidMessage()))
                  ? "Invalid Input. Please try again."
                  : questionLangBO.getInvalidMessage());

        } else {
          questionFormat.put("placeholder", "");
          questionFormat.put("invalidMessage", "");
        }
      } else {
        questionFormat.put(
            "placeholder",
            (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))
                ? ""
                : reponseType.getPlaceholder());
        questionFormat.put(
            "invalidMessage",
            (reponseType == null || StringUtils.isEmpty(reponseType.getInvalidMessage()))
                ? "Invalid Input. Please try again."
                : reponseType.getInvalidMessage());
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextDetails() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextDetails() :: Ends");
    return questionFormat;
  }

  /**
   * Get the active task start and end date time for the provided frequency type
   *
   * @param activeTaskDto {@link ActiveTaskDto}
   * @param activityBean {@link ActivitiesBean}
   * @param session {@link Session}
   * @return {@link ActivitiesBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActivitiesBean getTimeDetailsByActivityIdForActiveTask(
      ActiveTaskDto activeTaskDto, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: Starts");
    String startDateTime = "";
    String endDateTime = "";
    String startDate = "";
    String endDate = "";
    try {
      startDateTime =
          activeTaskDto.getActiveTaskLifetimeStart()
              + " "
              + StudyMetaDataConstants.DEFAULT_MIN_TIME;
      endDateTime =
          StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())
              ? ""
              : activeTaskDto.getActiveTaskLifetimeEnd()
                  + " "
                  + StudyMetaDataConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(activeTaskDto.getFrequency())) {
        if ((activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME))
            || (activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY))
            || (activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))) {

          ActiveTaskFrequencyDto activeTaskFrequency =
              (ActiveTaskFrequencyDto)
                  session
                      .createQuery(
                          "from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId=:id")
                      .setInteger("id", activeTaskDto.getId())
                      .uniqueResult();
          if (activeTaskFrequency != null
              && StringUtils.isNotEmpty(activeTaskFrequency.getFrequencyTime())) {
            if (activeTaskFrequency.isLaunchStudy() && activeTaskFrequency.isStudyLifeTime()) {
              startDateTime =
                  activeTaskDto.getActiveTaskLifetimeStart()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            } else {
              startDateTime =
                  activeTaskFrequency.getFrequencyDate()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
            if (!activeTaskDto
                    .getFrequency()
                    .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                && !activeTaskFrequency.isStudyLifeTime()) {
              endDateTime =
                  activeTaskDto.getActiveTaskLifetimeEnd()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
          }

          if (!startDateTime.contains("null")) {
            activityBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    startDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          } else {
            activityBean.setStartTime("");
          }

          activityBean.setEndTime(
              StringUtils.isEmpty(endDateTime)
                  ? ""
                  : StudyMetaDataUtil.getFormattedDateTimeZone(
                      endDateTime,
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (activeTaskDto
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {

          List<ActiveTaskFrequencyDto> activeTaskFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto ATFDTO"
                          + " where ATFDTO.activeTaskId="
                          + activeTaskDto.getId()
                          + " ORDER BY ATFDTO.frequencyTime")
                  .list();
          if (activeTaskFrequencyList != null && !activeTaskFrequencyList.isEmpty()) {
            startDateTime =
                activeTaskDto.getActiveTaskLifetimeStart()
                    + " "
                    + activeTaskFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                activeTaskDto.getActiveTaskLifetimeEnd()
                    + " "
                    + StudyMetaDataConstants.DEFAULT_MAX_TIME;
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (activeTaskDto
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomFrequenciesDto> activeTaskCustomFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomFrequenciesDto ATCFDTO where ATCFDTO.activeTaskId=:id ORDER BY ATCFDTO.frequencyTime")
                  .setInteger("id", activeTaskDto.getId())
                  .list();
          if (activeTaskCustomFrequencyList != null && !activeTaskCustomFrequencyList.isEmpty()) {
            startDate = activeTaskCustomFrequencyList.get(0).getFrequencyStartDate();
            endDate = activeTaskCustomFrequencyList.get(0).getFrequencyEndDate();
            for (ActiveTaskCustomFrequenciesDto customFrequency : activeTaskCustomFrequencyList) {
              if (StringUtils.isNotEmpty(startDate)
                  && StringUtils.isNotEmpty(customFrequency.getFrequencyStartDate())) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(startDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyStartDate()))) {
                  startDate = customFrequency.getFrequencyStartDate();
                }
              }
              if (StringUtils.isNotEmpty(endDate)
                  && StringUtils.isNotEmpty(customFrequency.getFrequencyEndDate())) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(endDate)
                    .before(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyEndDate()))) {
                  endDate = customFrequency.getFrequencyEndDate();
                }
              }
            }

            startDateTime =
                startDate + " " + activeTaskCustomFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                endDate
                    + " "
                    + activeTaskCustomFrequencyList
                        .get(activeTaskCustomFrequencyList.size() - 1)
                        .getFrequencyTime();
          }
          if (StringUtils.isNotEmpty(startDate)) {
            activityBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    startDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          if (StringUtils.isNotEmpty(endDate)) {
            activityBean.setEndTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    endDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: Ends");
    return activityBean;
  }

  /**
   * Get the questionnaire start and end date time for the provided frequency type
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param activityBean {@link ActivitiesBean}
   * @param session {@link Session}
   * @return {@link ActivitiesBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActivitiesBean getTimeDetailsByActivityIdForQuestionnaire(
      QuestionnairesDto questionaire, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: Starts");
    String startDateTime = "";
    String endDateTime = "";
    String startDate = "";
    String endDate = "";
    try {
      startDateTime =
          questionaire.getStudyLifetimeStart() + " " + StudyMetaDataConstants.DEFAULT_MIN_TIME;
      endDateTime =
          StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())
              ? ""
              : questionaire.getStudyLifetimeEnd() + " " + StudyMetaDataConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(questionaire.getFrequency())) {
        if ((questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))) {

          QuestionnairesFrequenciesDto questionnairesFrequency =
              (QuestionnairesFrequenciesDto)
                  session
                      .createQuery(
                          "from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId=:id")
                      .setInteger("id", questionaire.getId())
                      .uniqueResult();
          if (questionnairesFrequency != null
              && StringUtils.isNotEmpty(questionnairesFrequency.getFrequencyTime())) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequency.getFrequencyTime();
            if (!questionaire
                    .getFrequency()
                    .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                && !questionnairesFrequency.getIsStudyLifeTime()) {
              endDateTime =
                  questionaire.getStudyLifetimeEnd()
                      + " "
                      + questionnairesFrequency.getFrequencyTime();
            }
          }

          if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())) {
            activityBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    startDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          activityBean.setEndTime(
              StringUtils.isEmpty(endDateTime)
                  ? ""
                  : StudyMetaDataUtil.getFormattedDateTimeZone(
                      endDateTime,
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {

          List<QuestionnairesFrequenciesDto> questionnairesFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId=:id ORDER BY QFDTO.frequencyTime")
                  .setInteger("id", questionaire.getId())
                  .list();
          if (questionnairesFrequencyList != null && !questionnairesFrequencyList.isEmpty()) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                questionaire.getStudyLifetimeEnd() + " " + StudyMetaDataConstants.DEFAULT_MAX_TIME;
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnairesCustomFrequenciesDto> questionnaireCustomFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId=:id ORDER BY QCFDTO.frequencyTime")
                  .setInteger("id", questionaire.getId())
                  .list();
          if (questionnaireCustomFrequencyList != null
              && !questionnaireCustomFrequencyList.isEmpty()) {

            startDate = questionnaireCustomFrequencyList.get(0).getFrequencyStartDate();
            endDate = questionnaireCustomFrequencyList.get(0).getFrequencyEndDate();

            for (QuestionnairesCustomFrequenciesDto customFrequency :
                questionnaireCustomFrequencyList) {
              if (StringUtils.isNotEmpty(startDate)
                  && StringUtils.isNotEmpty(customFrequency.getFrequencyStartDate())) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(startDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyStartDate()))) {
                  startDate = customFrequency.getFrequencyStartDate();
                }
              } else {
                startDate = "";
              }

              if (StringUtils.isNotEmpty(endDate)
                  && StringUtils.isNotEmpty(customFrequency.getFrequencyEndDate())) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(endDate)
                    .before(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyEndDate()))) {
                  endDate = customFrequency.getFrequencyEndDate();
                }
              } else {
                endDate = "";
              }
            }

            startDateTime =
                startDate + " " + questionnaireCustomFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                endDate
                    + " "
                    + questionnaireCustomFrequencyList
                        .get(questionnaireCustomFrequencyList.size() - 1)
                        .getFrequencyTime();
          }
          if (StringUtils.isNotEmpty(startDate)) {
            activityBean.setStartTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    startDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
          if (StringUtils.isNotEmpty(endDate)) {
            activityBean.setEndTime(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    endDateTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  /**
   * Get the destination step identifier
   *
   * @param questionaireStepsList {@link List<QuestionnairesStepsDto>}
   * @return {@link List<QuestionnairesStepsDto>}
   * @throws DAOException
   * @author BTC
   */
  public List<QuestionnairesStepsDto> getDestinationStepType(
      List<QuestionnairesStepsDto> questionaireStepsList) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepType() :: Starts");
    List<QuestionnairesStepsDto> questionnaireStepsTypeList = new ArrayList<>();
    try {
      for (QuestionnairesStepsDto questionnaireStepsDto : questionaireStepsList) {
        for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
          if (questionnaireStepsDto.getDestinationStep().equals(stepsDto.getStepId())) {
            questionnaireStepsDto.setDestinationStepType(
                StringUtils.isEmpty(stepsDto.getStepShortTitle())
                    ? ""
                    : stepsDto.getStepShortTitle());
            break;
          }
        }
        questionnaireStepsTypeList.add(questionnaireStepsDto);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDestinationStepType() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepType() :: Ends");
    return questionnaireStepsTypeList;
  }

  /**
   * Get the destination step identifier for the provided response sub type
   *
   * @param destinationBean {@link DestinationBean}
   * @param destinationDto {@link QuestionResponseSubTypeDto}
   * @param questionaireStepsList {@link List<QuestionnairesStepsDto>}
   * @return {@link DestinationBean}
   * @throws DAOException
   * @author BTC
   */
  public DestinationBean getDestinationStepTypeForResponseSubType(
      DestinationBean destinationBean,
      QuestionResponseSubTypeDto destinationDto,
      List<QuestionnairesStepsDto> questionaireStepsList)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: Starts");
    try {
      for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
        if (destinationDto.getDestinationStepId().equals(stepsDto.getStepId())) {
          destinationBean.setDestination(
              StringUtils.isEmpty(stepsDto.getStepShortTitle())
                  ? ""
                  : stepsDto.getStepShortTitle());
          break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: Ends");
    return destinationBean;
  }

  /**
   * Get the base64 string for the provided image
   *
   * @param imagePath the input image path
   * @return the converted base64 code
   * @throws DAOException
   * @author BTC
   */
  public String getBase64Image(String imagePath) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getBase64Image() :: Starts");
    String base64Image = "";
    byte[] imageBytes = null;
    try {
      URL url = new URL(imagePath);
      if ("https".equalsIgnoreCase(url.getProtocol())) {
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        InputStream ins = con.getInputStream();
        imageBytes = IOUtils.toByteArray(ins);
      } else {
        imageBytes = IOUtils.toByteArray(new URL(imagePath));
      }

      base64Image = Base64.getEncoder().encodeToString(imageBytes);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getBase64Image() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getBase64Image() :: Ends");
    return base64Image;
  }

  /**
   * Get the step count for the scale response type
   *
   * @param step the step value
   * @param maxValue the maximum value
   * @param minValue the minimum value
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getScaleStepCount(Integer step, Integer maxValue, Integer minValue)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepCount() :: Starts");
    Integer scaleStepCount = 1;
    Integer maxStepCount = 13;
    List<Integer> stepCountList = new ArrayList<>();
    try {
      Integer diff = maxValue - minValue;
      while (maxStepCount > 0) {
        if ((diff % maxStepCount) == 0) {
          stepCountList.add(maxStepCount);
        }
        maxStepCount--;
      }
      if (stepCountList.contains(step)) {
        scaleStepCount = step;
        return scaleStepCount;
      }
      scaleStepCount = stepCountList.get(0);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleStepCount() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepCount() :: Ends");
    return scaleStepCount;
  }

  /**
   * Get the step size for the scale response type
   *
   * @param step the step value
   * @param maxValue the maximum value
   * @param minValue the minimum value
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getScaleStepSize(Integer step, Integer maxValue, Integer minValue)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepSize() :: Starts");
    Integer scaleStepCount = step;
    try {
      scaleStepCount = (maxValue - minValue) / step;
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleStepSize() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepSize() :: Ends");
    return scaleStepCount;
  }

  /**
   * Get the default value for the scale response type
   *
   * @param step the step value
   * @param maxValue the maximum value
   * @param minValue the minimum value
   * @param defaultValue the default value
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getScaleDefaultValue(
      Integer step, Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleDefaultValue() :: Starts");
    Integer stepSize = (maxValue - minValue) / step;
    Integer scaleDefaultValue = minValue;
    try {
      scaleDefaultValue += (stepSize * defaultValue);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleDefaultValue() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getScaleDefaultValue() :: Ends");
    return scaleDefaultValue;
  }

  /**
   * Get the maximum fraction digits for the continuous scale response type
   *
   * @param maxValue the maximum value
   * @param minValue the minimum value
   * @param actualFractionDigits the fraction digits
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getContinuousScaleMaxFractionDigits(
      Integer maxValue, Integer minValue, Integer actualFractionDigits) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Starts");
    Integer maxFracDigits = 0;
    Integer minTemp = 0;
    Integer maxTemp = 0;
    try {

      if (maxValue > 0 && maxValue <= 1) {
        maxTemp = 4;
      } else if (maxValue > 1 && maxValue <= 10) {
        maxTemp = 3;
      } else if (maxValue > 10 && maxValue <= 100) {
        maxTemp = 2;
      } else if (maxValue > 100 && maxValue <= 1000) {
        maxTemp = 1;
      } else if (maxValue > 1000 && maxValue <= 10000) {
        maxTemp = 0;
      }

      if (minValue >= -10000 && minValue < -1000) {
        minTemp = 0;
      } else if (minValue >= -1000 && minValue < -100) {
        minTemp = 1;
      } else if (minValue >= -100 && minValue < -10) {
        minTemp = 2;
      } else if (minValue >= -10 && minValue < -1) {
        minTemp = 3;
      } else if (minValue >= -1) {
        minTemp = 4;
      }
      maxFracDigits = (maxTemp > minTemp) ? minTemp : maxTemp;

      if (actualFractionDigits <= maxFracDigits) {
        maxFracDigits = actualFractionDigits;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Ends");
    return maxFracDigits;
  }

  /**
   * Get the default value for continuous scale response type
   *
   * @param maxValue the maximum value
   * @param minValue the minimum value
   * @param defaultValue the default value
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getContinuousScaleDefaultValue(
      Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleDefaultValue() :: Starts");
    Integer continuousScaleDefaultValue = minValue;
    try {
      if (defaultValue != null && (defaultValue >= minValue) && (defaultValue <= maxValue)) {
        continuousScaleDefaultValue = defaultValue;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getContinuousScaleDefaultValue() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleDefaultValue() :: Ends");
    return continuousScaleDefaultValue;
  }

  /**
   * Get the step count for the time interval response type
   *
   * @param stepValue the step value
   * @return {@link Integer}
   * @throws DAOException
   * @author BTC
   */
  public Integer getTimeIntervalStep(Integer stepValue) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeIntervalStep() :: Starts");
    int step = 1;
    Integer[] stepArray = {1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30};
    try {
      for (int i = 0; i < stepArray.length; i++) {
        if (stepValue > stepArray[i]) {
          step = stepArray[i];
        } else {
          step = stepArray[i];
          break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeIntervalStep() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeIntervalStep() :: Ends");
    return step;
  }

  /**
   * Get the seconds for the provided time
   *
   * @param time the time (HH:MM) hours
   * @return {@link Long}
   * @throws DAOException
   * @author BTC
   */
  public Long getTimeInSeconds(String time) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeInSeconds() :: Starts");
    long defaultTime = 0l;
    try {
      String[] timeArray = time.split(":");
      defaultTime += (long) (Integer.parseInt(timeArray[0]) * 3600);
      defaultTime += (long) (Integer.parseInt(timeArray[1]) * 60);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeInSeconds() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getTimeInSeconds() :: Ends");
    return defaultTime;
  }

  /**
   * Get the conditional branching destinations by solving for x
   *
   * @param reponseType {@link QuestionReponseTypeDto}
   * @param destinationsList {@link List<DestinationBean>}
   * @param questionBean {@link QuestionnaireActivityStepsBean}
   * @return {@link List<DestinationBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<DestinationBean> getConditionalBranchingDestinations(
      QuestionReponseTypeDto reponseType,
      List<DestinationBean> destinationsList,
      QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Starts");
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");

    String conditionFormula = "";
    String operator = "";
    StringTokenizer tokenizer = null;
    String LHS = "";
    String RHS = "";
    String tempFormula = "";
    boolean flag = false;
    Double maxFractionDigit = 1D;
    Double minValue = 0D;
    Double maxValue = 0D;
    Double valueOfX = 0D;
    Integer digitFormat = 0;
    Map<String, Object> prerequisitesMap = new HashMap<>();
    List<DestinationBean> updatedDestinationsList = destinationsList;
    String formatXValue = "";
    boolean skipLoop = false;
    try {
      if (StringUtils.isNotEmpty(reponseType.getConditionFormula())) {
        conditionFormula = reponseType.getConditionFormula();

        // Check the expression contains '=', if yes replace it with
        // '==' to evaluate the expression
        if (!reponseType
                .getConditionFormula()
                .contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)
            && !reponseType
                .getConditionFormula()
                .contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)
            && reponseType.getConditionFormula().contains("=")) {
          conditionFormula =
              reponseType
                  .getConditionFormula()
                  .replaceAll("=", StudyMetaDataConstants.CBO_OPERATOR_EQUAL);
        }

        // Get the minimum and maximum value range for response type
        prerequisitesMap = this.conditionalBranchingPrerequisites(questionBean);
        minValue = (Double) prerequisitesMap.get("minValue");
        maxValue = (Double) prerequisitesMap.get("maxValue");
        maxFractionDigit = (Double) prerequisitesMap.get("maxFractionDigit");
        digitFormat = (Integer) prerequisitesMap.get("digitFormat");
        formatXValue = "%." + digitFormat + "f";
        valueOfX = minValue;

        // Find position of X in the equation i.e LHS or RHS
        operator = this.getOperatorFromConditionalFormula(conditionFormula);

        // Evaluate the position of X in the equation
        if (StringUtils.isNotEmpty(operator)) {
          tokenizer = new StringTokenizer(conditionFormula, operator);
          LHS = tokenizer.nextToken().trim();
          RHS = tokenizer.nextToken().trim();
        }

        // Find minimum value of X
        while (valueOfX <= maxValue) {
          tempFormula =
              conditionFormula.replaceAll(
                  "x", valueOfX >= 0 ? valueOfX.toString() : "(" + valueOfX.toString() + ")");
          flag = (boolean) engine.eval(tempFormula);

          if (LHS.contains("x") && !RHS.contains("x")) {
            switch (operator) {
              case StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN:
                if (flag) {
                  skipLoop = true;

                  valueOfX -= maxFractionDigit;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_GREATER_THAN,
                          StudyMetaDataConstants.CBO_LESSER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_LESSER_THAN,
                          StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_EQUAL:
                if (flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_EQUAL_TO,
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO,
                          StudyMetaDataConstants.CBO_EQUAL_TO);
                }
                break;
              default:
                break;
            }
          } else {
            switch (operator) {
              case StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN:
                if (!flag) {

                  skipLoop = true;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_LESSER_THAN,
                          StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN:
                if (flag) {
                  skipLoop = true;

                  valueOfX -= maxFractionDigit;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_GREATER_THAN,
                          StudyMetaDataConstants.CBO_LESSER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_EQUAL:
                if (flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_EQUAL_TO,
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO,
                          StudyMetaDataConstants.CBO_EQUAL_TO);
                }
                break;
              default:
                break;
            }
          }

          if (skipLoop) {
            break;
          }

          valueOfX += maxFractionDigit;
          valueOfX = Double.parseDouble(String.format(formatXValue, valueOfX));
        }

        // Format the value of X by type
        updatedDestinationsList = this.formatValueOfX(updatedDestinationsList, questionBean);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getConditionalBranchingDestinations() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Ends");
    return updatedDestinationsList;
  }

  /**
   * Get conditional branching prerequisites by response type
   *
   * @param questionBean {@link QuestionnaireActivityStepsBean}
   * @return {@link Map<String, Object>}
   * @throws DAOException
   * @author BTC
   */
  public Map<String, Object> conditionalBranchingPrerequisites(
      QuestionnaireActivityStepsBean questionBean) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - conditionalBranchingPrerequisites() :: Starts");
    Map<String, Object> prerequisitesMap = new HashMap<>();
    Double maxFractionDigit = 1D;
    Double minValue = 0D;
    Double maxValue = 0D;
    Integer digitFormat = 0;
    try {
      switch (questionBean.getResultType()) {
        case StudyMetaDataConstants.QUESTION_SCALE:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());
          maxFractionDigit = 1D;
          digitFormat = 0;
          break;
        case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());

          switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
            case 0:
              maxFractionDigit = 1D;
              digitFormat = 0;
              break;
            case 1:
              maxFractionDigit = 0.1D;
              digitFormat = 1;
              break;
            case 2:
              maxFractionDigit = 0.01D;
              digitFormat = 2;
              break;
            case 3:
              maxFractionDigit = 0.001D;
              digitFormat = 3;
              break;
            case 4:
              maxFractionDigit = 0.0001D;
              digitFormat = 4;
              break;
            default:
              break;
          }
          break;
        case StudyMetaDataConstants.QUESTION_NUMERIC:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());

          switch (questionBean.getFormat().get("style").toString()) {
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:
              maxFractionDigit = 1D;
              digitFormat = 0;
              break;
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:
              maxFractionDigit = 0.01D;
              digitFormat = 2;
              break;
            default:
              break;
          }
          break;
        case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
          maxFractionDigit = 1D;
          minValue = 0D;
          maxValue = (double) (24 * 60);
          digitFormat = 0;
          break;
        case StudyMetaDataConstants.QUESTION_HEIGHT:
          maxFractionDigit = 1D;
          minValue = 0D;
          maxValue = 300D;
          digitFormat = 0;
          break;
        default:
          break;
      }

      prerequisitesMap.put("minValue", minValue);
      prerequisitesMap.put("maxValue", maxValue);
      prerequisitesMap.put("maxFractionDigit", maxFractionDigit);
      prerequisitesMap.put("digitFormat", digitFormat);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - conditionalBranchingPrerequisites() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - conditionalBranchingPrerequisites() :: Ends");
    return prerequisitesMap;
  }

  /**
   * Get the operator from the conditional formula
   *
   * @param conditionFormula the conditional branching formula
   * @return {@link String}
   * @throws DAOException
   * @author BTC
   */
  public String getOperatorFromConditionalFormula(String conditionFormula) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getOperatorFromConditionalFormula() :: Starts");
    String operator = "";
    try {
      if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_EQUAL;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getOperatorFromConditionalFormula() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getOperatorFromConditionalFormula() :: Ends");
    return operator;
  }

  /**
   * Get the conditional branching format
   *
   * @param destinationsList {@link List<DestinationBean>}
   * @param valueOfX the X value
   * @param trueOperator the true condition operator
   * @param falseOperator the false condition operator
   * @return {@link List<DestinationBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<DestinationBean> getConditionalBranchingFormat(
      List<DestinationBean> destinationsList,
      String valueOfX,
      String trueOperator,
      String falseOperator)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingFormat() :: Starts");
    try {
      if (destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2) {
        destinationsList.get(0).setCondition(valueOfX);
        destinationsList.get(0).setOperator(trueOperator);

        destinationsList.get(1).setCondition(valueOfX);
        destinationsList.get(1).setOperator(falseOperator);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getConditionalBranchingFormat() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingFormat() :: Ends");
    return destinationsList;
  }

  /**
   * Format the X value by response type and response sub type
   *
   * @param destinationsList {@link List<DestinationBean>}
   * @param questionBean {@link QuestionnaireActivityStepsBean}
   * @return {@link List<DestinationBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<DestinationBean> formatValueOfX(
      List<DestinationBean> destinationsList, QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfX() :: Starts");
    List<DestinationBean> updatedDestinationsList = destinationsList;
    try {
      if (destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2) {
        if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE)) {

          switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
            case 0:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
              break;
            case 1:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.1f", questionBean);
              break;
            case 2:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.2f", questionBean);
              break;
            case 3:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.3f", questionBean);
              break;
            case 4:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
              break;
            default:
              break;
          }
        } else if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_NUMERIC)) {

          switch (questionBean.getFormat().get("style").toString()) {
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
              break;
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
              break;
            default:
              break;
          }
        } else {
          updatedDestinationsList =
              this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatValueOfX() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfX() :: Ends");
    return updatedDestinationsList;
  }

  /**
   * Format the X value by input format
   *
   * @param destinationsList {@link List<DestinationBean>}
   * @param stringFormat the format
   * @param questionBean {@link QuestionnaireActivityStepsBean}
   * @return {@link List<DestinationBean>}
   * @throws DAOException
   * @author BTC
   */
  public List<DestinationBean> formatValueOfXByStringFormat(
      List<DestinationBean> destinationsList,
      String stringFormat,
      QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfXByStringFormat() :: Starts");
    try {
      if (destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2) {
        if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_TIME_INTERVAL)) {
          destinationsList
              .get(0)
              .setCondition(
                  String.format(
                      stringFormat,
                      Double.parseDouble(destinationsList.get(0).getCondition()) * 60));
          destinationsList
              .get(1)
              .setCondition(
                  String.format(
                      stringFormat,
                      Double.parseDouble(destinationsList.get(1).getCondition()) * 60));
        } else {
          destinationsList
              .get(0)
              .setCondition(
                  String.format(
                      stringFormat, Double.parseDouble(destinationsList.get(0).getCondition())));
          destinationsList
              .get(1)
              .setCondition(
                  String.format(
                      stringFormat, Double.parseDouble(destinationsList.get(1).getCondition())));
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatValueOfXByStringFormat() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfXByStringFormat() :: Ends");
    return destinationsList;
  }

  /**
   * Get the date range type for date response type
   *
   * @param dateRange the wcp date range value
   * @return {@link String}
   * @author BTC
   */
  public String getDateRangeType(String dateRange) {
    LOGGER.info("INFO: ActivityMetaDataDao - getDateRangeType() :: Starts");
    String dateRangeType = "";
    try {
      switch (dateRange) {
        case StudyMetaDataConstants.WCP_DATE_RANGE_UNTILL_CURRENT:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_UNTILL_CURRENT;
          break;
        case StudyMetaDataConstants.WCP_DATE_RANGE_AFTER_CURRENT:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_AFTER_CURRENT;
          break;
        case StudyMetaDataConstants.WCP_DATE_RANGE_CUSTOM:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_CUSTOM;
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDateRangeType() :: ERROR", e);
    }
    LOGGER.info("INFO: ActivityMetaDataDao - getDateRangeType() :: Ends");
    return dateRangeType;
  }

  /**
   * Get the questionnaire start and end date time for the provided frequency type
   *
   * @param activeTaskDto {@link ActiveTaskDto}
   * @param activityBean {@link ActivitiesBean}
   * @param session {@link Session}
   * @return {@link ActivitiesBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActivitiesBean getAnchordateDetailsByActivityIdForActivetask(
      ActiveTaskDto activeTaskDto, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: Starts");
    String searchQuery = "";
    try {
      ActivityAnchorDateBean activityAnchorDateBean = new ActivityAnchorDateBean();
      searchQuery = "from AnchorDateTypeDto a where a.id=:id";
      AnchorDateTypeDto anchorDateTypeDto =
          (AnchorDateTypeDto)
              session
                  .createQuery(searchQuery)
                  .setInteger("id", activeTaskDto.getAnchorDateId())
                  .uniqueResult();
      if (anchorDateTypeDto != null) {
        if (null != anchorDateTypeDto.getParticipantProperty()
            && anchorDateTypeDto.getParticipantProperty()) {
          query =
              session
                  .createQuery(
                      "From ParticipantPropertiesBO PPBO WHERE PPBO.customStudyId =:studyId "
                          + "and PPBO.active=1 and PPBO.anchorDateId=:id")
                  .setString("studyId", activeTaskDto.getCustomStudyId())
                  .setInteger("id", anchorDateTypeDto.getId());
          ParticipantPropertiesBO participantPropertiesBO =
              (ParticipantPropertiesBO) query.uniqueResult();
          if (null != participantPropertiesBO) {
            activityAnchorDateBean.setSourceType(
                StudyMetaDataConstants.ANCHOR_TYPE_PARTICIPANTPROPERTY);
            ParticipantPropertyBean participantPropertyBean = new ParticipantPropertyBean();
            participantPropertyBean.setPropertyId(participantPropertiesBO.getShortTitle());
            participantPropertyBean.setPropertyType(participantPropertiesBO.getPropertyType());
            participantPropertyBean.setPropertyDataFormat(participantPropertiesBO.getDataType());
            participantPropertyBean.setShouldRefresh(participantPropertiesBO.getRefreshedValue());
            participantPropertyBean.setDataSource(participantPropertiesBO.getDataSource());
            if (participantPropertiesBO.getStatus()) {
              participantPropertyBean.setStatus("active");
            } else {
              participantPropertyBean.setStatus("deactivated");
            }
            if (participantPropertiesBO.getDataType().equalsIgnoreCase("date")
                && participantPropertiesBO.getDataSource().equalsIgnoreCase("ExternalSystem")) {
              participantPropertyBean.setExternalPropertyId(
                  participantPropertiesBO.getShortTitle() + "ExternalId");
              participantPropertyBean.setDateOfEntryId(
                  participantPropertiesBO.getShortTitle() + "EntryDate");
            }
            activityAnchorDateBean.setPropertyMetadata(participantPropertyBean);
            activityAnchorDateBean.setSourceKey("");
            activityAnchorDateBean.setSourceFormKey("");
            activityAnchorDateBean.setSourceActivityId("");
          }
        } else if (!anchorDateTypeDto
            .getName()
            .replace(" ", "")
            .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
          searchQuery =
              "select s.step_short_title,qr.short_title"
                  + " from questionnaires qr,questions q, questionnaires_steps s where"
                  + " s.questionnaires_id=qr.id and s.instruction_form_id=q.id"
                  + " and s.step_type='Question' and qr.custom_study_id=:custom_study_id "
                  + "and qr.schedule_type=:schedule_type and qr.frequency =:frequency and q.anchor_date_id=:id";

          List<?> result =
              session
                  .createSQLQuery(searchQuery)
                  .setString("custom_study_id", activeTaskDto.getCustomStudyId())
                  .setString("schedule_type", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                  .setString("frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                  .setInteger("id", activeTaskDto.getAnchorDateId())
                  .list();
          if (null != result && !result.isEmpty()) {
            Object[] objects = (Object[]) result.get(0);
            activityAnchorDateBean.setSourceKey((String) objects[0]);
            activityAnchorDateBean.setSourceActivityId((String) objects[1]);
          } else {
            String query = "";
            query =
                "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                    + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                    + " where q.id=fm.questionId and f.formId=fm.formId"
                    + " and f.formId=qsf.instructionFormId and qsf.stepType='Form'"
                    + " and qsf.questionnairesId=qq.id and q.anchorDateId=:id"
                    + " and qq.customStudyId=:customStudyId and qq.scheduleType=:scheduleType"
                    + " and qq.frequency = :frequency";
            List<?> result1 =
                session
                    .createQuery(query)
                    .setInteger("id", activeTaskDto.getAnchorDateId())
                    .setString("customStudyId", activeTaskDto.getCustomStudyId())
                    .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                    .setString("frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                    .list();
            if (null != result1 && !result1.isEmpty()) {
              Object[] objects = (Object[]) result1.get(0);
              activityAnchorDateBean.setSourceKey((String) objects[0]);
              activityAnchorDateBean.setSourceFormKey((String) objects[1]);
              activityAnchorDateBean.setSourceActivityId((String) objects[2]);
            }
          }
        } else {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
        }
        ActivityAnchorStartBean start = new ActivityAnchorStartBean();
        ActivityAnchorEndBean end = new ActivityAnchorEndBean();
        if (activeTaskDto
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomFrequenciesDto QCFDTO where QCFDTO.activeTaskId=:id order by QCFDTO.id")
                  .setInteger("id", activeTaskDto.getId())
                  .list();
          if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
            start.setAnchorDays(
                manuallyScheduleFrequencyList.get(0).isxDaysSign()
                    ? -manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays()
                    : manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays());
            start.setTime(manuallyScheduleFrequencyList.get(0).getFrequencyTime());
            end.setAnchorDays(
                manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .isyDaysSign()
                    ? -manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays()
                    : manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays());
            end.setTime(
                manuallyScheduleFrequencyList
                    .get(manuallyScheduleFrequencyList.size() - 1)
                    .getFrequencyTime());
          }
        } else if (activeTaskDto
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {
          List<ActiveTaskFrequencyDto> taskFrequencyDtoList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto QCFDTO where QCFDTO.activeTaskId=:id order by QCFDTO.id")
                  .setInteger("id", activeTaskDto.getId())
                  .list();

          if (taskFrequencyDtoList != null && taskFrequencyDtoList.size() > 0) {
            start.setTime(taskFrequencyDtoList.get(0).getFrequencyTime());
            end.setRepeatInterval(
                activeTaskDto.getRepeatActiveTask() == null
                    ? 0
                    : activeTaskDto.getRepeatActiveTask());
            end.setAnchorDays(0);
            end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
          }

        } else {

          ActiveTaskFrequencyDto taskFrequencyDto =
              (ActiveTaskFrequencyDto)
                  session
                      .createQuery("from ActiveTaskFrequencyDto QFDTO where QFDTO.activeTaskId=:id")
                      .setInteger("id", activeTaskDto.getId())
                      .uniqueResult();
          if (taskFrequencyDto != null) {
            if (taskFrequencyDto.getTimePeriodFromDays() != null) {
              start.setAnchorDays(
                  taskFrequencyDto.isxDaysSign()
                      ? -taskFrequencyDto.getTimePeriodFromDays()
                      : taskFrequencyDto.getTimePeriodFromDays());
            }
            if (activeTaskDto.getFrequency().equals(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY)
                || activeTaskDto
                    .getFrequency()
                    .equals(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) {
              // start.setDateOfMonth(questionnairesFrequency.getFrequencyDate());
              end.setTime(taskFrequencyDto.getFrequencyTime());
            }
            /*
             * if(questionaire.getFrequency().equals(StudyMetaDataConstants.
             * FREQUENCY_TYPE_WEEKLY)) {
             * start.setDayOfWeek(StudyMetaDataUtil.getDayName(questionaire.getDayOfTheWeek(
             * ))); }
             */
            start.setTime(taskFrequencyDto.getFrequencyTime());

            end.setRepeatInterval(
                activeTaskDto.getRepeatActiveTask() == null
                    ? 0
                    : activeTaskDto.getRepeatActiveTask());
            // end.setTime(time);
            if (activeTaskDto
                .getFrequency()
                .equals(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (taskFrequencyDto.isLaunchStudy()) {
                start = null;
              }
              if (taskFrequencyDto.isStudyLifeTime()) {
                end = null;
              } else {
                end.setAnchorDays(
                    taskFrequencyDto.isyDaysSign()
                        ? -taskFrequencyDto.getTimePeriodToDays()
                        : taskFrequencyDto.getTimePeriodToDays());
                end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
              }
            }
          }
        }
        activityAnchorDateBean.setStart(start);
        activityAnchorDateBean.setEnd(end);
        activityBean.setAnchorDate(activityAnchorDateBean);
      }

    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  /**
   * Get the active task frequency details for manually schedule frequency
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param anchorRunDetailsBean {@link List<ActivityFrequencyAnchorRunsBean>}
   * @param session {@link Session}
   * @return {@link List<ActivityFrequencyScheduleBean>}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyAnchorRunsBean>
      getQuestionnaireFrequencyAncorDetailsForManuallySchedule(
          QuestionnairesDto questionaire,
          List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean,
          Session session)
          throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: Starts");
    try {
      List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId=:id")
              .setInteger("id", questionaire.getId())
              .list();
      if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
        for (QuestionnairesCustomFrequenciesDto customFrequencyDto :
            manuallyScheduleFrequencyList) {
          ActivityFrequencyAnchorRunsBean activityFrequencyAnchorRunsBean =
              new ActivityFrequencyAnchorRunsBean();
          if (customFrequencyDto.getTimePeriodFromDays() != null) {
            activityFrequencyAnchorRunsBean.setStartDays(
                customFrequencyDto.isxDaysSign()
                    ? -customFrequencyDto.getTimePeriodFromDays()
                    : customFrequencyDto.getTimePeriodFromDays());
          }
          if (customFrequencyDto.getTimePeriodToDays() != null) {
            activityFrequencyAnchorRunsBean.setEndDays(
                customFrequencyDto.isyDaysSign()
                    ? -customFrequencyDto.getTimePeriodToDays()
                    : customFrequencyDto.getTimePeriodToDays());
          }
          activityFrequencyAnchorRunsBean.setTime(customFrequencyDto.getFrequencyTime());
          anchorRunDetailsBean.add(activityFrequencyAnchorRunsBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: Ends");
    return anchorRunDetailsBean;
  }

  /**
   * Get the questionnaire start and end date time for the provided frequency type
   *
   * @param questionaire {@link QuestionnairesDto}
   * @param activityBean {@link ActivitiesBean}
   * @param session {@link Session}
   * @return {@link ActivitiesBean}
   * @throws DAOException
   * @author BTC
   */
  @SuppressWarnings("unchecked")
  public ActivitiesBean getAnchordateDetailsByActivityIdForQuestionnaire(
      QuestionnairesDto questionaire, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: Starts");
    String searchQuery = "";
    try {
      ActivityAnchorDateBean activityAnchorDateBean = new ActivityAnchorDateBean();
      searchQuery = "from AnchorDateTypeDto a where a.id=:id";
      AnchorDateTypeDto anchorDateTypeDto =
          (AnchorDateTypeDto)
              session
                  .createQuery(searchQuery)
                  .setInteger("id", questionaire.getAnchorDateId())
                  .uniqueResult();
      if (anchorDateTypeDto != null) {
        if (null != anchorDateTypeDto.getParticipantProperty()
            && anchorDateTypeDto.getParticipantProperty()) {
          query =
              session
                  .createQuery(
                      "From ParticipantPropertiesBO PPBO WHERE PPBO.customStudyId =:customStudyId and PPBO.active=1 and PPBO.anchorDateId=:id")
                  .setString("customStudyId", questionaire.getCustomStudyId())
                  .setInteger("id", anchorDateTypeDto.getId());
          ParticipantPropertiesBO participantPropertiesBO =
              (ParticipantPropertiesBO) query.uniqueResult();
          if (null != participantPropertiesBO) {
            activityAnchorDateBean.setSourceType(
                StudyMetaDataConstants.ANCHOR_TYPE_PARTICIPANTPROPERTY);
            ParticipantPropertyBean participantPropertyBean = new ParticipantPropertyBean();
            participantPropertyBean.setPropertyId(participantPropertiesBO.getShortTitle());
            participantPropertyBean.setPropertyType(participantPropertiesBO.getPropertyType());
            participantPropertyBean.setPropertyDataFormat(participantPropertiesBO.getDataType());
            participantPropertyBean.setShouldRefresh(participantPropertiesBO.getRefreshedValue());
            participantPropertyBean.setDataSource(participantPropertiesBO.getDataSource());
            if (participantPropertiesBO.getStatus()) {
              participantPropertyBean.setStatus("active");
            } else {
              participantPropertyBean.setStatus("deactivated");
            }
            if (participantPropertiesBO.getDataType().equalsIgnoreCase("date")
                && participantPropertiesBO.getDataSource().equalsIgnoreCase("ExternalSystem")) {
              participantPropertyBean.setExternalPropertyId(
                  participantPropertiesBO.getShortTitle() + "ExternalId");
              participantPropertyBean.setDateOfEntryId(
                  participantPropertiesBO.getShortTitle() + "EntryDate");
            }
            activityAnchorDateBean.setPropertyMetadata(participantPropertyBean);
            activityAnchorDateBean.setSourceKey("");
            activityAnchorDateBean.setSourceFormKey("");
            activityAnchorDateBean.setSourceActivityId("");
          }
        } else if (!anchorDateTypeDto
            .getName()
            .replace(" ", "")
            .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
          searchQuery =
              "select s.step_short_title,qr.short_title"
                  + " from questionnaires qr,questions q, questionnaires_steps s where"
                  + " s.questionnaires_id=qr.id and s.instruction_form_id=q.id"
                  + " and s.step_type='Question' and qr.custom_study_id=:customStudyId "
                  + "and qr.schedule_type=:scheduleType and qr.frequency =:frequency"
                  + " and q.anchor_date_id=:id";
          List<?> result =
              session
                  .createSQLQuery(searchQuery)
                  .setString("customStudyId", questionaire.getCustomStudyId())
                  .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                  .setString("frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                  .setInteger("id", questionaire.getAnchorDateId())
                  .list();
          if (null != result && !result.isEmpty()) {
            // for(int i=0;i<result.size();i++) {
            Object[] objects = (Object[]) result.get(0);
            activityAnchorDateBean.setSourceKey((String) objects[0]);
            activityAnchorDateBean.setSourceActivityId((String) objects[1]);
            // }
          } else {
            String query = "";
            query =
                "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                    + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                    + " where q.id=fm.questionId and f.formId=fm.formId"
                    + " and f.formId=qsf.instructionFormId and qsf.stepType='Form'"
                    + " and qsf.questionnairesId=qq.id and q.anchorDateId=:id "
                    + "and qq.customStudyId=:customStudyId and qq.scheduleType=:scheduleType "
                    + "and qq.frequency =:frequency";
            List<?> result1 =
                session
                    .createQuery(query)
                    .setInteger("id", questionaire.getAnchorDateId())
                    .setString("customStudyId", questionaire.getCustomStudyId())
                    .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                    .setString("frequency", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                    .list();
            System.out.println("Total Number Of Records : " + result1.size());
            if (null != result1 && !result1.isEmpty()) {
              Object[] objects = (Object[]) result1.get(0);
              activityAnchorDateBean.setSourceKey((String) objects[0]);
              activityAnchorDateBean.setSourceFormKey((String) objects[1]);
              activityAnchorDateBean.setSourceActivityId((String) objects[2]);
            }
          }
        } else {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
        }
        ActivityAnchorStartBean start = new ActivityAnchorStartBean();
        ActivityAnchorEndBean end = new ActivityAnchorEndBean();
        if (questionaire
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId=:id order by QCFDTO.id")
                  .setInteger("id", questionaire.getId())
                  .list();
          if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
            start.setAnchorDays(
                manuallyScheduleFrequencyList.get(0).isxDaysSign()
                    ? -manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays()
                    : manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays());
            start.setTime(manuallyScheduleFrequencyList.get(0).getFrequencyTime());
            end.setAnchorDays(
                manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .isyDaysSign()
                    ? -manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays()
                    : manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays());
            end.setTime(
                manuallyScheduleFrequencyList
                    .get(manuallyScheduleFrequencyList.size() - 1)
                    .getFrequencyTime());
          }
        } else if (questionaire
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {
          List<QuestionnairesFrequenciesDto> QuestionnairesFrequenciesDtoList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QCFDTO where QCFDTO.questionnairesId=:id order by QCFDTO.id")
                  .setInteger("id", questionaire.getId())
                  .list();

          if (QuestionnairesFrequenciesDtoList != null
              && QuestionnairesFrequenciesDtoList.size() > 0) {
            start.setTime(QuestionnairesFrequenciesDtoList.get(0).getFrequencyTime());
            end.setRepeatInterval(
                questionaire.getRepeatQuestionnaire() == null
                    ? 0
                    : questionaire.getRepeatQuestionnaire());
            start.setAnchorDays(
                QuestionnairesFrequenciesDtoList.get(0).isxDaysSign()
                    ? -QuestionnairesFrequenciesDtoList.get(0).getTimePeriodFromDays()
                    : QuestionnairesFrequenciesDtoList.get(0).getTimePeriodFromDays());
            end.setAnchorDays(0);
            end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
          }

        } else {
          QuestionnairesFrequenciesDto questionnairesFrequency =
              (QuestionnairesFrequenciesDto)
                  session
                      .createQuery(
                          "from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId=:id")
                      .setInteger("id", questionaire.getId())
                      .uniqueResult();
          if (questionnairesFrequency != null) {
            if (questionnairesFrequency.getTimePeriodFromDays() != null) {
              start.setAnchorDays(
                  questionnairesFrequency.isxDaysSign()
                      ? -questionnairesFrequency.getTimePeriodFromDays()
                      : questionnairesFrequency.getTimePeriodFromDays());
            }
            if (questionaire.getFrequency().equals(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY)
                || questionaire
                    .getFrequency()
                    .equals(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) {
              // start.setDateOfMonth(questionnairesFrequency.getFrequencyDate());
              end.setTime(questionnairesFrequency.getFrequencyTime());
            }
            /*
             * if(questionaire.getFrequency().equals(StudyMetaDataConstants.
             * FREQUENCY_TYPE_WEEKLY)) {
             * start.setDayOfWeek(StudyMetaDataUtil.getDayName(questionaire.getDayOfTheWeek(
             * ))); }
             */
            start.setTime(questionnairesFrequency.getFrequencyTime());

            end.setRepeatInterval(
                questionaire.getRepeatQuestionnaire() == null
                    ? 0
                    : questionaire.getRepeatQuestionnaire());
            // end.setTime(time);
            if (questionaire
                .getFrequency()
                .equals(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (questionnairesFrequency.getIsLaunchStudy()) {
                start = null;
              }
              if (questionnairesFrequency.getIsStudyLifeTime()) {
                end = null;
              } else {
                end.setAnchorDays(
                    questionnairesFrequency.isyDaysSign()
                        ? -questionnairesFrequency.getTimePeriodToDays()
                        : questionnairesFrequency.getTimePeriodToDays());
                end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
              }
            }
          }
        }
        activityAnchorDateBean.setStart(start);
        activityAnchorDateBean.setEnd(end);
        activityBean.setAnchorDate(activityAnchorDateBean);
      }

    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyAnchorRunsBean> getAcivetaskFrequencyAncorDetailsForManuallySchedule(
      ActiveTaskDto activeTaskDto,
      List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAcivetaskFrequencyAncorDetailsForManuallySchedule() :: Starts");
    try {
      List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from ActiveTaskCustomFrequenciesDto QCFDTO where QCFDTO.activeTaskId=:id")
              .setInteger("id", activeTaskDto.getId())
              .list();
      if (manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()) {
        for (ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList) {
          ActivityFrequencyAnchorRunsBean activityFrequencyAnchorRunsBean =
              new ActivityFrequencyAnchorRunsBean();
          activityFrequencyAnchorRunsBean.setStartDays(
              customFrequencyDto.isxDaysSign()
                  ? (customFrequencyDto.getTimePeriodFromDays()!=null?-customFrequencyDto.getTimePeriodFromDays():null)
                  : customFrequencyDto.getTimePeriodFromDays());
          activityFrequencyAnchorRunsBean.setEndDays(
              customFrequencyDto.isyDaysSign()
                  ? (customFrequencyDto.getTimePeriodToDays()!=null?-customFrequencyDto.getTimePeriodToDays():null)
                  : customFrequencyDto.getTimePeriodToDays());
          activityFrequencyAnchorRunsBean.setTime(customFrequencyDto.getFrequencyTime());
          anchorRunDetailsBean.add(activityFrequencyAnchorRunsBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAcivetaskFrequencyAncorDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.info(
        "INFO: ActivityMetaDataDao - getAcivetaskFrequencyAncorDetailsForManuallySchedule() :: Ends");
    return anchorRunDetailsBean;
  }

  public InstructionsLangBO getInstructionLangBo(int instructionId, String language) {
    LOGGER.info("ActivityMetaDataDao - getInstructionLangBo - Starts");
    InstructionsLangBO instructionsLangBO = null;
    try {
      Session session = sessionFactory.openSession();
      instructionsLangBO =
          (InstructionsLangBO)
              session
                  .createQuery(
                      "from InstructionsLangBO ILB where ILB.instructionLangPK.langCode=:language and ILB.instructionLangPK.id=:id")
                  .setString("language", language)
                  .setInteger("id", instructionId)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getInstructionLangBo - Error : ", e);
    }
    LOGGER.info("ActivityMetaDataDao - getInstructionLangBo - Ends");
    return instructionsLangBO;
  }

  public FormLangBO getFormLangBo(int formId, String language) {
    LOGGER.info("ActivityMetaDataDao - getFormLangBo - Starts");
    FormLangBO formLangBO = null;
    try {
      Session session = sessionFactory.openSession();
      formLangBO =
          (FormLangBO)
              session
                  .createQuery(
                      "from FormLangBO FLB where FLB.formLangPK.langCode=:language and FLB.formLangPK.formId=:id")
                  .setString("language", language)
                  .setInteger("id", formId)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFormLangBo - Error : ", e);
    }
    LOGGER.info("ActivityMetaDataDao - getFormLangBo - Ends");
    return formLangBO;
  }

  public QuestionLangBO getQuestionLangBo(int id, String language) {
    LOGGER.info("ActivityMetaDataDao - getQuestionLangBo - Starts");
    QuestionLangBO questionLangBO = null;
    try {
      Session session = sessionFactory.openSession();
      questionLangBO =
          (QuestionLangBO)
              session
                  .createQuery(
                      "from QuestionLangBO QLB where QLB.questionLangPK.langCode=:language and QLB.questionLangPK.id=:id")
                  .setString("language", language)
                  .setInteger("id", id)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionLangBo - Error : ", e);
    }
    LOGGER.info("ActivityMetaDataDao - getQuestionLangBo - Ends");
    return questionLangBO;
  }
}
