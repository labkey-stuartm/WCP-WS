package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;

import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.BrandingDto;
import com.studymetadata.dto.ComprehensionTestQuestionDto;
import com.studymetadata.dto.ConsentDto;
import com.studymetadata.dto.ConsentInfoDto;
import com.studymetadata.dto.EligibilityDto;
import com.studymetadata.dto.GatewayInfoDto;
import com.studymetadata.dto.GatewayWelcomeInfoDto;
import com.studymetadata.dto.ResourcesDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyPageDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.ActivitiesBean;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.BrandingBean;
import com.studymetadata.bean.ComprehensionBean;
import com.studymetadata.bean.ConfigurationBean;
import com.studymetadata.bean.ConsentBean;
import com.studymetadata.bean.EligibilityBean;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.InfoBean;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.ResourcesBean;
import com.studymetadata.bean.ResourcesResponse;
import com.studymetadata.bean.ReviewBean;
import com.studymetadata.bean.SharingBean;
import com.studymetadata.bean.StudyBean;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.bean.appendix.QuestionStepStructureBean;

public class StudyMetaDataDao {

	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataDao.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	Session session = null;
	Transaction transaction = null;
	Query query = null;
	String queryString = "";
	String fdaSmdImagePath = propMap.get("fda.smd.currentPath")+propMap.get("fda.smd.study.imagePath");

