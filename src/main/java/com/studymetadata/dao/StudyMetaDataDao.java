package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.dto.ActiveTaskAttrtibutesValuesDto;
import com.studymetadata.dto.ActiveTaskCustomFrequenciesDto;
import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.ActiveTaskFrequencyDto;
import com.studymetadata.dto.ActiveTaskListDto;
import com.studymetadata.dto.ActiveTaskMasterAttributeDto;
import com.studymetadata.dto.ComprehensionTestQuestionDto;
import com.studymetadata.dto.ConsentDto;
import com.studymetadata.dto.ConsentInfoDto;
import com.studymetadata.dto.ConsentMasterInfoDto;
import com.studymetadata.dto.EligibilityDto;
import com.studymetadata.dto.FormMappingDto;
import com.studymetadata.dto.GatewayInfoDto;
import com.studymetadata.dto.GatewayWelcomeInfoDto;
import com.studymetadata.dto.InstructionsDto;
import com.studymetadata.dto.QuestionnairesCustomFrequenciesDto;
import com.studymetadata.dto.QuestionnairesDto;
import com.studymetadata.dto.QuestionnairesFrequenciesDto;
import com.studymetadata.dto.QuestionnairesStepsDto;
import com.studymetadata.dto.QuestionsDto;
import com.studymetadata.dto.ReferenceTablesDto;
import com.studymetadata.dto.ResourcesDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyPageDto;
import com.studymetadata.dto.StudySequenceDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.exception.OrchestrationException;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.ActivitiesBean;
import com.studymetadata.bean.ActivityFrequencyBean;
import com.studymetadata.bean.ActivityFrequencyScheduleBean;
import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityMetadataBean;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.ActivityStepsBean;
import com.studymetadata.bean.ChartsBean;
import com.studymetadata.bean.ComprehensionBean;
import com.studymetadata.bean.ComprehensionDetailsBean;
import com.studymetadata.bean.ConfigurationBean;
import com.studymetadata.bean.ConsentBean;
import com.studymetadata.bean.ConsentDetailsBean;
import com.studymetadata.bean.ConsentDocumentBean;
import com.studymetadata.bean.ConsentDocumentResponse;
import com.studymetadata.bean.CorrectAnswersBean;
import com.studymetadata.bean.DashboardBean;
import com.studymetadata.bean.EligibilityBean;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.InfoBean;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.ResourcesBean;
import com.studymetadata.bean.ResourcesResponse;
import com.studymetadata.bean.ReviewBean;
import com.studymetadata.bean.SettingsBean;
import com.studymetadata.bean.SharingBean;
import com.studymetadata.bean.StatisticsBean;
import com.studymetadata.bean.StudyBean;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.bean.WithdrawalConfigBean;
import com.studymetadata.bean.appendix.ActivityStructureBean;
import com.studymetadata.bean.appendix.QuestionStepStructureBean;

