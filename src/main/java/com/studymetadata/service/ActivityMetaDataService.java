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

import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.exception.ErrorCodes;
import com.studymetadata.integration.ActivityMetaDataOrchestration;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

@Path("/activityData/")
public class ActivityMetaDataService {
private static final Logger LOGGER = Logger.getLogger(ActivityMetaDataService.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	ActivityMetaDataOrchestration activityMetaDataOrchestration = new ActivityMetaDataOrchestration();
	
	/**
	 * This method is used to getStudyActivityList
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return ActivityResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("activityList")
	public Object studyActivityList(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: ActivityMetaDataService - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				activityResponse = activityMetaDataOrchestration.studyActivityList(studyId);
				if(!activityResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(activityResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("ActivityMetaDataService - studyActivityList() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: ActivityMetaDataService - studyActivityList() :: Ends");
		return activityResponse;
	}
	
	
	/**
	 * This method is used to getStudyActivityMetadata
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @param context
	 * @param response
	 * @return ActivityMetaDataResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("activity")
	public Object studyActivityMetadata(@HeaderParam("studyId") String studyId, @HeaderParam("activityId") String activityId, @HeaderParam("activityVersion") String activityVersion, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: ActivityMetaDataService - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		try{
			if(StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)){
				activityMetaDataResponse = activityMetaDataOrchestration.studyActivityMetadata(studyId, activityId, activityVersion);
				if(!activityMetaDataResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(activityMetaDataResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("ActivityMetaDataService - studyActivityMetadata() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: ActivityMetaDataService - studyActivityMetadata() :: Ends");
		return activityMetaDataResponse;
	}
}