	/**
	 * @author Mohan
	 * @param authorization
	 * @return hasValidAuthorization
	 * @throws DAOException
	 */
	public boolean isValidAuthorizationId(String authorization) throws OrchestrationException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
		boolean hasValidAuthorization = false;
		String bundleIdAndAppToken = null;
		try{
			byte[] decodedBytes = Base64.getDecoder().decode(authorization);
			bundleIdAndAppToken =  new String(decodedBytes, StudyMetaDataConstants.TYPE_UTF8);
			final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
			final String bundleId = tokenizer.nextToken();
			final String appToken = tokenizer.nextToken();
			if((bundleId.equals(StudyMetaDataConstants.ANDROID_BUNDLE_ID) && appToken.equals(StudyMetaDataConstants.ANDROID_APP_TOKEN)) || (bundleId.equals(StudyMetaDataConstants.IOS_BUNDLE_ID) && appToken.equals(StudyMetaDataConstants.IOS_APP_TOKEN))){
				hasValidAuthorization = true;
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Ends");
		return hasValidAuthorization;
	}
	
	/**
	 * @author Mohan
	 * @return GatewayInfoResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public GatewayInfoResponse gatewayAppResourcesInfo() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfoResponse = new GatewayInfoResponse();
		GatewayInfoDto gatewayInfo = null;
		List<GatewayWelcomeInfoDto> gatewayWelcomeInfoList = null;
		List<ResourcesDto> resourcesList = null;
		try{
			session = sessionFactory.openSession();
			query = session.getNamedQuery("getGatewayInfo");
			gatewayInfo = (GatewayInfoDto) query.uniqueResult();
			if( null != gatewayInfo){
				//get info details
				query = session.getNamedQuery("getGatewayWelcomeInfoList");
				gatewayWelcomeInfoList = query.list();
				if( null != gatewayWelcomeInfoList && gatewayWelcomeInfoList.size() > 0){
					List<InfoBean> infoBeanList = new ArrayList<InfoBean>();
					for(GatewayWelcomeInfoDto gatewayWelcomeInfo : gatewayWelcomeInfoList){
						InfoBean infoBean = new InfoBean();
						infoBean.setTitle(StringUtils.isEmpty(gatewayWelcomeInfo.getAppTitle())==true?"":gatewayWelcomeInfo.getAppTitle());
						infoBean.setImage(StringUtils.isEmpty(gatewayWelcomeInfo.getImagePath())==true?"":fdaSmdImagePath+gatewayWelcomeInfo.getImagePath());
						infoBean.setText(StringUtils.isEmpty(gatewayWelcomeInfo.getDescription())==true?"":gatewayWelcomeInfo.getDescription());
						infoBean.setFdaLink(StringUtils.isEmpty(gatewayInfo.getFdaWebsiteUrl())==true?"":gatewayInfo.getFdaWebsiteUrl());
						if(infoBeanList.size() == 0){
							infoBean.setType(StudyMetaDataConstants.TYPE_VIDEO);
							infoBean.setLink(StringUtils.isEmpty(gatewayInfo.getVideoUrl())==true?"":gatewayInfo.getVideoUrl());
						}else{
							infoBean.setType(StudyMetaDataConstants.TYPE_TEXT);
						}
						infoBeanList.add(infoBean);
					}
					gatewayInfoResponse.setInfo(infoBeanList);
				}
				
				//get resources details
				query = session.getNamedQuery("getResourcesList");
				resourcesList = query.list();
				if( null != resourcesList && resourcesList.size() > 0){
					List<ResourcesBean> resourceBeanList = new ArrayList<ResourcesBean>();
					for(ResourcesDto resource : resourcesList){
						ResourcesBean resourceBean = new ResourcesBean();
						resourceBean.setTitle(StringUtils.isEmpty(resource.getTitle())==true?"":resource.getTitle());
						if(null != resource.getTextOrPdf() && resource.getTextOrPdf() == 1){
							resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
							resourceBean.setContent(StringUtils.isEmpty(resource.getRichText())==true?"":resource.getRichText());
						}else{
							resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourceBean.setContent(StringUtils.isEmpty(resource.getPdfUrl())==true?"":resource.getPdfUrl());
						}
						resourceBeanList.add(resourceBean);
					}
				}
			}
			gatewayInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - gatewayAppResourcesInfo() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - gatewayAppResourcesInfo() :: Ends");
		return gatewayInfoResponse;
	}
	
	/**
	 * @author Mohan
	 * @return StudyResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public StudyResponse studyList(String authorization) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyList() :: Starts");
		StudyResponse studyResponse = new StudyResponse();
		List<StudyDto> studiesList = null;
		String platformType = "";
		try{
			platformType = StudyMetaDataUtil.platformType(authorization);
			if(StringUtils.isNotEmpty(platformType)){
				session = sessionFactory.openSession();
				
				//fetch all Gateway studies based on the platform supported (iOS/android)
				query = session.getNamedQuery("gatewayStudiesListByPlatform").setString("type", StudyMetaDataConstants.STUDY_TYPE_GT).setString("platform", platformType);
				//query = session.createQuery(" from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' ");
				studiesList = query.list();
				if(null != studiesList && studiesList.size() > 0){
					List<StudyBean> studyBeanList = new ArrayList<StudyBean>();
					for(StudyDto studyDto : studiesList){
						StudyBean studyBean = new StudyBean();
						studyBean.setCategory(StringUtils.isEmpty(studyDto.getCategory())==true?"":studyDto.getCategory());
						studyBean.setDescription(StringUtils.isEmpty(studyDto.getDescription())==true?"":studyDto.getDescription());
						studyBean.setSponsorName(StringUtils.isEmpty(studyDto.getResearchSponsor())==true?"":studyDto.getResearchSponsor());
						studyBean.setStatus(StringUtils.isEmpty(studyDto.getStatus())==true?"":studyDto.getStatus());
						studyBean.setTitle(StringUtils.isEmpty(studyDto.getName())==true?"":studyDto.getName());
						studyBean.setLogo(StringUtils.isEmpty(studyDto.getThumbnailImage())==true?"":fdaSmdImagePath+studyDto.getThumbnailImage());
						studyBeanList.add(studyBean);
					}
					studyResponse.setStudies(studyBeanList);
				}
				studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyList() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyList() :: Ends");
		return studyResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return EligibilityConsentResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public EligibilityConsentResponse eligibilityConsentMetadata(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - eligibilityConsentMetadata() :: Starts");
		EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
		EligibilityDto eligibilityDto = null;
		ConsentDto consentDto = null;
		List<ConsentInfoDto> consentInfoDtoList = null;
		List<ComprehensionTestQuestionDto> comprehensionQuestionList = null;
		try{
			session = sessionFactory.openSession();
			
			//Eligibility details by studyId
			query = session.getNamedQuery("eligibilityDtoByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			eligibilityDto = (EligibilityDto) query.uniqueResult();
			if( null != eligibilityDto){
				EligibilityBean eligibility = new EligibilityBean();
				//1 - ID validation only (token), 2 - ID validation + Eligibility Test(both), 3 - Eligibility Test only(test)
				if(null != eligibilityDto.getEligibilityMechanism()){
					switch (eligibilityDto.getEligibilityMechanism()) {
					case 1: eligibility.setType(StudyMetaDataConstants.TYPE_TOKEN);
							break;
					case 2: eligibility.setType(StudyMetaDataConstants.TYPE_BOTH);
							break;
					case 3: eligibility.setType(StudyMetaDataConstants.TYPE_TEST);
							break;
					default:eligibility.setType("");
							break;
					}
				}
				eligibility.setTokenTitle(StringUtils.isEmpty(eligibilityDto.getInstructionalText())==true?"":eligibilityDto.getInstructionalText());
				eligibilityConsentResponse.setEligibility(eligibility);
			}
			
			//Consent Details
			query = session.getNamedQuery("consentDtoByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			consentDto = (ConsentDto) query.uniqueResult();
			if( null != consentDto){
				//Sharing
				SharingBean sharingBean = new SharingBean();
				sharingBean.setLearnMore(StringUtils.isEmpty(consentDto.getHtmlConsent())==true?"":consentDto.getHtmlConsent());
				sharingBean.setLongDesc(StringUtils.isEmpty(consentDto.getAffirmationText())==true?"":consentDto.getAffirmationText());
				sharingBean.setShortDesc(StringUtils.isEmpty(consentDto.getDenialText())==true?"":consentDto.getDenialText());
				eligibilityConsentResponse.setSharing(sharingBean);
				
				//Review
				ReviewBean reviewBean = new ReviewBean();
				reviewBean.setSignatureContent("");
				reviewBean.setSignatureTitle("");
				reviewBean.setTitle("");
				eligibilityConsentResponse.setReview(reviewBean);
			}
			
			//get Consent Info details by consentId
			query = session.getNamedQuery("consentInfoDtoByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			consentInfoDtoList = query.list();
			if( null != consentInfoDtoList && consentInfoDtoList.size() > 0){
				List<ConsentBean> consentBeanList = new ArrayList<ConsentBean>();
				for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
					ConsentBean consentBean = new ConsentBean();
					consentBean.setDescription(StringUtils.isEmpty(consentInfoDto.getBriefSummary())==true?"":consentInfoDto.getBriefSummary());
					consentBean.setTitle(StringUtils.isEmpty(consentInfoDto.getTitle())==true?"":consentInfoDto.getTitle());
					consentBean.setText(StringUtils.isEmpty(consentInfoDto.getElaborated())==true?"":consentInfoDto.getElaborated());
					consentBean.setHtml(StringUtils.isEmpty(consentInfoDto.getHtmlContent())==true?"":consentInfoDto.getHtmlContent());
					consentBean.setType(StringUtils.isEmpty(consentInfoDto.getContentType())==true?"":consentInfoDto.getContentType());
					consentBean.setUrl(StringUtils.isEmpty(consentInfoDto.getUrl())==true?"":consentInfoDto.getUrl());
					//Yes=true and No=false
					if(StringUtils.isNotEmpty(consentInfoDto.getVisualStep()) && consentInfoDto.getVisualStep().equalsIgnoreCase(StudyMetaDataConstants.YES)){
						consentBean.setVisualStep(true);
					}else{
						consentBean.setVisualStep(false);
					}
					consentBeanList.add(consentBean);
				}
				eligibilityConsentResponse.setConsent(consentBeanList);
			}
			
			//Comprehension Question Details
			query = session.getNamedQuery("comprehensionQuestionByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			comprehensionQuestionList = query.list();
			if( null != comprehensionQuestionList && comprehensionQuestionList.size() > 0){
				List<ComprehensionBean> comprehensionList = new ArrayList<ComprehensionBean>();
				for(ComprehensionTestQuestionDto comprehensionQuestionDto : comprehensionQuestionList){
					QuestionStepStructureBean questionStepStructure = new QuestionStepStructureBean();
					ComprehensionBean comprehensionBean = new ComprehensionBean();
					questionStepStructure.setTitle(StringUtils.isEmpty(comprehensionQuestionDto.getQuestionText())==true?"":comprehensionQuestionDto.getQuestionText());
					comprehensionBean.setQuestionStepStructureBean(questionStepStructure);
					comprehensionList.add(comprehensionBean);
				}
				eligibilityConsentResponse.setComprehension(comprehensionList);
			}
			
			eligibilityConsentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - eligibilityConsentMetadata() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return ResourcesResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ResourcesResponse resourcesForStudy(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - resourcesForStudy() :: Starts");
		ResourcesResponse resourcesResponse = new ResourcesResponse();
		List<ResourcesDto> resourcesDtoList = null; 
		try{
			session = sessionFactory.openSession();
			query = session.getNamedQuery("getResourcesListByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			resourcesDtoList = query.list();
			if( null != resourcesDtoList && resourcesDtoList.size() > 0){
				List<ResourcesBean> resourcesBeanList = new ArrayList<ResourcesBean>();
				for(ResourcesDto resourcesDto : resourcesDtoList){
					ResourcesBean resourcesBean = new ResourcesBean();
					resourcesBean.setAudience(""); //need to clarify
					resourcesBean.setTitle(StringUtils.isEmpty(resourcesDto.getTitle())==true?"":resourcesDto.getTitle());
					if(null != resourcesDto.getTextOrPdf() && resourcesDto.getTextOrPdf() == 1){
						resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
						resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getRichText())==true?"":resourcesDto.getRichText());
					}else{
						resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
						resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getPdfUrl())==true?"":resourcesDto.getPdfUrl());
					}
					
					//configuration details for the study
					ConfigurationBean configuration = new ConfigurationBean();
					configuration.setAnchorDateType("");
					configuration.setActivityId("");
					configuration.setKey("");
					configuration.setStart(0);
					configuration.setEnd(0);
					resourcesBean.setConfiguration(configuration);
					resourcesBeanList.add(resourcesBean);
				}
				resourcesResponse.setResources(resourcesBeanList);
			}
			resourcesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - resourcesForStudy() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - resourcesForStudy() :: Ends");
		return resourcesResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return StudyResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public StudyInfoResponse studyInfo(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyInfo() :: Starts");
		StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
		StudyDto studyDto = null;
		List<StudyPageDto> StudyPageDtoList = null;
		BrandingDto brandingDto = null;
		try{
			session = sessionFactory.openSession();
			//get study welcome info and page details by studyId
			query = session.getNamedQuery("studyDetailsByStudyId").setInteger("id", Integer.valueOf(studyId));
			//query = session.createQuery(" from StudyDto SDTO where SDTO.id="+studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(null != studyDto){
				query = session.getNamedQuery("studyPageDetailsByStudyId").setInteger("studyId", Integer.valueOf(studyId));
				StudyPageDtoList = query.list();
				if( null != StudyPageDtoList && StudyPageDtoList.size() > 0){
					List<InfoBean> infoList = new ArrayList<InfoBean>();
					for(StudyPageDto studyPageInfo : StudyPageDtoList){
						InfoBean info = new InfoBean();
						if(infoList.size() == 0){
							info.setType(StudyMetaDataConstants.TYPE_VIDEO);
							info.setLink(StringUtils.isEmpty(studyDto.getMediaLink())==true?"":studyDto.getMediaLink());
						}else{
							info.setType(StudyMetaDataConstants.TYPE_TEXT);
							info.setLink("");
						}
						info.setTitle(StringUtils.isEmpty(studyPageInfo.getTitle())==true?"":studyPageInfo.getTitle());
						info.setImage(StringUtils.isEmpty(studyPageInfo.getImagePath())==true?"":fdaSmdImagePath+studyPageInfo.getImagePath());
						info.setText(StringUtils.isEmpty(studyPageInfo.getDescription())==true?"":studyPageInfo.getDescription());
					}
				}
			}
			
			//get branding details by studyId
			query = session.getNamedQuery("brandingDetailsByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			brandingDto = (BrandingDto) query.uniqueResult();
			if(null != brandingDto){
				BrandingBean branding = new BrandingBean();
				branding.setBgColor(StringUtils.isEmpty(brandingDto.getBackground())==true?"":brandingDto.getBackground());
				branding.setLogo(StringUtils.isEmpty(brandingDto.getLogoImagePath())==true?"":fdaSmdImagePath+brandingDto.getLogoImagePath());
				branding.setTintColor(StringUtils.isEmpty(brandingDto.getTint())==true?"":brandingDto.getTint());
				branding.setTitleFont(StringUtils.isEmpty(brandingDto.getFont())==true?"":brandingDto.getFont());
				studyInfoResponse.setBranding(branding);
			}
			
			studyInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyInfo() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyInfo() :: Ends");
		return studyInfoResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return ActivityResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ActivityResponse studyActivityList(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		List<ActiveTaskDto> activeTaskDtoList = null;
		try{
			session = sessionFactory.openSession();
			query = session.getNamedQuery("activeTaskByStudyId").setInteger("studyId", Integer.valueOf(studyId));
			activeTaskDtoList = query.list();
			if( null != activeTaskDtoList && activeTaskDtoList.size() > 0){
				List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
				for(ActiveTaskDto activeTaskDto : activeTaskDtoList){
					ActivitiesBean activityBean = new ActivitiesBean();
					activityBean.setTitle(StringUtils.isEmpty(activeTaskDto.getTaskName())==true?"":activeTaskDto.getTaskName());
					activityBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
					activityBean.setFrequency(StringUtils.isEmpty(activeTaskDto.getDuration())==true?"":activeTaskDto.getDuration());
					
					//set configuration details
					ConfigurationBean configurationBean = new ConfigurationBean();
					configurationBean.setStartTime("");
					configurationBean.setEndTime("");
					configurationBean.setLifetime("");
					configurationBean.setRunLifetime("");
					activityBean.setConfiguration(configurationBean);
					
					activitiesBeanList.add(activityBean);
				}
				activityResponse.setActivities(activitiesBeanList);
			}
			activityResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyActivityList() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityList() :: Ends");
		return activityResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return ActivityResponse
	 * @throws DAOException
	 */
	public ActivityResponse studyActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityMetadata() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		try{
			
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyActivityMetadata() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityMetadata() :: Ends");
		return activityResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return StudyDashboardResponse
	 * @throws DAOException
	 */
	public StudyDashboardResponse studyDashboardInfo(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		try{
			
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyDashboardInfo() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return TermsPolicyResponse
	 * @throws DAOException
	 */
	public TermsPolicyResponse termsPolicy(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - termsPolicy() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}
	
	/**
	 * @author Mohan
	 * @param skip
	 * @return NotificationsResponse
	 * @throws DAOException
	 */
	public NotificationsResponse notifications(String skip) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		try{
			
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - notifications() :: ERROR", e);
			e.printStackTrace();
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - notifications() :: Ends");
		return notificationsResponse;
	}
}
