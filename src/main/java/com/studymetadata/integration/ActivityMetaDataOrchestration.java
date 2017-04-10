package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.dao.ActivityMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataUtil;

public class ActivityMetaDataOrchestration {

	private static final Logger LOGGER = Logger.getLogger(ActivityMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	ActivityMetaDataDao activityMetaDataDao = new ActivityMetaDataDao();
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return ActivityResponse
	 * @throws OrchestrationException
	 */
	public ActivityResponse studyActivityList(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try{
			activityResponse = activityMetaDataDao.studyActivityList(studyId);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataOrchestration - studyActivityList() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityList() :: Ends");
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
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		try{
			activityMetaDataResponse = activityMetaDataDao.studyActivityMetadata(studyId, activityId, activityVersion);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataOrchestration - studyActivityMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataOrchestration - studyActivityMetadata() :: Ends");
		return activityMetaDataResponse;
	}
}
