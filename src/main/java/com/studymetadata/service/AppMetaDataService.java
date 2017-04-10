package com.studymetadata.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.StudyUpdatesResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.exception.ErrorCodes;
import com.studymetadata.integration.AppMetaDataOrchestration;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

@Path("/appData/")
public class AppMetaDataService {
private static final Logger LOGGER = Logger.getLogger(AppMetaDataService.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	AppMetaDataOrchestration appMetaDataOrchestration = new AppMetaDataOrchestration();
	
	/**
	 * This method is used to getTermsPolicy
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return TermsPolicyResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("termsPolicy")
	public Object termsPolicy(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			termsPolicyResponse = appMetaDataOrchestration.termsPolicy(studyId);
			if(!termsPolicyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - termsPolicy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: AppMetaDataService - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}
	
	/**
	 *  This method is used to getNotifications
	 * @author Mohan
	 * @param authorization
	 * @param skip
	 * @param context
	 * @param response
	 * @return NotificationsResponse
	 *//*
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("notifications")
	public Object notifications(@HeaderParam("skip") String skip, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			if(StringUtils.isNotEmpty(skip)){
				notificationsResponse = appMetaDataOrchestration.notifications(skip);
				if(!notificationsResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - notifications() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: AppMetaDataService - notifications() :: Ends");
		return notificationsResponse;
	}
	
	*//**
	 * This method is used to save feedback
	 * @author Mohan
	 * @param subject
	 * @param body
	 * @param context
	 * @param response
	 * @return Object
	 *//*
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("feedback")
	public Object feedbackDetails(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - feedbackDetails() :: Starts");
		try{
			JSONObject serviceJson = new JSONObject(params);
			String subject = serviceJson.getString("subject");
			String body = serviceJson.getString("body");
			if(StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)){
				LOGGER.info("feedback YET TO DEVELOP");
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - feedbackDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: AppMetaDataService - feedbackDetails() :: Ends");
		return null;
	}
	
	*//**
	 * This method is used to save the contact us details
	 * @author Mohan
	 * @param studyId
	 * @param context
	 * @param response
	 * @return Object
	 *//*
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("contactUs")
	public Object contactUsDetails(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - contactUsDetails() :: Starts");
		try{
			JSONObject serviceJson = new JSONObject(params);
			String studyId = serviceJson.getString("studyId");
			String subject = serviceJson.getString("subject");
			String body = serviceJson.getString("body");
			String firstName = serviceJson.getString("firstName");
			String email = serviceJson.getString("email");
			boolean inputFlag1 = StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body);
			boolean inputFlag2 = StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(email);
			if(inputFlag1 && inputFlag2){
				LOGGER.info("contactUs YET TO DEVELOP");
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - contactUsDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: AppMetaDataService - contactUsDetails() :: Ends");
		return null;
	}
	
	*//**
	 * This method is used to check for latest app updates
	 * @author Mohan
	 * @param params
	 * @param context
	 * @param response
	 * @return Object
	 *//*
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("appUpdates")
	public Object appUpdates(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - appUpdates() :: Starts");
		try{
			JSONObject serviceJson = new JSONObject(params);
			String appVersion = serviceJson.getString("appVersion");
			String os = serviceJson.getString("os"); //ios/android
			if(StringUtils.isNotEmpty(appVersion) && StringUtils.isNotEmpty(os)){
				LOGGER.info("appUpdates YET TO DEVELOP");
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - appUpdates() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: AppMetaDataService - appUpdates() :: Ends");
		return null;
	}
	
	*//**
	 * This method is used to check for study updates
	 * @author Mohan
	 * @param params
	 * @param context
	 * @param response
	 * @return Object
	 *//*
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("studyUpdates")
	public Object studyUpdates(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: AppMetaDataService - studyUpdates() :: Starts");
		StudyUpdatesResponse studyUpdatesResponse = new StudyUpdatesResponse();
		try{
			JSONObject serviceJson = new JSONObject(params);
			String studyId = serviceJson.getString("studyId");
			String studyVersion = serviceJson.getString("studyVersion"); //current study version in app
			if(StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(studyVersion)){
				studyUpdatesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				Map<String, Object> updates = new HashMap<>();
				updates.put("consent", true);
				updates.put("activities", true);
				updates.put("resources", true);
				updates.put("info", true);
				studyUpdatesResponse.setUpdates(updates);
				studyUpdatesResponse.setCurrentVersion("1.0");
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataService - appUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataService - studyUpdates() :: Ends");
		return studyUpdatesResponse;
	}*/
}
