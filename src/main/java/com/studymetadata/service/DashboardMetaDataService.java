package com.studymetadata.service;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.exception.ErrorCodes;
import com.studymetadata.integration.DashboardMetaDataOrchestration;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

@Path("/dashboradData/")
public class DashboardMetaDataService {
	private static final Logger LOGGER = Logger.getLogger(DashboardMetaDataService.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	DashboardMetaDataOrchestration dashboardMetaDataOrchestration = new DashboardMetaDataOrchestration();
	
	/**
	 * This method is used to getStudyDashboardInfo
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return StudyDashboardResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyDashboard")
	public Object studyDashboardInfo(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: DashboardMetaDataService - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				studyDashboardResponse = dashboardMetaDataOrchestration.studyDashboardInfo(studyId);
				if(!studyDashboardResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(studyDashboardResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
						StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
						return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
					}else{
						StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT, StudyMetaDataConstants.INVALID_STUDY_ID, response);
						return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_STUDY_ID).build();
					}
				}
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataService - studyDashboardInfo() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: DashboardMetaDataService - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
}
