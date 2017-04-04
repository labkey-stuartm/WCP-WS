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

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.studymetadata.exception.ErrorCodes;
import com.studymetadata.integration.StudyMetaDataOrchestration;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.ConsentDocumentResponse;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.FailureResponse;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.ResourcesResponse;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;
import com.studymetadata.bean.StudyUpdatesResponse;
import com.studymetadata.bean.SuccessResponse;
import com.studymetadata.bean.TermsPolicyResponse;

@Path("/")
public class StudyMetaDataService {
	
	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataService.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	StudyMetaDataOrchestration studyMetaDataOrchestration = new StudyMetaDataOrchestration();

	/*------------------------------------FDA-HPHI Study Meta Data Web Services Starts------------------------------------*/
	/**
	 * This Method is used to getGatewayAppResourcesInfo
	 * @author Mohan
	 * @param authorization
	 * @param context
	 * @param response
	 * @return GatewayInfoResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("gatewayInfo")
	public Object gatewayAppResourcesInfo(@HeaderParam("Authorization") String authorization, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
		try{
			gatewayInfo = studyMetaDataOrchestration.gatewayAppResourcesInfo(authorization);
			if(!gatewayInfo.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - gatewayAppResourcesInfo() :: ERROR ", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - gatewayAppResourcesInfo() :: Ends");
		return gatewayInfo;
	}
	
	/**
	 * This Method is used to getStudyList
	 * @author Mohan
	 * @param authorization
	 * @param context
	 * @param response
	 * @return StudyResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyList")
	public Object studyList(@HeaderParam("Authorization") String authorization, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		try{
			studyResponse = studyMetaDataOrchestration.studyList(authorization);
			if(!studyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
				return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - studyList() :: ERROR ", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyList() :: Ends");
		return studyResponse;
	}
	
	/**
	 * This method is used to getEligibilityConsentMetadata
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return EligibilityConsentResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("eligibilityConsent")
	public Object eligibilityConsentMetadata(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - eligibilityConsentMetadata() :: Starts");
		EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				eligibilityConsentResponse = studyMetaDataOrchestration.eligibilityConsentMetadata(studyId);
				if(!eligibilityConsentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(eligibilityConsentResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("StudyMetaDataService - eligibilityConsentMetadata() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}
	
	/**
	 * This method is used to getConsentDocument Details
	 * @author Mohan
	 * @param studyId
	 * @param consentVersion
	 * @param activityId
	 * @param activityVersion
	 * @param context
	 * @param response
	 * @return ConsentDocumentResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("consentDocument")
	public Object consentDocument(@HeaderParam("studyId") String studyId, @HeaderParam("consentVersion") String consentVersion, @HeaderParam("activityId") String activityId, @HeaderParam("activityVersion") String activityVersion, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		try{
			/*if(StringUtils.isNotEmpty(studyId) && (StringUtils.isNotEmpty(consentVersion) || StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion))){*/
			if(StringUtils.isNotEmpty(studyId)){	
				consentDocumentResponse = studyMetaDataOrchestration.consentDocument(studyId, consentVersion, activityId, activityVersion);
				if(!consentDocumentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(consentDocumentResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Ends");
		return consentDocumentResponse;
	}
	
	/**
	 * This method is used to getResourcesForStudy
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return ResourcesResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("resources")
	public Object resourcesForStudy(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Starts");
		ResourcesResponse resourcesResponse = new ResourcesResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				resourcesResponse = studyMetaDataOrchestration.resourcesForStudy(studyId);
				if(!resourcesResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(resourcesResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - resourcesForStudy() :: Ends");
		return resourcesResponse;
	}
	
	/**
	 * This method is used to getStudyInfo
	 * @author Mohan
	 * @param authorization
	 * @param studyId
	 * @param context
	 * @param response
	 * @return StudyInfoResponse
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("studyInfo")
	public Object studyInfo(@HeaderParam("studyId") String studyId, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - studyInfo() :: Starts");
		StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				studyInfoResponse = studyMetaDataOrchestration.studyInfo(studyId);
				if(!studyInfoResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					if(studyInfoResponse.getMessage().equalsIgnoreCase(StudyMetaDataConstants.FAILURE)){
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
			LOGGER.error("StudyMetaDataService - studyInfo() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyInfo() :: Ends");
		return studyInfoResponse;
	}
	
	
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
		LOGGER.info("INFO: StudyMetaDataService - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				activityResponse = studyMetaDataOrchestration.studyActivityList(studyId);
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
			LOGGER.error("StudyMetaDataService - studyActivityList() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyActivityList() :: Ends");
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
		LOGGER.info("INFO: StudyMetaDataService - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		try{
			if(StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)){
				activityMetaDataResponse = studyMetaDataOrchestration.studyActivityMetadata(studyId, activityId, activityVersion);
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
			LOGGER.error("StudyMetaDataService - studyActivityMetadata() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyActivityMetadata() :: Ends");
		return activityMetaDataResponse;
	}
	
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
		LOGGER.info("INFO: StudyMetaDataService - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try{
			if(StringUtils.isNotEmpty(studyId)){
				studyDashboardResponse = studyMetaDataOrchestration.studyDashboardInfo(studyId);
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
			LOGGER.error("StudyMetaDataService - studyDashboardInfo() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
	
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
		LOGGER.info("INFO: StudyMetaDataService - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			/*if(StringUtils.isNotEmpty(studyId)){*/
				termsPolicyResponse = studyMetaDataOrchestration.termsPolicy(studyId);
				if(!termsPolicyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			/*}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}*/
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - termsPolicy() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - termsPolicy() :: Ends");
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
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("notifications")
	public Object notifications(@HeaderParam("skip") String skip, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			if(StringUtils.isNotEmpty(skip)){
				notificationsResponse = studyMetaDataOrchestration.notifications(skip);
				if(!notificationsResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)){
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
					return Response.status(Response.Status.NO_CONTENT).entity(StudyMetaDataConstants.NO_RECORD).build();
				}
			}else{
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.INVALID_INPUT, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG).build();
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - notifications() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - notifications() :: Ends");
		return notificationsResponse;
	}
	
	/**
	 * This method is used to save feedback
	 * @author Mohan
	 * @param subject
	 * @param body
	 * @param context
	 * @param response
	 * @return Object
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("feedback")
	public Object feedbackDetails(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - feedbackDetails() :: Starts");
		String subject = "";
		String body = "";
		try{
			try {
				JSONObject serviceJson = new JSONObject(params);
				subject = serviceJson.getString("subject");
				body = serviceJson.getString("body");
				System.out.println(subject+" : "+body);
			} catch (Exception e) {
				LOGGER.error("StudyMetaDataService - contactUsDetails() :: ERROR", e);
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
			System.out.println("Yet to develop");
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - feedbackDetails() :: ERROR", e);
			StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
			return Response.status(Response.Status.NOT_FOUND).entity(StudyMetaDataConstants.FAILURE).build();
		}
		LOGGER.info("INFO: StudyMetaDataService - feedbackDetails() :: Ends");
		return null;
	}
	
	/**
	 * This method is used to save the contact us details
	 * @author Mohan
	 * @param studyId
	 * @param context
	 * @param response
	 * @return Object
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("contactUs")
	public Object contactUsDetails(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - contactUsDetails() :: Starts");
		String studyId = "";
		String subject = "";
		String body = "";
		String firstName = "";
		String email = "";
		try{
			try {
				JSONObject serviceJson = new JSONObject(params);
				studyId = serviceJson.getString("studyId");
				subject = serviceJson.getString("subject");
				body = serviceJson.getString("body");
				firstName = serviceJson.getString("firstName");
				email = serviceJson.getString("email");
				System.out.println(studyId+" : "+subject+" : "+body+" : "+firstName+" : "+email);
			} catch (Exception e) {
				LOGGER.error("StudyMetaDataService - contactUsDetails() :: ERROR", e);
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
			System.out.println("Yet to develop");
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - contactUsDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataService - contactUsDetails() :: Ends");
		return null;
	}
	
	/**
	 * This method is used to check for latest app updates
	 * @author Mohan
	 * @param params
	 * @param context
	 * @param response
	 * @return Object
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("appUpdates")
	public Object appUpdates(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - appUpdates() :: Starts");
		String appVersion = "";
		String os = ""; //ios/android
		try{
			try {
				JSONObject serviceJson = new JSONObject(params);
				appVersion = serviceJson.getString("appVersion");
				os = serviceJson.getString("os");
				System.out.println(appVersion+" : "+os);
			} catch (Exception e) {
				LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
			System.out.println("Yet to develop");
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataService - appUpdates() :: Ends");
		return null;
	}
	
	/**
	 * This method is used to check for study updates
	 * @author Mohan
	 * @param params
	 * @param context
	 * @param response
	 * @return Object
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("studyUpdates")
	public Object studyUpdates(String params, @Context ServletContext context, @Context HttpServletResponse response){
		LOGGER.info("INFO: StudyMetaDataService - studyUpdates() :: Starts");
		StudyUpdatesResponse studyUpdatesResponse = new StudyUpdatesResponse();
		String studyId = "";
		String studyVersion = ""; //current study version in app
		try{
			try {
				JSONObject serviceJson = new JSONObject(params);
				studyId = serviceJson.getString("studyId");
				studyVersion = serviceJson.getString("studyVersion");
				if(StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(studyVersion)){
					studyUpdatesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
					Map<String, Object> updates = new HashMap<String, Object>();
					updates.put("consent", true);
					updates.put("activities", true);
					updates.put("resources", true);
					updates.put("info", true);
					studyUpdatesResponse.setUpdates(updates);
					studyUpdatesResponse.setCurrentVersion("1.0");
					System.out.println(studyId+" : "+studyVersion);
				}else{
					StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
					return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
				}
			} catch (Exception e) {
				LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
				StudyMetaDataUtil.getFailureResponse(ErrorCodes.STATUS_102, ErrorCodes.UNKNOWN, StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG, response);
				return Response.status(Response.Status.BAD_REQUEST).entity(StudyMetaDataConstants.INVALID_INPUT).build();
			}
			System.out.println("Yet to develop");
		}catch(Exception e){
			LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataService - studyUpdates() :: Ends");
		return studyUpdatesResponse;
	}
	
	
	/*------------------------------------FDA-HPHI Study Meta Data Web Services Starts------------------------------------*/
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_XML)
	@Path("ping")
	public String ping() {
		LOGGER.info("INFO: StudyMetaDataService - ping() :: Starts ");
		String response = "It Works!";
		LOGGER.info("INFO: StudyMetaDataService - ping() :: Ends ");
		return response;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("test")
	public Object test(@HeaderParam("type") String type, String params, @Context ServletContext context, @Context HttpServletResponse response) {
		LOGGER.info("INFO: StudyMetaDataService - test() :: Starts ");
		String type1 = "";
		try {
			type1 = type;
			JSONObject serviceJson = new JSONObject(params);
			LOGGER.info("json type  " + serviceJson.get("type"));
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataService.signin() :: ERROR ==> 'type' is missing... ");
		}
		if(StudyMetaDataUtil.isNotEmpty(type1) && "1".equals(type1)){
			SuccessResponse sr = new SuccessResponse();
			sr.setResultType(StudyMetaDataConstants.SUCCESS);
			LOGGER.info("INFO: StudyMetaDataService - test() :: Ends ");
			return sr;
		} else {
			FailureResponse fr = new FailureResponse();
			fr.setResultType(StudyMetaDataConstants.FAILURE);
			fr.getErrors().setStatus(ErrorCodes.STATUS_104);
			fr.getErrors().setTitle(ErrorCodes.UNKNOWN);
			fr.getErrors().setDetail("Testing failure response.");
			LOGGER.info("INFO: StudyMetaDataService - test() :: Ends ");
			return fr;
		}
	}
}