public class StudyMetaDataDao {

	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataDao.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> authPropMap = StudyMetaDataUtil.authConfigMap;
	
	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	Session session = null;
	Transaction transaction = null;
	Query query = null;
	String queryString = "";
	
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
			if((bundleId.equals(authPropMap.get("android.bundleid")) && appToken.equals(authPropMap.get("android.apptoken"))) || (bundleId.equals(authPropMap.get("ios.bundleid")) && appToken.equals(authPropMap.get("ios.apptoken")))){
				hasValidAuthorization = true;
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR", e);
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
	public GatewayInfoResponse gatewayAppResourcesInfo(String authorization) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - gatewayAppResourcesInfo() :: Starts");
		GatewayInfoResponse gatewayInfoResponse = new GatewayInfoResponse();
		GatewayInfoDto gatewayInfo = null;
		List<GatewayWelcomeInfoDto> gatewayWelcomeInfoList = null;
		List<ResourcesDto> resourcesList = null;
		String platformType = "";
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
						infoBean.setImage(StringUtils.isEmpty(gatewayWelcomeInfo.getImagePath())==true?"":propMap.get("fda.smd.study.thumbnailPath")+gatewayWelcomeInfo.getImagePath());
						infoBean.setText(StringUtils.isEmpty(gatewayWelcomeInfo.getDescription())==true?"":gatewayWelcomeInfo.getDescription());
						if(infoBeanList.size() == 0){
							infoBean.setType(StudyMetaDataConstants.TYPE_VIDEO);
							infoBean.setVideoLink(StringUtils.isEmpty(gatewayInfo.getVideoUrl())==true?"":gatewayInfo.getVideoUrl());
						}else{
							infoBean.setType(StudyMetaDataConstants.TYPE_TEXT);
						}
						infoBeanList.add(infoBean);
					}
					gatewayInfoResponse.setInfo(infoBeanList);
				}
			}
			
			//get resources details
			platformType = StudyMetaDataUtil.platformType(authorization);
			if(StringUtils.isNotEmpty(platformType)){
				query = session.createQuery(" from ResourcesDto RDTO where RDTO.studyId in ( select SDTO.id from StudyDto SDTO where SDTO.platform like '%"+platformType+"%' and SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"') ");
				resourcesList = query.list();
				if( null != resourcesList && resourcesList.size() > 0){
					List<ResourcesBean> resourceBeanList = new ArrayList<ResourcesBean>();
					for(ResourcesDto resource : resourcesList){
						ResourcesBean resourceBean = new ResourcesBean();
						resourceBean.setTitle(StringUtils.isEmpty(resource.getTitle())==true?"":resource.getTitle());
						if(null != resource.getTextOrPdf() && resource.getTextOrPdf() == 0){
							resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
							resourceBean.setContent(StringUtils.isEmpty(resource.getRichText())==true?"":resource.getRichText());
						}else{
							resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourceBean.setContent(StringUtils.isEmpty(resource.getPdfUrl())==true?"":propMap.get("fda.smd.resource.pdfPath")+resource.getPdfUrl());
						}
						resourceBean.setResourcesId(resource.getId() == null?"":String.valueOf(resource.getId()));
						resourceBean.setKey(resource.getId() == null?"":String.valueOf(resource.getId()));
						resourceBeanList.add(resourceBean);
					}
					gatewayInfoResponse.setResources(resourceBeanList);
				}
			}
			gatewayInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - gatewayAppResourcesInfo() :: ERROR", e);
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
		String studyListQuery = "";
		try{
			platformType = StudyMetaDataUtil.platformType(authorization);
			if(StringUtils.isNotEmpty(platformType)){
				session = sessionFactory.openSession();
				
				//fetch all Gateway studies based on the platform supported (iOS/android)
				//query = session.createQuery(" from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' ");
				/*query = session.createQuery(" from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' and SDTO.status !='"+StudyMetaDataConstants.STUDY_STATUS_PRE_LAUNCH+"' ");*/
				studyListQuery = " from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' "
										+" and SDTO.id IN (select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.basicInfo='Y' and SSDTO.overView='Y' and SSDTO.settingAdmins='Y' ) ";
				query = session.createQuery(studyListQuery);
				studiesList = query.list();
				if(null != studiesList && studiesList.size() > 0){
					List<StudyBean> studyBeanList = new ArrayList<StudyBean>();
					for(StudyDto studyDto : studiesList){
						StudyBean studyBean = new StudyBean();
						studyBean.setStudyVersion(StringUtils.isEmpty(studyDto.getStudyVersion())==true?"":studyDto.getStudyVersion());
						studyBean.setTagline(StringUtils.isEmpty(studyDto.getStudyTagline())==true?"":studyDto.getStudyTagline());
						
						//for sprint 1 if the admin completes overview, settings & admins and basic info details and marked as complete assume that the study is active 
						studyBean.setStatus(StudyMetaDataConstants.STUDY_STATUS_ACTIVE.toLowerCase());
						//studyBean.setStatus(StringUtils.isEmpty(studyDto.getStatus())==true?"":studyDto.getStatus());
						
						studyBean.setTitle(StringUtils.isEmpty(studyDto.getName())==true?"":studyDto.getName());
						studyBean.setLogo(StringUtils.isEmpty(studyDto.getThumbnailImage())==true?"":propMap.get("fda.smd.study.thumbnailPath")+studyDto.getThumbnailImage());
						//set the primary key of studies table
						/*if(null != studyDto.getId()){
							studyBean.setStudyId(String.valueOf(studyDto.getId()));
						}*/
						
						//set the custom studyid of studies table
						studyBean.setStudyId(StringUtils.isEmpty(studyDto.getCustomStudyId())==true?"":studyDto.getCustomStudyId());
						
						//get category and sponser details
						if(StringUtils.isNotEmpty(studyDto.getCategory()) && StringUtils.isNotEmpty(studyDto.getResearchSponsor())){
							List<ReferenceTablesDto> referenceTablesList = null;
							query = session.createQuery(" from ReferenceTablesDto RTDTO where RTDTO.id IN ("+studyDto.getCategory()+","+studyDto.getResearchSponsor()+")");
							referenceTablesList = query.list();
							if(null != referenceTablesList && referenceTablesList.size() > 0){
								for(ReferenceTablesDto reference : referenceTablesList){
									if(reference.getCategory().equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)){
										studyBean.setCategory(StringUtils.isEmpty(reference.getValue())==true?"":reference.getValue());
									}else{
										studyBean.setSponsorName(StringUtils.isEmpty(reference.getValue())==true?"":reference.getValue());
									}
								}
							}
						}
						
						//study settings details
						SettingsBean settings = new SettingsBean();
						if(studyDto.getPlatform().contains(",")){
							settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_BOTH);
						}else{
							switch (studyDto.getPlatform()) {
							case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS:	settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_IOS);
																					break;
							case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID:	settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID);
																						break;
							}
						}
						if(StringUtils.isNotEmpty(studyDto.getAllowRejoin()) && studyDto.getAllowRejoin().equalsIgnoreCase(StudyMetaDataConstants.YES)){
							settings.setRejoin(true);
						}else{
							settings.setRejoin(false);
						}
						if(StringUtils.isNotEmpty(studyDto.getEnrollingParticipants()) && studyDto.getEnrollingParticipants().equalsIgnoreCase(StudyMetaDataConstants.YES)){
							settings.setEnrolling(true);
						}else{
							settings.setEnrolling(false);
						}
						studyBean.setSettings(settings);
						studyBeanList.add(studyBean);
					}
					studyResponse.setStudies(studyBeanList);
				}
				studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyList() :: ERROR", e);
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
		List<ConsentMasterInfoDto> consentMasterInfoList = null;
		ConsentDetailsBean consent = new ConsentDetailsBean();
		Integer actualStudyId = null;
		StudySequenceDto studySequenceDto = null;
		try{
			session = sessionFactory.openSession();
			
			//get studyId from studies table
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//get the studySequence details by studyId to check the status of the modules for a study
				query = session.getNamedQuery("getStudySequenceDetailsByStudyId").setInteger("studyId", actualStudyId);
				studySequenceDto = (StudySequenceDto) query.uniqueResult();
				if(studySequenceDto != null){

					//Check whether Eligibility module is done or not
					if(studySequenceDto.getEligibility().equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)){
						//Eligibility details by studyId
						query = session.getNamedQuery("eligibilityDtoByStudyId").setInteger("studyId", actualStudyId);
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
							
							//set correctAnswers details for eligibility
							List<HashMap<String,Object>> correctAnswers = new ArrayList<HashMap<String,Object>>();
							HashMap<String,Object> correctAnsHashMap = new HashMap<String, Object>();
							correctAnsHashMap.put("key", "");
							correctAnsHashMap.put("answer", false);
							correctAnswers.add(correctAnsHashMap);
							eligibility.setCorrectAnswers(correctAnswers);
							eligibilityConsentResponse.setEligibility(eligibility);
						}
					}
					
					//Consent Details
					//in MS-1 and MS-2 check whether the consent module is marked as complete/not
					query = session.getNamedQuery("consentDtoByStudyId").setInteger("studyId", actualStudyId);
					consentDto = (ConsentDto) query.uniqueResult();
					if( null != consentDto){
						if(consentDto.getVersion() != null){
							consent.setVersion(consentDto.getVersion().toString());
						}
						
						//Sharing
						SharingBean sharingBean = new SharingBean();
						sharingBean.setTitle(StringUtils.isEmpty(consentDto.getTitle())==true?"":consentDto.getTitle());
						sharingBean.setText(StringUtils.isEmpty(consentDto.getTaglineDescription())==true?"":consentDto.getTaglineDescription());
						sharingBean.setLearnMore(StringUtils.isEmpty(consentDto.getLearnMoreText())==true?"":consentDto.getLearnMoreText());
						sharingBean.setLongDesc(StringUtils.isEmpty(consentDto.getLongDescription())==true?"":consentDto.getLongDescription());
						sharingBean.setShortDesc(StringUtils.isEmpty(consentDto.getShortDescription())==true?"":consentDto.getShortDescription());
						if(consentDto.getAllowWithoutPermission() != null && consentDto.getAllowWithoutPermission() == 1){
							sharingBean.setAllowWithoutSharing(true);
						}
						consent.setSharing(sharingBean);
					}
					
					//get consent master info list
					query = session.createQuery(" from ConsentMasterInfoDto CMIDTO");
					consentMasterInfoList = query.list();
					
					//Check whether ConsentEduInfo module is done or not
					if(studySequenceDto.getConsentEduInfo().equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)){
						//get Consent Info details by consentId
						query = session.getNamedQuery("consentInfoDtoByStudyId").setInteger("studyId", actualStudyId);
						consentInfoDtoList = query.list();
						if( null != consentInfoDtoList && consentInfoDtoList.size() > 0){
							List<ConsentBean> consentBeanList = new ArrayList<ConsentBean>();
							for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
								ConsentBean consentBean = new ConsentBean();
								consentBean.setText(StringUtils.isEmpty(consentInfoDto.getBriefSummary())==true?"":consentInfoDto.getBriefSummary());
								consentBean.setTitle(StringUtils.isEmpty(consentInfoDto.getDisplayTitle())==true?"":consentInfoDto.getDisplayTitle());
								if(consentInfoDto.getConsentItemTitleId() != null){
									if(consentMasterInfoList != null && consentMasterInfoList.size() > 0){
										for(ConsentMasterInfoDto masterInfo : consentMasterInfoList){
											if(masterInfo.getId().intValue() == consentInfoDto.getConsentItemTitleId().intValue()){
												consentBean.setType(masterInfo.getCode());
												break;
											}
										}
									}
								}else{
									consentBean.setType(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM.toLowerCase());
								}
								consentBean.setDescription("");
								consentBean.setHtml(StringUtils.isEmpty(consentInfoDto.getElaborated())==true?"":consentInfoDto.getElaborated());
								consentBean.setUrl(StringUtils.isEmpty(consentInfoDto.getUrl())==true?"":consentInfoDto.getUrl());
								//Yes=true and No=false
								if(StringUtils.isNotEmpty(consentInfoDto.getVisualStep()) && consentInfoDto.getVisualStep().equalsIgnoreCase(StudyMetaDataConstants.YES)){
									consentBean.setVisualStep(true);
								}else{
									consentBean.setVisualStep(false);
								}
								consentBeanList.add(consentBean);
							}
							consent.setVisualScreens(consentBeanList);
						}
					}
					
					//Check whether Comprehension List module is done or not
					if(studySequenceDto.getComprehensionTest().equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)){
						//Comprehension Question Details
						query = session.getNamedQuery("comprehensionQuestionByStudyId").setInteger("studyId", actualStudyId);
						comprehensionQuestionList = query.list();
						if( null != comprehensionQuestionList && comprehensionQuestionList.size() > 0){
							ComprehensionDetailsBean comprehensionDetailsBean = new ComprehensionDetailsBean();
							//set pass score for comprehension test
							if(consentDto != null && consentDto.getComprehensionTestMinimumScore() != null){
								comprehensionDetailsBean.setPassScore(consentDto.getComprehensionTestMinimumScore());
							}else{
								comprehensionDetailsBean.setPassScore(0);
							}
							
							List<ComprehensionBean> comprehensionList = new ArrayList<ComprehensionBean>();
							for(ComprehensionTestQuestionDto comprehensionQuestionDto : comprehensionQuestionList){
								QuestionStepStructureBean questionStepStructure = new QuestionStepStructureBean();
								ComprehensionBean comprehensionBean = new ComprehensionBean();
								questionStepStructure.setTitle(StringUtils.isEmpty(comprehensionQuestionDto.getQuestionText())==true?"":comprehensionQuestionDto.getQuestionText());
								comprehensionBean.setQuestionStepStructureBean(questionStepStructure);
								comprehensionList.add(comprehensionBean);
							}
							comprehensionDetailsBean.setQuestions(comprehensionList);
							
							List<CorrectAnswersBean> correctAnswerBeanList = new ArrayList<CorrectAnswersBean>();
							CorrectAnswersBean correctAnswerBean = new CorrectAnswersBean();
							correctAnswerBeanList.add(correctAnswerBean);
							comprehensionDetailsBean.setCorrectAnswers(correctAnswerBeanList);
							consent.setComprehension(comprehensionDetailsBean);
						}
					}
					
					//Review
					if( consentDto != null){
						ReviewBean reviewBean = new ReviewBean();
						if(consentDto.getConsentDocType().equals(StudyMetaDataConstants.CONSENT_DOC_TYPE_NEW)){
							reviewBean.setSignatureContent(StringUtils.isEmpty(consentDto.getConsentDocContent())==true?"":consentDto.getConsentDocContent());
						}else{
							/*String signatureContent = "";
							if( consentInfoDtoList != null && consentInfoDtoList.size() > 0){
								for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
									if( StringUtils.isNotEmpty(consentInfoDto.getConsentItemType()) && !consentInfoDto.getConsentItemType().equalsIgnoreCase(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM)){
										//get the actual display title based on the constant
										consentInfoDto.setDisplayTitle(getconsentDocumentDisplayTitle(consentInfoDto.getDisplayTitle()));
									}

									//get the review content from the individual consents
									signatureContent += "<span style=&#34;font-size:20px;&#34;><strong>"
														+consentInfoDto.getDisplayTitle()
														+"</strong></span><br/>"
														+"<span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>"
														+consentInfoDto.getElaborated()
														+"</span><br/>";
								}
							}
							reviewBean.setSignatureContent(signatureContent);*/
						}
						reviewBean.setSignatureTitle("");
						reviewBean.setReasonForConsent("");
						consent.setReview(reviewBean);
					}
					eligibilityConsentResponse.setConsent(consent);
				
					eligibilityConsentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
			}else{
				eligibilityConsentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - eligibilityConsentMetadata() :: ERROR", e);
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - eligibilityConsentMetadata() :: Ends");
		return eligibilityConsentResponse;
	}
	
	/**
	 * 
	 * @author Mohan
	 * @param studyId
	 * @param consentVersion
	 * @param activityId
	 * @param activityVersion
	 * @return ConsentDocumentResponse
	 * @throws DAOException
	 */
	public ConsentDocumentResponse consentDocument(String studyId, String consentVersion, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - consentDocument() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		ConsentDto consent = null;
		Integer actualStudyId = null;
		String consentQuery = "from ConsentDto CDTO where CDTO.studyId=";
		List<ConsentInfoDto> consentInfoDtoList = null;
		try{
			session = sessionFactory.openSession();
			//get studyId from studies table
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				/*if(StringUtils.isNotEmpty(consentVersion)){
					//get the consent details by consent version and studyId
					query = session.createQuery(" from ConsentDto CDTO where CDTO.studyId="+actualStudyId+" and CDTO.version="+consentVersion);
					consent = (ConsentDto) query.uniqueResult();
				}else if(StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)){
					//get the consent details by activityId and activity version
				}*/
				
				/*if(StringUtils.isNotEmpty(consentVersion)){
					consentQuery += actualStudyId+" and CDTO.version="+consentVersion;
				}else{
					consentQuery += actualStudyId;
				}*/
				consentQuery += actualStudyId;
				query = session.createQuery(consentQuery);
				consent = (ConsentDto) query.uniqueResult();
				
				//check the consentBo is empty or not
				if( consent != null){
					ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
					consentDocumentBean.setType("text/html");
					if(consent.getVersion() != null){
						consentDocumentBean.setVersion(consent.getVersion().toString());
					}else{
						consentDocumentBean.setVersion("1.0");
					}
					
					if(consent.getConsentDocType().equals(StudyMetaDataConstants.CONSENT_DOC_TYPE_NEW)){
						consentDocumentBean.setContent(StringUtils.isEmpty(consent.getConsentDocContent())==true?"":consent.getConsentDocContent());
					}else{
						query = session.getNamedQuery("consentInfoDtoByStudyId").setInteger("studyId", actualStudyId);
						consentInfoDtoList = query.list();
						String content = "";
						if( consentInfoDtoList != null && consentInfoDtoList.size() > 0){
							for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
								if( StringUtils.isNotEmpty(consentInfoDto.getConsentItemType()) && !consentInfoDto.getConsentItemType().equalsIgnoreCase(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM)){
									//get the actual display title based on the constant 
									consentInfoDto.setDisplayTitle(getconsentDocumentDisplayTitle(consentInfoDto.getDisplayTitle()));
								}

								content += "<span style=&#34;font-size:20px;&#34;><strong>"
													+consentInfoDto.getDisplayTitle()
													+"</strong></span><br/>"
													+"<span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>"
													+consentInfoDto.getElaborated()
													+"</span><br/>";
							}
							consentDocumentBean.setContent(content);
						}
					}
					consentDocumentResponse.setConsent(consentDocumentBean);
				}
				consentDocumentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				consentDocumentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - consentDocument() :: ERROR", e);
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - consentDocument() :: Ends");
		return consentDocumentResponse;
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
		Integer actualStudyId = null;
		StudyDto studyDetails = null;
		try{
			session = sessionFactory.openSession();
			//get studyId from studies table
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				query = session.getNamedQuery("getResourcesListByStudyId").setInteger("studyId", actualStudyId);
				resourcesDtoList = query.list();
				if( null != resourcesDtoList && resourcesDtoList.size() > 0){
					List<ResourcesBean> resourcesBeanList = new ArrayList<ResourcesBean>();
					for(ResourcesDto resourcesDto : resourcesDtoList){
						ResourcesBean resourcesBean = new ResourcesBean();
						studyDetails = (StudyDto) session.getNamedQuery("studyDetailsByStudyId").setInteger("id", actualStudyId).uniqueResult();
						if(studyDetails != null){
							if(studyDetails.getType().equalsIgnoreCase(StudyMetaDataConstants.STUDY_TYPE_GT)){
								resourcesBean.setAudience(StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_ALL);
							}else{
								//need to check based on the paticipants
								resourcesBean.setAudience(StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_LIMITED);
							}
						}
						
						resourcesBean.setKey(String.valueOf(resourcesDto.getId()));
						resourcesBean.setTitle(StringUtils.isEmpty(resourcesDto.getTitle())==true?"":resourcesDto.getTitle());
						if(null != resourcesDto.getTextOrPdf() && resourcesDto.getTextOrPdf() == 0){
							resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getRichText())==true?"":resourcesDto.getRichText());
						}else{
							resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getPdfUrl())==true?"":propMap.get("fda.smd.resource.pdfPath")+resourcesDto.getPdfUrl());
						}
						resourcesBean.setResourcesId(resourcesDto.getId() == null?"":String.valueOf(resourcesDto.getId()));
						
						//configuration details for the study
						ConfigurationBean configuration = new ConfigurationBean();
						if(resourcesDto.getTimePeriodFromDays() != null){
							configuration.setStart(resourcesDto.getTimePeriodFromDays());
						}else{
							configuration.setStart(0);
						}
						
						if(resourcesDto.getTimePeriodToDays() != null){
							configuration.setEnd(resourcesDto.getTimePeriodToDays());
						}else{
							configuration.setEnd(0);
						}
						resourcesBean.setStudyId(studyId);
						resourcesBean.setConfiguration(configuration);
						resourcesBeanList.add(resourcesBean);
					}
					resourcesResponse.setResources(resourcesBeanList);
				}
				resourcesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				resourcesResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - resourcesForStudy() :: ERROR", e);
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
		List<StudyPageDto> studyPageDtoList = null;
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//get study welcome info and page details by studyId
				query = session.getNamedQuery("studyDetailsByStudyId").setInteger("id", actualStudyId);
				//query = session.createQuery(" from StudyDto SDTO where SDTO.id="+studyId);
				studyDto = (StudyDto) query.uniqueResult();
				if(null != studyDto){
					studyInfoResponse.setStudyWebsite(StringUtils.isEmpty(studyDto.getStudyWebsite())==true?"":studyDto.getStudyWebsite());
					List<InfoBean> infoList = new ArrayList<InfoBean>();
					query = session.getNamedQuery("studyPageDetailsByStudyId").setInteger("studyId", actualStudyId);
					studyPageDtoList = query.list();
					if( null != studyPageDtoList && studyPageDtoList.size() > 0){
						for(StudyPageDto studyPageInfo : studyPageDtoList){
							InfoBean info = new InfoBean();
							if(infoList.size() == 0){
								info.setType(StudyMetaDataConstants.TYPE_VIDEO);
								info.setVideoLink(StringUtils.isEmpty(studyDto.getMediaLink())==true?"":studyDto.getMediaLink());
							}else{
								info.setType(StudyMetaDataConstants.TYPE_TEXT);
								info.setVideoLink("");
							}
							info.setTitle(StringUtils.isEmpty(studyPageInfo.getTitle())==true?"":studyPageInfo.getTitle());
							info.setImage(StringUtils.isEmpty(studyPageInfo.getImagePath())==true?"":propMap.get("fda.smd.study.pagePath")+studyPageInfo.getImagePath());
							info.setText(StringUtils.isEmpty(studyPageInfo.getDescription())==true?"":studyPageInfo.getDescription());
							infoList.add(info);
						}
					}else{
						//for MS1 default value is added for study page1
						InfoBean info = new InfoBean();
						if(infoList.size() == 0){
							info.setType(StudyMetaDataConstants.TYPE_VIDEO);
							info.setVideoLink(StringUtils.isEmpty(studyDto.getMediaLink())==true?"":studyDto.getMediaLink());
						}else{
							info.setType(StudyMetaDataConstants.TYPE_TEXT);
							info.setVideoLink("");
						}
						//info.setTitle("A Study for Pregnent Women");
						info.setTitle(StringUtils.isEmpty(studyDto.getName())==true?"":studyDto.getName());
						info.setImage(StringUtils.isEmpty(studyDto.getThumbnailImage())==true?"":propMap.get("fda.smd.study.thumbnailPath")+studyDto.getThumbnailImage());
						//info.setText("Collection of participant-provided information through a mobile device app for use in drug safety research");
						info.setText(StringUtils.isEmpty(studyDto.getFullName())==true?"":studyDto.getFullName());
						infoList.add(info);
					}
					studyInfoResponse.setInfo(infoList);
					
					//withdraw details
					WithdrawalConfigBean withdrawConfig = new WithdrawalConfigBean();
					switch (studyDto.getRetainParticipant()) {
					case StudyMetaDataConstants.YES: withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_NO_ACTION);
													 break;
					case StudyMetaDataConstants.NO: withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_DELETE_DATA);
													break;
					case StudyMetaDataConstants.ALL: withdrawConfig.setType(StudyMetaDataConstants.STUDY_WITHDRAW_CONFIG_ASK_USER);
													 break;
					}
					withdrawConfig.setMessage(StringUtils.isEmpty(studyDto.getAllowRejoinText())==true?"":studyDto.getAllowRejoinText());
					studyInfoResponse.setWithdrawalConfig(withdrawConfig);
				}
				
				studyInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				studyInfoResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyInfo() :: ERROR", e);
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
		List<QuestionnairesDto> questionnairesList = null;
		List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();
			
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//get the Activities (type : Active Task list) by studyId
				query = session.getNamedQuery("activeTaskByStudyId").setInteger("studyId", actualStudyId);
				/*query = session.createQuery(" from ActiveTaskDto ATDTO where ATDTO.studyId in ( select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.studyId="+actualStudyId+" and SSDTO.studyExcActiveTask='Y' )");*/
				activeTaskDtoList = query.list();
				if( null != activeTaskDtoList && activeTaskDtoList.size() > 0){
					for(ActiveTaskDto activeTaskDto : activeTaskDtoList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(activeTaskDto.getTaskTitle())==true?"":activeTaskDto.getTaskTitle());
						activityBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
						
						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						/*if(StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeEnd())){
							frequencyDetails = getFrequencyRunsDetailsForActiveTasks(activeTaskDto, frequencyDetails, session);
						}*/
						frequencyDetails = getFrequencyRunsDetailsForActiveTasks(activeTaskDto, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(activeTaskDto.getFrequency())==true?"":activeTaskDto.getFrequency());
						activityBean.setFrequency(frequencyDetails);
						
						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
						activityBean.setStartTime(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeStart())==true?"":activeTaskDto.getActiveTaskLifetimeStart());
						activityBean.setEndTime(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())==true?"":activeTaskDto.getActiveTaskLifetimeEnd());
						activitiesBeanList.add(activityBean);
					}
				}
				
				//get the Activities (type : Questionaires list) by studyId
				query = session.getNamedQuery("questionnairesListByStudyId").setInteger("studyId", actualStudyId);
				/*query = session.createQuery(" from QuestionnairesDto QDTO where QDTO.studyId in ( select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.studyId="+actualStudyId+" and SSDTO.studyExcQuestionnaries='Y' )");*/
				questionnairesList = query.list();
				if( questionnairesList != null && questionnairesList.size() > 0){
					for(QuestionnairesDto questionaire : questionnairesList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(questionaire.getTitle())==true?"":questionaire.getTitle());
						activityBean.setType(StudyMetaDataConstants.TYPE_QUESTIONNAIRE);
						
						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						/*if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
							
						}*/
						frequencyDetails = getFrequencyRunsDetailsForQuestionaires(questionaire, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(questionaire.getFrequency())==true?"":questionaire.getFrequency());
						activityBean.setFrequency(frequencyDetails);
						
						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionaire.getId());
						activityBean.setStartTime(StringUtils.isEmpty(questionaire.getStudyLifetimeStart())==true?"":questionaire.getStudyLifetimeStart());
						activityBean.setEndTime(StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())==true?"":questionaire.getStudyLifetimeEnd());
						
						activitiesBeanList.add(activityBean);
					}
				}
				
				//check the activities list for the studyId
				if(activitiesBeanList.size() > 0){
					activityResponse.setActivities(activitiesBeanList);
				}
				activityResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				activityResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyActivityList() :: ERROR", e);
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
	@SuppressWarnings("unchecked")
	public ActivityMetaDataResponse studyActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		ActivityStructureBean activityStructureBean = new ActivityStructureBean();
		Map<String, Integer> sequenceNoMap = new HashMap<String, Integer>();
		TreeMap<Integer, ActivityStepsBean> stepsSequenceTreeMap = new TreeMap<Integer, ActivityStepsBean>();
		List<ActivityStepsBean> steps = new ArrayList<ActivityStepsBean>();
		ActiveTaskDto activeTaskDto = null;
		QuestionnairesDto questionnaireDto = null;
		Integer actualStudyId = null;
		String[] activityInfoArray = null;
		List<QuestionnairesStepsDto> questionaireStepsList = null;
		//List<ActiveTaskStepsDto> activeTaskStepsList = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//check whether the activityId is valid or not (i.e. delimeter '-')
				if(activityId.contains("-")){
					activityInfoArray = activityId.split("-");
					if(activityInfoArray[0].equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)){
						query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.id ="+activityInfoArray[1]);
						activeTaskDto = (ActiveTaskDto) query.uniqueResult();
						if( activeTaskDto != null){
							List<Integer> taskMasterAttrIdList = new ArrayList<Integer>();
							List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList = null;
							List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;
							List<ActiveTaskListDto> activeTaskList = null;
							
							activityStructureBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
							
							ActivityMetadataBean metadata = new ActivityMetadataBean();
							metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
							metadata.setEndDate(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())==true?"":activeTaskDto.getActiveTaskLifetimeEnd());
							metadata.setLastModified(""); //column not there in the database
							metadata.setName(StringUtils.isEmpty(activeTaskDto.getTaskName())==true?"":activeTaskDto.getTaskName());
							metadata.setStartDate(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeStart())==true?"":activeTaskDto.getActiveTaskLifetimeStart());
							metadata.setStudyId(studyId);
							metadata.setVersion(""); //column not there in the database
							activityStructureBean.setMetadata(metadata);
							
							/*query = session.createQuery("from ActiveTaskStepsDto ATSDTO where ATSDTO.activeTaskId="+activeTaskDto.getId()+" ORDER BY ATSDTO.sequenceNo");
							activeTaskStepsList = query.list();
							if(activeTaskStepsList != null && activeTaskStepsList.size() > 0){
								for(ActiveTaskStepsDto activeTaskStepsDto : activeTaskStepsList){
									
								}
							}*/
							
							//get the active task attribute values based on the activityId
							query = session.createQuery(" from ActiveTaskAttrtibutesValuesDto ATAVDTO where ATAVDTO.activeTaskId="+activeTaskDto.getId());
							activeTaskAttrtibuteValuesList = query.list();
							if( activeTaskAttrtibuteValuesList != null && activeTaskAttrtibuteValuesList.size() > 0){
								for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
									taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
								}
								
								if(taskMasterAttrIdList != null && taskMasterAttrIdList.size() > 0){
									//get the active task master info based on the active task attribute ids
									query = session.createQuery(" from ActiveTaskMasterAttributeDto ATMADTO where ATMADTO.masterId in ("+StringUtils.join(taskMasterAttrIdList, ",")+")");
									activeTaskMaterList = query.list();
								}
								
								//get the active task list details
								query = session.createQuery(" from ActiveTaskListDto ATDTO ");
								activeTaskList = query.list();
							}
							
							//check the active task details are exists or not
							if((activeTaskAttrtibuteValuesList != null && activeTaskAttrtibuteValuesList.size() > 0) && (activeTaskMaterList != null && activeTaskMaterList.size() > 0) && (activeTaskList != null && activeTaskList.size() > 0)){
								//get the steps details based on the activity type
								for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
									for(ActiveTaskMasterAttributeDto masterAttributeDto : activeTaskMaterList){
										if(attributeDto.getActiveTaskMasterAttrId().intValue() == masterAttributeDto.getMasterId().intValue()){
											for(ActiveTaskListDto taskDto : activeTaskList){
												if(taskDto.getActiveTaskListId().intValue() == masterAttributeDto.getTaskTypeId().intValue()){
													ActivityStepsBean activeTaskStep = new ActivityStepsBean();
													activeTaskStep.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
													activeTaskStep.setResultType(StringUtils.isEmpty(taskDto.getType())==true?"":taskDto.getType());
													activeTaskStep.setKey(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
													activeTaskStep.setText(StringUtils.isEmpty(masterAttributeDto.getDisplayName())==true?"":masterAttributeDto.getDisplayName());
													activeTaskStep.setOptions(null);
													activeTaskStep.setFormat(getActiveTaskStepFormatByType(attributeDto, masterAttributeDto, taskDto.getType()));
													steps.add(activeTaskStep);
												}
											}
										}
									}
								}
								activityStructureBean.setSteps(steps);
							}
						}
					}else{
						query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id ="+activityInfoArray[1]);
						questionnaireDto = (QuestionnairesDto) query.uniqueResult();
						if(questionnaireDto != null){
							activityStructureBean.setType(StudyMetaDataConstants.TYPE_QUESTIONNAIRE);
							
							ActivityMetadataBean metadata = new ActivityMetadataBean();
							metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId());
							metadata.setEndDate(StringUtils.isEmpty(questionnaireDto.getStudyLifetimeEnd())==true?"":questionnaireDto.getStudyLifetimeEnd());
							metadata.setLastModified(StringUtils.isEmpty(questionnaireDto.getModifiedDate())==true?"":questionnaireDto.getModifiedDate());
							metadata.setName(StringUtils.isEmpty(questionnaireDto.getTitle())==true?"":questionnaireDto.getTitle());
							metadata.setStartDate(StringUtils.isEmpty(questionnaireDto.getStudyLifetimeStart())==true?"":questionnaireDto.getStudyLifetimeStart());
							metadata.setStudyId(studyId);
							metadata.setVersion(""); //column not there in the database
							activityStructureBean.setMetadata(metadata);
							
							query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" ORDER BY QSDTO.sequenceNo");
							questionaireStepsList = query.list();
							if(questionaireStepsList != null && questionaireStepsList.size() > 0){
								List<Integer> instructionIdList = new ArrayList<Integer>();
								List<Integer> questionIdList = new ArrayList<Integer>();
								List<Integer> formIdList = new ArrayList<Integer>();
								for(QuestionnairesStepsDto questionaireSteps : questionaireStepsList){
									switch (questionaireSteps.getStepType()) {
										case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION: instructionIdList.add(questionaireSteps.getInstructionFormId());
																										sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, questionaireSteps.getSequenceNo());
																										break;
										case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION: questionIdList.add(questionaireSteps.getInstructionFormId());
																										sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, questionaireSteps.getSequenceNo());
																										break;
										case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM: formIdList.add(questionaireSteps.getInstructionFormId());
																										sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, questionaireSteps.getSequenceNo());
																										break;
									}
								}
								
								//get the instructionsList
								if(instructionIdList.size() > 0){
									List<InstructionsDto> instructionsDtoList = null;
									query = session.createQuery(" from InstructionsDto IDTO where IDTO.id in ("+StringUtils.join(instructionIdList, ",")+")");
									instructionsDtoList = query.list();
									if(instructionsDtoList != null && instructionsDtoList.size() > 0){
										stepsSequenceTreeMap = getStepsInfoForQuestionaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, instructionsDtoList, null, null, sequenceNoMap, stepsSequenceTreeMap, session);
									}
								}
								
								//get the questionaire List
								if(questionIdList.size() > 0){
									List<QuestionsDto> questionsList = null;
									query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdList, ",")+")");
									questionsList = query.list();
									if( questionsList != null && questionsList.size() > 0){
										stepsSequenceTreeMap = getStepsInfoForQuestionaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, null, questionsList, null, sequenceNoMap, stepsSequenceTreeMap, session);
									}
								}
								
								//get the forms list
								if(formIdList.size() > 0){
									List<FormMappingDto> formList = null;
									//query = session.createQuery(" from FormDto FDTO where FDTO.formId in ("+StringUtils.join(formIdList, ",")+")");
									query = session.createQuery(" from FormMappingDto FMDTO where FMDTO.formId in ("+StringUtils.join(formIdList, ",")+")");
									formList = query.list();
									if(formList != null && formList.size() > 0){
										stepsSequenceTreeMap = getStepsInfoForQuestionaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, null, null, formList, sequenceNoMap, stepsSequenceTreeMap, session);
									}
								}
								
								//iterate the treemap to get the activities based on the sequence order
								for(Integer key : stepsSequenceTreeMap.keySet()){
									steps.add(stepsSequenceTreeMap.get(key));
								}
								
								activityStructureBean.setSteps(steps);
							}
						}
					}
					
					activityMetaDataResponse.setActivity(activityStructureBean);
					activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
			}else{
				activityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyActivityMetadata() :: ERROR", e);
		}finally{
			if(null != session){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityMetadata() :: Ends");
		return activityMetaDataResponse;
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
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				DashboardBean dashboard = new DashboardBean();
				List<ChartsBean> charts = new ArrayList<ChartsBean>();
				ChartsBean cbean = new ChartsBean();
				cbean.setConfiguration(singleLineChartDetails());
				charts.add(cbean);
				/*ChartsBean cbean1 = new ChartsBean();
				cbean1.setConfiguration(multipleLineChartDetails());
				charts.add(cbean1);
				
				ChartsBean cbean2 = new ChartsBean();
				cbean2.setConfiguration(uniquePieChartDetails());
				charts.add(cbean2);
				
				ChartsBean cbean3 = new ChartsBean();
				cbean3.setConfiguration(rangePieChartDetails());
				charts.add(cbean3);
				
				ChartsBean cbean4 = new ChartsBean();
				cbean4.setConfiguration(singleBarChartDetails());
				charts.add(cbean4);
				
				ChartsBean cbean5 = new ChartsBean();
				cbean5.setConfiguration(multipleBarChartDetails());
				charts.add(cbean5);*/
				dashboard.setCharts(charts);
				List<StatisticsBean> statisticsList = new ArrayList<StatisticsBean>();
				StatisticsBean statistics = new StatisticsBean();
				statisticsList.add(statistics);
				dashboard.setStatistics(statisticsList);
				studyDashboardResponse.setDashboard(dashboard);
				studyDashboardResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				studyDashboardResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyDashboardInfo() :: ERROR", e);
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
			termsPolicyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			termsPolicyResponse.setPrivacy(propMap.get("fda.smd.pricaypolicy"));
			termsPolicyResponse.setTerms(propMap.get("fda.smd.terms"));
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - termsPolicy() :: ERROR", e);
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
		}
		LOGGER.info("INFO: StudyMetaDataDao - notifications() :: Ends");
		return notificationsResponse;
	}
	
	/*------------------------------Common methods starts------------------------------*/
	/**
	 * @author Mohan
	 * @param displaytitle
	 * @return consentTitle
	 */
	public String getconsentDocumentDisplayTitle(String displaytitle) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getconsentDocumentDisplayTitle() :: Starts");
		String consentTitle = "";
		try {
			consentTitle = displaytitle;
			switch (displaytitle) {
					case "overview": consentTitle = "Overview";
									 break;
					case "dataGathering": consentTitle = "Data Gathering";
					 				 break;
					case "privacy": consentTitle = "Privacy";
					 				 break;
					case "dataUse": consentTitle = "Data Use";
					 				 break;
					case "timeCommitment": consentTitle = "Time Commitment";
					 				 break;
					case "studySurvey": consentTitle = "Study Survey";
					 				 break;
					case "studyTasks": consentTitle = "Study Tasks";
					 				 break;
					case "withdrawing": consentTitle = "Withdrawing";
					 				 break;
					case "customService": consentTitle = "Custom Service";
					 				 break;
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataDao - getconsentDocumentDisplayTitle() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getconsentDocumentDisplayTitle() :: Ends");
		return consentTitle;
	}
	/*------------------------------Common methods starts------------------------------*/
	/*-----------------------------Activity data methods starts----------------------------------*/
	/**
	 * This method is used to get the Activivty Steps details based on the sequence order no in quationaire steps
	 * 
	 * @author Mohan
	 * @param type
	 * @param instructionsDtoList
	 * @param questionsDtoList
	 * @param formsList
	 * @param sequenceNoMap
	 * @param stepsSequenceTreeMap
	 * @return
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<Integer, ActivityStepsBean> getStepsInfoForQuestionaires(String type, List<InstructionsDto> instructionsDtoList, List<QuestionsDto> questionsDtoList, List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, TreeMap<Integer, ActivityStepsBean> stepsSequenceTreeMap, Session session) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getStepsInfoForQuestionaires() :: Starts");
		TreeMap<Integer, ActivityStepsBean> stepsSequenceOrderTreeMap = new TreeMap<Integer, ActivityStepsBean>();
		try{
			if(type.equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)){
				if( instructionsDtoList != null && instructionsDtoList.size() > 0){
					for(InstructionsDto instructionsDto : instructionsDtoList){
						ActivityStepsBean instructionBean = new ActivityStepsBean();
						
						instructionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
						instructionBean.setResultType(null);
						instructionBean.setKey((instructionsDto.getId() == null || instructionsDto.getId() == 0) ?"":instructionsDto.getId().toString());
						instructionBean.setTitle(StringUtils.isEmpty(instructionsDto.getInstructionTitle())==true?"":instructionsDto.getInstructionTitle());
						instructionBean.setText(StringUtils.isEmpty(instructionsDto.getInstructionText())==true?"":instructionsDto.getInstructionText());
						instructionBean.setSkippable(false);
						instructionBean.setGroupName(null);
						instructionBean.setRepeatable(false);
						instructionBean.setRepeatableText(null);
						
						List<String[]> destinations = new ArrayList<String[]>();
						instructionBean.setDestinations(destinations);
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(instructionsDto.getId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION), instructionBean);
					}
				}
			}else if(type.equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)){
				if(questionsDtoList != null && questionsDtoList.size() > 0){
					for(QuestionsDto questionsDto : questionsDtoList){
						ActivityStepsBean questionBean = new ActivityStepsBean();
						
						questionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
						questionBean.setResultType(""); //NA
						questionBean.setKey((questionsDto.getId() == null || questionsDto.getId() == 0) ?"":questionsDto.getId().toString());
						questionBean.setTitle(StringUtils.isEmpty(questionsDto.getQuestion())==true?"":questionsDto.getQuestion());
						questionBean.setSkippable(false); //NA
						questionBean.setGroupName(""); //NA
						questionBean.setRepeatable(false); //NA
						questionBean.setRepeatableText(null); //NA
						
						List<String[]> destinations = new ArrayList<String[]>();
						questionBean.setDestinations(destinations);
						
						questionBean.setHealthDataKey("");
						
						Map<String, Object> format = new HashMap<String, Object>();
						questionBean.setFormat(format);
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(questionsDto.getId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION), questionBean);
					}
				}
			}else{
				if( formsList != null && formsList.size() > 0){
					for(FormMappingDto formDto : formsList){
						ActivityStepsBean formBean = new ActivityStepsBean();
						List<ActivityStepsBean> formSteps = new ArrayList<ActivityStepsBean>();
						formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
						formBean.setResultType("");
						formBean.setKey((formDto.getId() == null || formDto.getId() == 0) ?"":formDto.getId().toString());
						formBean.setTitle("");
						formBean.setText("");
						formBean.setSkippable(false);
						formBean.setGroupName(null);
						formBean.setRepeatable(false);
						formBean.setRepeatableText("");
						
						List<String[]> destinations = new ArrayList<String[]>();
						formBean.setDestinations(destinations);
						/*get the question deatils for a form in one query 
						 * alter the query based on the column details needed
						 * select f.form_id, q.id, q.short_title, q.question from questions q, form_mapping f where q.id=f.question_id and f.form_id in (1,2);*/
						
						List<QuestionsDto> formQuestionsList = null;
						query = session.createQuery("from QuestionsDto QDTO where QDTO.id="+formDto.getQuestionId());
						formQuestionsList = query.list();
						if(formQuestionsList != null && formQuestionsList.size() > 0){
							for(QuestionsDto formQuestionDto : formQuestionsList){
								ActivityStepsBean formQuestionBean = new ActivityStepsBean();
								formQuestionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
								formQuestionBean.setResultType(""); //NA
								formQuestionBean.setKey((formQuestionDto.getId() == null || formQuestionDto.getId() == 0) ?"":formQuestionDto.getId().toString());
								formQuestionBean.setTitle(StringUtils.isEmpty(formQuestionDto.getQuestion())==true?"":formQuestionDto.getQuestion());
								formQuestionBean.setSkippable(false); //NA
								formQuestionBean.setGroupName(""); //NA
								formQuestionBean.setRepeatable(false); //NA
								formQuestionBean.setRepeatableText(null); //NA
								
								List<String[]> formQuestiondestinations = new ArrayList<String[]>();
								formQuestionBean.setDestinations(formQuestiondestinations);
								
								formQuestionBean.setHealthDataKey("");
								
								Map<String, Object> format = new HashMap<String, Object>();
								formQuestionBean.setFormat(format);
								
								formSteps.add(formQuestionBean);
							}
						}
						formBean.setSteps(formSteps);
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(formDto.getFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM), formBean);
					}
				}
			}
			
			stepsSequenceOrderTreeMap = stepsSequenceTreeMap;
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getStepsInfoForQuestionaires() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getStepsInfoForQuestionaires() :: Ends");
		return stepsSequenceOrderTreeMap;
	}
	
	/**
	 * This method is used to get the Active Task Format type based on the type of the Task
	 * 
	 * @author Mohan
	 * @param attributeValues
	 * @param masterAttributeValue
	 * @param taskType
	 * @return activeTaskFormat
	 * @throws DAOException
	 */
	public Map<String, Object> getActiveTaskStepFormatByType(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue, String taskType) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getActiveTaskStepFormatByType() :: Starts");
		Map<String, Object> activeTaskFormat = new HashMap<String, Object>();
		try{
			if(StringUtils.isNotEmpty(taskType)){
				switch (taskType) {
				case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
					activeTaskFormat.put("duration", ""); //in hours
					break;
				case StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY:
					activeTaskFormat.put("initialSpan", 0);
					activeTaskFormat.put("minimumSpan", 0);
					activeTaskFormat.put("maximumSpan", 0);
					activeTaskFormat.put("playSpeed", 0);
					activeTaskFormat.put("maximumTests", 0);
					activeTaskFormat.put("maximumConsecutiveFailures", 0);
					activeTaskFormat.put("customTargetImage", "");
					activeTaskFormat.put("customTargetPluralName", "");
					activeTaskFormat.put("requireReversal", false);
					break;
				case StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI:
					activeTaskFormat.put("numberOfDisks", ""); //Integer;
					break;
				}
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getActiveTaskStepFormatByType() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getActiveTaskStepFormatByType() :: Ends");
		return activeTaskFormat;
	}
	
	/**
	 * This method is used to get the Question format based on the type of the questionResultType
	 * 
	 * @author Mohan
	 * @param questionDto
	 * @param questionResultType
	 * @return questionFormat
	 * @throws DAOException
	 */
	public Map<String, Object> getQuestionaireQuestionFormatByType(QuestionsDto questionDto, String questionResultType) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getQuestionaireQuestionFormatByType() :: Starts");
		Map<String, Object> questionFormat = new HashMap<String, Object>();
		try{
			if(StringUtils.isNotEmpty(questionResultType)){
				switch (questionResultType) {
				case StudyMetaDataConstants.QUESTION_SCALE:
					questionFormat.put("maxValue", 0);
					questionFormat.put("minValue", 0);
					questionFormat.put("default", 0);
					questionFormat.put("step", 1);
					questionFormat.put("vertical", false);
					questionFormat.put("maxDesc", "");
					questionFormat.put("minDesc", "");
					questionFormat.put("maxImage", "");
					questionFormat.put("minImage", "");
					break;
				case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
					questionFormat.put("maxValue", 0);
					questionFormat.put("minValue", 0);
					questionFormat.put("default", 0);
					questionFormat.put("maxFractionDigits", 1);
					questionFormat.put("vertical", false);
					questionFormat.put("maxDesc", "");
					questionFormat.put("minDesc", "");
					questionFormat.put("maxImage", "");
					questionFormat.put("minImage", "");
					break;
				case StudyMetaDataConstants.QUESTION_TEXT_SCALE:
					HashMap<String, Object> textScaleMap = new HashMap<String, Object>();
					textScaleMap.put("text", "");
					textScaleMap.put("value", "");
					textScaleMap.put("detail", "");
					textScaleMap.put("exclusive", true);
					
					List<HashMap<String, Object>> textChoicesList = new ArrayList<HashMap<String,Object>>();
					textChoicesList.add(textScaleMap);
					
					questionFormat.put("textChoices", textChoicesList);
					questionFormat.put("default", 0);
					questionFormat.put("vertical", false);
					break;
				case StudyMetaDataConstants.QUESTION_VALUE_PICKER:
					HashMap<String, Object> valuePickerMap = new HashMap<String, Object>();
					valuePickerMap.put("text", "");
					valuePickerMap.put("value", "");
					valuePickerMap.put("detail", "");
					valuePickerMap.put("exclusive", true);
					
					List<HashMap<String, Object>> valuePickerList = new ArrayList<HashMap<String,Object>>();
					valuePickerList.add(valuePickerMap);
					questionFormat.put("textChoices", valuePickerList);
					break;
				case StudyMetaDataConstants.QUESTION_IMAGE_CHOICE:
					HashMap<String, Object> imageChoiceMap = new HashMap<String, Object>();
					imageChoiceMap.put("image", "");
					imageChoiceMap.put("selectedImage", "");
					imageChoiceMap.put("text", "");
					imageChoiceMap.put("value", "");
					
					List<HashMap<String, Object>> imageChoicesList = new ArrayList<HashMap<String,Object>>();
					imageChoicesList.add(imageChoiceMap);
					questionFormat.put("imageChoices", imageChoicesList);
					break;
				case StudyMetaDataConstants.QUESTION_TEXT_CHOICE:
					HashMap<String, Object> textChoiceMap = new HashMap<String, Object>();
					textChoiceMap.put("text", "");
					textChoiceMap.put("value", "");
					textChoiceMap.put("detail", "");
					textChoiceMap.put("exclusive", true);
					
					List<HashMap<String, Object>> textChoiceMapList = new ArrayList<HashMap<String,Object>>();
					textChoiceMapList.add(textChoiceMap);
					questionFormat.put("textChoices", textChoiceMapList);
					questionFormat.put("selectionStyle", ""); //Single/Multiple
					break;
				case StudyMetaDataConstants.QUESTION_NUMERIC:
					questionFormat.put("style", "");
					questionFormat.put("unit", "");
					questionFormat.put("minValue", 0);
					questionFormat.put("maxValue", 0);
					questionFormat.put("placeholder", "");
					break;
				case StudyMetaDataConstants.QUESTION_DATE:
					questionFormat.put("style", ""); //Date/Date-Time
					questionFormat.put("minDate", ""); //yyyy-MM-dd'T'HH:mm:ss.SSSZ
					questionFormat.put("maxDate", 0); //yyyy-MM-dd'T'HH:mm:ss.SSSZ
					questionFormat.put("default", 0); //Date
					break;
				case StudyMetaDataConstants.QUESTION_TEXT: 
					questionFormat.put("maxLength", 0);
					questionFormat.put("validationRegex", "");
					questionFormat.put("invalidMessage", "");
					questionFormat.put("multipleLines", false);
					questionFormat.put("placeholder", "");
					break;
				case StudyMetaDataConstants.QUESTION_EMAIL:
					questionFormat.put("placeholder", "");
					break;
				case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:  
					questionFormat.put("default", 0);
					questionFormat.put("step", 0); //In minutes 1-30
					break;
				case StudyMetaDataConstants.QUESTION_HEIGHT:  
					questionFormat.put("measurementSystem", 0); //Local/Metric/US
					questionFormat.put("placeholder", "");
					break;
				case StudyMetaDataConstants.QUESTION_LOCATION:
					questionFormat.put("useCurrentLocation", false);
					break;
				}
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getQuestionaireQuestionFormatByType() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getQuestionaireQuestionFormatByType() :: Ends");
		return questionFormat;
	}
	
	/*-----------------------------Activity data methods ends----------------------------------*/
	/*-----------------------------Manipulate chart data methods starts----------------------------------*/
	/**
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 * This method is used to fetch the chart configuration details for single line chart
	 */
	public Map<String, Object> singleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - singleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "single");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions
			
			Map<String, Object> axisColor = new HashMap<String, Object>();
			axisColor.put("x-axis", "#fff"); //hexcolor
			axisColor.put("y-axis", "#000"); //hexcolor
			configuration.put("axisColor", axisColor);
			
			configuration.put("max", 0.0d);
			configuration.put("min", 0.0d);
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles);
			configuration.put("defaultText", "");
			
			//single setting only
			List<Map<String, Object>> settingsList = new ArrayList<Map<String,Object>>();
			Map<String, Object> settings = new HashMap<String, Object>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<Double>();
			settings.put("pointValues", pointValues);
			settings.put("lineColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - singleLineChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - singleLineChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 * This method is used to fetch the chart configuration details for multiple line chart
	 */
	public Map<String, Object> multipleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - multipleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "multiple");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions
			
			Map<String, Object> axisColor = new HashMap<String, Object>();
			axisColor.put("x-axis", "#fff"); //hexcolor
			axisColor.put("y-axis", "#000"); //hexcolor
			configuration.put("axisColor", axisColor);
			
			configuration.put("max", 0.0d);
			configuration.put("min", 0.0d);
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles);
			configuration.put("defaultText", "");
			
			// more than one setting
			List<Map<String, Object>> settingsList = new ArrayList<Map<String,Object>>();
			Map<String, Object> settings = new HashMap<String, Object>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<Double>();
			settings.put("pointValues", pointValues);
			settings.put("lineColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - multipleLineChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - multipleLineChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for unique pie chart
	 */
	public Map<String, Object> uniquePieChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - uniquePieChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "unique-responses");
			configuration.put("numberOfSegments", 0); // =0, calculated at run time <number of unique responses>
			
			List<Double> values = new ArrayList<Double>();
			configuration.put("values", values); //calculated <count of each unique response / total number of responses>
			
			List<String> colors = new ArrayList<String>();
			configuration.put("colors", colors); //<default colors>
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles); //<unique response>
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - uniquePieChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - uniquePieChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for range pie chart
	 */
	public Map<String, Object> rangePieChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - rangePieChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "range-responses");
			configuration.put("numberOfSegments", 5); //Number of ranges
			
			List<Double> values = new ArrayList<Double>();
			configuration.put("values", values); //calculated <count of responses in each range / total number of responses>
			
			List<String> colors = new ArrayList<String>();
			configuration.put("colors", colors);
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles); //<unique response>
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - rangePieChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - rangePieChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for single bar chart
	 */
	public Map<String, Object> singleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - singleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "single");
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles);
			
			//single setting only
			List<Map<String, Object>> settingsList = new ArrayList<Map<String,Object>>();
			Map<String, Object> settings = new HashMap<String, Object>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<Double>();
			settings.put("pointValues", pointValues);
			settings.put("barColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - singleBarChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - singleBarChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for multiple bar chart
	 */
	public Map<String, Object> multipleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - multipleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<String, Object>();
		try{
			configuration.put("subType", "multiple");
			
			List<String> titles = new ArrayList<String>();
			configuration.put("titles", titles);
			
			//more than one setting
			List<Map<String, Object>> settingsList = new ArrayList<Map<String,Object>>();
			Map<String, Object> settings = new HashMap<String, Object>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<Double>();
			settings.put("pointValues", pointValues);
			settings.put("barColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - multipleBarChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - multipleBarChartDetails() :: Ends");
		return configuration;
	}
	
	/**
	 * @author Mohan
	 * @param questionaire
	 * @return ActivityFrequencyBean
	 * @throws DAOException
	 * 
	 * This method is used to get the frequency details for Questionaires based on the frequncy type selected i.e One Time, Within a Day, Daily, Weekly, Monthly, Manually Schedule
	 */
	public ActivityFrequencyBean getFrequencyRunsDetailsForQuestionaires(QuestionnairesDto questionaire, ActivityFrequencyBean frequencyDetails, Session session) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Starts");
		List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<ActivityFrequencyScheduleBean>();
		List<QuestionnairesFrequenciesDto> dailyFrequencyList = null;
		try{
			switch (questionaire.getFrequency()) {
				case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
					ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
					oneTimeBean.setStartTime(StringUtils.isEmpty(questionaire.getStudyLifetimeStart())==true?"":questionaire.getStudyLifetimeStart());
					oneTimeBean.setEndTime(StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())==true?"":questionaire.getStudyLifetimeEnd());
					runDetailsBean.add(oneTimeBean);
					break;
				/*case StudyMetaDataConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
					break;*/
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
						//get the list of frequency time based on the questionaire id 
						query = session.createQuery(" from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId="+questionaire.getId());
						dailyFrequencyList = query.list();
						if(dailyFrequencyList != null && dailyFrequencyList.size() > 0){
							//old approach was replaced
							Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
							String questionaireStartDate = questionaire.getStudyLifetimeStart();
							while(repeatCount > 0){
								//ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
								String questionaireEndDate = "";
								String dayEndDate = "";
								boolean flag = false;
								boolean skipLoop = false;
								
								dayEndDate = StudyMetaDataUtil.addDaysToDate(questionaireStartDate, 1);
								if(StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(dayEndDate))){
									flag = true;
								}
								
								//get the flag for valid start and end time
								if(flag){
									questionaireEndDate = dayEndDate;
									if((StudyMetaDataConstants.SDF_DATE.parse(dayEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(dayEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))){
										questionaireEndDate = questionaire.getStudyLifetimeEnd();
										skipLoop = true;
									}
									
									//get the frequency time for the daily activity
									for(QuestionnairesFrequenciesDto questionnaireFrequencyDto : dailyFrequencyList){
										ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
										dailyBean.setStartTime(questionaireStartDate+" "+questionnaireFrequencyDto.getFrequencyTime());
										dailyBean.setEndTime(questionaireEndDate+" "+questionnaireFrequencyDto.getFrequencyTime());
										/*dailyBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaireStartDate+" "+questionnaireFrequencyDto.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
										dailyBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaireEndDate+" "+questionnaireFrequencyDto.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));*/
										runDetailsBean.add(dailyBean);
									}
									//skip the loop if the enddate of questionaire is before the month end date
									if(skipLoop){
										break;
									}
								}
								
								questionaireStartDate = dayEndDate;
								repeatCount--;
							}
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd()) && StringUtils.isNotEmpty(questionaire.getDayOfTheWeek())){
						Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
						//create the runs frequency list based on the repeat questionaire count
						String questionaireDay = questionaire.getDayOfTheWeek();
						String questionaireStartDate = questionaire.getStudyLifetimeStart();
						while(repeatCount > 0){
							ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
							String questionaireEndDate = "";
							String day = "";
							String weekEndDate = "";
							boolean flag = false;
							boolean skipLoop = false;
							
							if(questionaireDay.equalsIgnoreCase(StudyMetaDataUtil.getDayByDate(questionaireStartDate))){
								day = questionaireDay;
							}
							
							if(!questionaireDay.equalsIgnoreCase(day)){
								while(!questionaireDay.equalsIgnoreCase(day)){
									questionaireStartDate = StudyMetaDataUtil.addDaysToDate(questionaireStartDate, 1);
									day = StudyMetaDataUtil.getDayByDate(questionaireStartDate);
								}
							}
							
							weekEndDate = StudyMetaDataUtil.addWeeksToDate(questionaireStartDate, 1);
							//check the current date is after the active task end date or not 
							//if not add the start and end date else send empty start and end date 
							if((StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate))) || (StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))){
								flag = true;
							}
							
							//get the flag for valid start and end time
							if(flag){
								//check the calculated end date is after the activity actual end date or not
								questionaireEndDate = weekEndDate;
								if((StudyMetaDataConstants.SDF_DATE.parse(weekEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(weekEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))){
									questionaireEndDate = questionaire.getStudyLifetimeEnd();
									skipLoop = true;
								}
								weeklyBean.setStartTime(questionaireStartDate);
								weeklyBean.setEndTime(questionaireEndDate);
								runDetailsBean.add(weeklyBean);
								
								//skip the loop if the enddate of questionaire is before the week end date
								if(skipLoop){
									break;
								}
							}
							
							questionaireStartDate = weekEndDate;
							questionaireDay = day;
							repeatCount--;
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
					if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
						Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
						String questionaireStartDate = questionaire.getStudyLifetimeStart();
						while(repeatCount > 0){
							ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
							String questionaireEndDate = "";
							String monthEndDate = "";
							boolean flag = false;
							boolean skipLoop = false;
							
							monthEndDate = StudyMetaDataUtil.addMonthsToDate(questionaireStartDate, 1);
							if((StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate))) || (StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))){
								flag = true;
							}
							
							//get the flag for valid start and end time
							if(flag){
								//check the calculated end date is after the activity actual end date or not
								questionaireEndDate = monthEndDate;
								if((StudyMetaDataConstants.SDF_DATE.parse(monthEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(monthEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))){
									questionaireEndDate = questionaire.getStudyLifetimeEnd();
									skipLoop = true;
								}
								monthlyBean.setStartTime(questionaireStartDate);
								monthlyBean.setEndTime(questionaireEndDate);
								runDetailsBean.add(monthlyBean);
								
								//skip the loop if the enddate of questionaire is before the month end date
								if(skipLoop){
									break;
								}
							}
							
							questionaireStartDate = monthEndDate;
							repeatCount--;
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
					//get the custom frequency details based on the questionaireId
					List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList = null; 
					query = session.createQuery("from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId="+questionaire.getId());
					manuallyScheduleFrequencyList = query.list();
					if(manuallyScheduleFrequencyList != null && manuallyScheduleFrequencyList.size() > 0){
						for(QuestionnairesCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList){
							ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
							manuallyScheduleBean.setEndTime(StringUtils.isEmpty(customFrequencyDto.getFrequencyEndDate())==true?"":customFrequencyDto.getFrequencyEndDate());
							manuallyScheduleBean.setStartTime(StringUtils.isEmpty(customFrequencyDto.getFrequencyStartDate())==true?"":customFrequencyDto.getFrequencyStartDate());
							runDetailsBean.add(manuallyScheduleBean);
						}
					}
					break;
			}
			frequencyDetails.setRuns(runDetailsBean);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Ends");
		return frequencyDetails;
	}
	
	/**
	 * @author Mohan
	 * @param activeTask
	 * @return ActivityFrequencyBean
	 * @throws DAOException
	 * 
	 * This method is used to get the frequency details for ActiveTasks based on the frequncy type selected i.e One Time, Within a Day, Daily, Weekly, Monthly, Manually Schedule
	 */
	public ActivityFrequencyBean getFrequencyRunsDetailsForActiveTasks(ActiveTaskDto activeTask, ActivityFrequencyBean frequencyDetails, Session session) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Starts");
		List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<ActivityFrequencyScheduleBean>();
		try{
			switch (activeTask.getFrequency()) {
				case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
					ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
					oneTimeBean.setStartTime(StringUtils.isEmpty(activeTask.getActiveTaskLifetimeStart())==true?"":activeTask.getActiveTaskLifetimeStart());
					oneTimeBean.setEndTime(StringUtils.isEmpty(activeTask.getActiveTaskLifetimeEnd())==true?"":activeTask.getActiveTaskLifetimeEnd());
					runDetailsBean.add(oneTimeBean);
					break;
				/*case StudyMetaDataConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
					break;*/
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())){
						List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList = null;
						query = session.createQuery(" from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId="+activeTask.getId());
						activeTaskDailyFrequencyList = query.list();
						if(activeTaskDailyFrequencyList != null && activeTaskDailyFrequencyList.size() > 0){
							Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
							String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
							while(repeatCount > 0){
								String activeTaskEndDate = "";
								String dayEndDate = "";
								boolean flag = false;
								boolean skipLoop = false;
								
								dayEndDate = StudyMetaDataUtil.addDaysToDate(activeTaskStartDate, 1);
								if(StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(dayEndDate))){
									flag = true;
								}
								
								//get the flag for valid start and end time
								if(flag){
									//check the calculated end date is after the activity actual end date or not
									activeTaskEndDate = dayEndDate;
									if((StudyMetaDataConstants.SDF_DATE.parse(dayEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(dayEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd())))){
										activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
										skipLoop = true;
									}
									
									//get the frequency time for the daily activity
									for(ActiveTaskFrequencyDto activeTaskFrequency : activeTaskDailyFrequencyList){
										ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
										dailyBean.setStartTime(activeTaskStartDate+" "+activeTaskFrequency.getFrequencyTime());
										dailyBean.setEndTime(activeTaskEndDate+" "+activeTaskFrequency.getFrequencyTime());
										/*dailyBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskStartDate+" "+activeTaskFrequency.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
										dailyBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskEndDate+" "+activeTaskFrequency.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));*/
										runDetailsBean.add(dailyBean);
									}
									
									//skip the loop if the enddate of activeTask is before the month end date
									if(skipLoop){
										break;
									}
								}
								
								activeTaskStartDate = dayEndDate;
								repeatCount--;
							}
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd()) && StringUtils.isNotEmpty(activeTask.getDayOfTheWeek())){
						Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
						//create the runs frequency list based on the repeat activeTask count
						String activeTaskDay = activeTask.getDayOfTheWeek();
						String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
						while(repeatCount > 0){
							ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
							String activeTaskEndDate = "";
							String day = "";
							String weekEndDate = "";
							boolean flag = false;
							boolean skipLoop = false;
							
							if(activeTaskDay.equalsIgnoreCase(StudyMetaDataUtil.getDayByDate(activeTaskStartDate))){
								day = activeTaskDay;
							}
							
							if(!activeTaskDay.equalsIgnoreCase(day)){
								while(!activeTaskDay.equalsIgnoreCase(day)){
									activeTaskStartDate = StudyMetaDataUtil.addDaysToDate(activeTaskStartDate, 1);
									day = StudyMetaDataUtil.getDayByDate(activeTaskStartDate);
								}
							}
							
							weekEndDate = StudyMetaDataUtil.addWeeksToDate(activeTaskStartDate, 1);
							//check the current date is after the active task end date or not 
							//if not add the start and end date else send empty start and end date 
							if((StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate))) || (StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))){
								flag = true;
							}
							
							//get the flag for valid start and end time
							if(flag){
								//check the calculated end date is after the activity actual end date or not
								activeTaskEndDate = weekEndDate;
								if((StudyMetaDataConstants.SDF_DATE.parse(weekEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(weekEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd())))){
									activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
									skipLoop = true;
								}
								weeklyBean.setStartTime(activeTaskStartDate);
								weeklyBean.setEndTime(activeTaskEndDate);
								runDetailsBean.add(weeklyBean);
								
								//skip the loop if the enddate of activeTask is before the week end date
								if(skipLoop){
									break;
								}
							}
							
							activeTaskStartDate = weekEndDate;
							activeTaskDay = day;
							repeatCount--;
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
					if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())){
						Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
						String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
						while(repeatCount > 0){
							ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
							String activeTaskEndDate = "";
							String monthEndDate = "";
							boolean flag = false;
							boolean skipLoop = false;
							
							monthEndDate = StudyMetaDataUtil.addMonthsToDate(activeTaskStartDate, 1);
							if((StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate))) || (StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))){
								flag = true;
							}
							
							//get the flag for valid start and end time
							if(flag){
								//check the calculated end date is after the activity actual end date or not
								activeTaskEndDate = monthEndDate;
								if((StudyMetaDataConstants.SDF_DATE.parse(monthEndDate).equals(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd()))) || (StudyMetaDataConstants.SDF_DATE.parse(monthEndDate).after(StudyMetaDataConstants.SDF_DATE.parse(activeTask.getActiveTaskLifetimeEnd())))){
									activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
									skipLoop = true;
								}
								monthlyBean.setStartTime(activeTaskStartDate);
								monthlyBean.setEndTime(activeTaskEndDate);
								runDetailsBean.add(monthlyBean);
								
								//skip the loop if the enddate of activeTask is before the month end date
								if(skipLoop){
									break;
								}
							}
							
							activeTaskStartDate = monthEndDate;
							repeatCount--;
						}
					}
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
					//get the custom frequency details based on the activeTaskId
					List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList = null; 
					query = session.createQuery("from ActiveTaskCustomFrequenciesDto ATCFDTO where ATCFDTO.activeTaskId="+activeTask.getId());
					manuallyScheduleFrequencyList = query.list();
					if(manuallyScheduleFrequencyList != null && manuallyScheduleFrequencyList.size() > 0){
						for(ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList){
							ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
							manuallyScheduleBean.setEndTime(StringUtils.isEmpty(customFrequencyDto.getFrequencyEndDate())==true?"":customFrequencyDto.getFrequencyEndDate());
							manuallyScheduleBean.setStartTime(StringUtils.isEmpty(customFrequencyDto.getFrequencyStartDate())==true?"":customFrequencyDto.getFrequencyStartDate());
							runDetailsBean.add(manuallyScheduleBean);
						}
					}
					break;
			}
			frequencyDetails.setRuns(runDetailsBean);
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Ends");
		return frequencyDetails;
	}
	/*-----------------------------Manipulate chart data methods ends----------------------------------*/
}
