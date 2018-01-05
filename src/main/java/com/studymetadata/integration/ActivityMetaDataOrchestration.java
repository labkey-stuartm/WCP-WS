package com.studymetadata.integration;

import java.util.HashMap;
import org.apache.log4j.Logger;

import com.studymetadata.bean.ActiveTaskActivityMetaDataResponse;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.QuestionnaireActivityMetaDataResponse;
import com.studymetadata.dao.ActivityMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataUtil;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:36:39 PM
 *
 */
public class ActivityMetaDataOrchestration {

	private static final Logger LOGGER = Logger
			.getLogger(ActivityMetaDataOrchestration.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	ActivityMetaDataDao activityMetaDataDao = new ActivityMetaDataDao();

	/**
	 * Get all the activities for the provided study identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @param authorization
	 * @return {@link ActivityResponse}
	 * @throws OrchestrationException
	 */
	public ActivityResponse studyActivityList(String studyId,
			String authorization) throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try {
			activityResponse = activityMetaDataDao.studyActivityList(studyId,
					authorization);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyActivityList() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Ends");
		return activityResponse;
	}

	/**
	 * Get the active task metadata for the provided activity version, study and
	 * activity identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return {@link ActiveTaskActivityMetaDataResponse}
	 * @throws OrchestrationException
	 */
	public ActiveTaskActivityMetaDataResponse studyActiveTaskActivityMetadata(
			String studyId, String activityId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: Starts");
		ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse = new ActiveTaskActivityMetaDataResponse();
		try {
			activeTaskActivityMetaDataResponse = activityMetaDataDao
					.studyActiveTaskActivityMetadata(studyId, activityId,
							activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActiveTaskActivityMetadata() :: Ends");
		return activeTaskActivityMetaDataResponse;
	}

	/**
	 * Get the questionnaire metadata for the provided activity version, study
	 * and activity identifier
	 * 
	 * @author BTC
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return {@link QuestionnaireActivityMetaDataResponse}
	 * @throws OrchestrationException
	 */
	public QuestionnaireActivityMetaDataResponse studyQuestionnaireActivityMetadata(
			String studyId, String activityId, String activityVersion)
			throws OrchestrationException {
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: Starts");
		QuestionnaireActivityMetaDataResponse questionnaireActivityMetaDataResponse = new QuestionnaireActivityMetaDataResponse();
		try {
			questionnaireActivityMetaDataResponse = activityMetaDataDao
					.studyQuestionnaireActivityMetadata(studyId, activityId,
							activityVersion);
		} catch (Exception e) {
			LOGGER.error(
					"ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: ERROR",
					e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyQuestionnaireActivityMetadata() :: Ends");
		return questionnaireActivityMetaDataResponse;
	}

}
