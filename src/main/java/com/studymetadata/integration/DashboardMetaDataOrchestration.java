package com.studymetadata.integration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.dao.DashboardMetaDataDao;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataUtil;

public class DashboardMetaDataOrchestration {
	private static final Logger LOGGER = Logger.getLogger(DashboardMetaDataOrchestration.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();
	
	DashboardMetaDataDao dashboardMetaDataDao = new DashboardMetaDataDao();
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return StudyDashboardResponse
	 * @throws OrchestrationException
	 */
	public StudyDashboardResponse studyDashboardInfo(String studyId) throws OrchestrationException{
		LOGGER.info("INFO: DashboardMetaDataOrchestration - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try{
			studyDashboardResponse = dashboardMetaDataDao.studyDashboardInfo(studyId);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataOrchestration - studyDashboardInfo() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataOrchestration - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
}
