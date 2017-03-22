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

import com.studymetadata.dto.ActiveTaskCustomFrequenciesDto;
import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.BrandingDto;
import com.studymetadata.dto.ComprehensionTestQuestionDto;
import com.studymetadata.dto.ConsentDto;
import com.studymetadata.dto.ConsentInfoDto;
import com.studymetadata.dto.EligibilityDto;
import com.studymetadata.dto.FormMappingDto;
import com.studymetadata.dto.GatewayInfoDto;
import com.studymetadata.dto.GatewayWelcomeInfoDto;
import com.studymetadata.dto.InstructionsDto;
import com.studymetadata.dto.QuestionnairesCustomFrequenciesDto;
import com.studymetadata.dto.QuestionnairesDto;
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
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.BrandingBean;
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
import com.studymetadata.bean.StudyBean;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.bean.StudyInfoResponse;
import com.studymetadata.bean.StudyResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.bean.WithdrawalConfigBean;
import com.studymetadata.bean.appendix.ActivityStructureBean;
import com.studymetadata.bean.appendix.InfoStructureBean;
import com.studymetadata.bean.appendix.QuestionStepStructureBean;
import com.studymetadata.bean.appendix.QuestionnaireConfigurationStructureBean;
import com.studymetadata.bean.appendix.ResourceContextStructureBean;
import com.studymetadata.bean.appendix.StepsStructureBean;

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
			if((bundleId.equals(authPropMap.get("android.bundleid.pregnent.women")) && appToken.equals(authPropMap.get("android.apptoken.pregnent.women"))) || (bundleId.equals(authPropMap.get("ios.bundleid.pregnent.women")) && appToken.equals(authPropMap.get("ios.apptoken.pregnent.women")))){
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
				//query = session.createQuery(" from ResourcesDto RDTO where RDTO.status=1 and RDTO.studyId in ( select SDTO.id from StudyDto SDTO where SDTO.platform like '%"+platformType+"%' and SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"') ");
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
								/*consentBean.setTitle(StringUtils.isEmpty(consentInfoDto.getTitle())==true?"":consentInfoDto.getTitle());*/
								consentBean.setDescription(StringUtils.isEmpty(consentInfoDto.getElaborated())==true?"":consentInfoDto.getElaborated());
								consentBean.setHtml(StringUtils.isEmpty(consentInfoDto.getHtmlContent())==true?"":consentInfoDto.getHtmlContent());
								/*consentBean.setType(StringUtils.isEmpty(consentInfoDto.getContentType())==true?"":consentInfoDto.getContentType());*/
								consentBean.setType(StringUtils.isEmpty(consentInfoDto.getConsentItemType())==true?"":consentInfoDto.getConsentItemType());
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
							String signatureContent = "";
							if( consentInfoDtoList != null && consentInfoDtoList.size() > 0){
								for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
									if( StringUtils.isNotEmpty(consentInfoDto.getConsentItemType()) && !consentInfoDto.getConsentItemType().equalsIgnoreCase(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM)){
										switch (consentInfoDto.getDisplayTitle()) {
												case "overview": consentInfoDto.setDisplayTitle("Overview");
																 break;
												case "dataGathering": consentInfoDto.setDisplayTitle("Data Gathering");
												 				 break;
												case "privacy": consentInfoDto.setDisplayTitle("Privacy");
												 				 break;
												case "dataUse": consentInfoDto.setDisplayTitle("Data Use");
												 				 break;
												case "timeCommitment": consentInfoDto.setDisplayTitle("Time Commitment");
												 				 break;
												case "studySurvey": consentInfoDto.setDisplayTitle("Study Survey");
												 				 break;
												case "studyTasks": consentInfoDto.setDisplayTitle("Study Tasks");
												 				 break;
												case "withdrawing": consentInfoDto.setDisplayTitle("Withdrawing");
												 				 break;
												case "customService": consentInfoDto.setDisplayTitle("Custom Service");
												 				 break;
										}
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
							reviewBean.setSignatureContent(signatureContent);
						}
						reviewBean.setSignatureTitle("");
						reviewBean.setReasonForConsent("");
						consent.setReview(reviewBean);
					}
					eligibilityConsentResponse.setConsent(consent);
				
					eligibilityConsentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
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
				
				if(StringUtils.isNotEmpty(consentVersion)){
					consentQuery += actualStudyId+" and CDTO.version="+consentVersion;
				}else{
					consentQuery += actualStudyId;
				}
				query = session.createQuery(consentQuery);
				consent = (ConsentDto) query.uniqueResult();
				
				//check the consentBo is empty or not
				if( consent != null){
					ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
					consentDocumentBean.setType("text/html");
					if(consent.getVersion() != null){
						consentDocumentBean.setVersion(consent.getVersion().toString());
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
									switch (consentInfoDto.getDisplayTitle()) {
											case "overview": consentInfoDto.setDisplayTitle("Overview");
															 break;
											case "dataGathering": consentInfoDto.setDisplayTitle("Data Gathering");
											 				 break;
											case "privacy": consentInfoDto.setDisplayTitle("Privacy");
											 				 break;
											case "dataUse": consentInfoDto.setDisplayTitle("Data Use");
											 				 break;
											case "timeCommitment": consentInfoDto.setDisplayTitle("Time Commitment");
											 				 break;
											case "studySurvey": consentInfoDto.setDisplayTitle("Study Survey");
											 				 break;
											case "studyTasks": consentInfoDto.setDisplayTitle("Study Tasks");
											 				 break;
											case "withdrawing": consentInfoDto.setDisplayTitle("Withdrawing");
											 				 break;
											case "customService": consentInfoDto.setDisplayTitle("Custom Service");
											 				 break;
									}
								}

								content += "<span style=&#34;font-size:20px;&#34;><strong>"
													+consentInfoDto.getDisplayTitle()
													+"</strong></span><br/>"
													+"<span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>"
													+consentInfoDto.getElaborated()
													+"</span><br/>";
							}
						}
						consentDocumentBean.setContent(content);
					}
					consentDocumentResponse.setConsent(consentDocumentBean);
				}
				consentDocumentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - consentDocument() :: ERROR", e);
			e.printStackTrace();
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
			}
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
					StudyPageDtoList = query.list();
					if( null != StudyPageDtoList && StudyPageDtoList.size() > 0){
						for(StudyPageDto studyPageInfo : StudyPageDtoList){
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
			}
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
			}
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
	@SuppressWarnings("unchecked")
	public ActivityMetaDataResponse studyActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		String[] activityInfoArray = null;
		ActivityStructureBean activityStructureBean = new ActivityStructureBean();
		InfoStructureBean infoBean = new InfoStructureBean();
		QuestionnaireConfigurationStructureBean questionnaireConfig = new QuestionnaireConfigurationStructureBean();
		List<String> randomizationSet = new ArrayList<String>();
		
		ActiveTaskDto activeTaskDto = null;
		QuestionnairesDto questionnaireDto = null;
		List<ResourceContextStructureBean> resourceContextList = new ArrayList<ResourceContextStructureBean>();
		TreeMap<Integer, StepsStructureBean> stepsSequenceTreeMap = new TreeMap<Integer, StepsStructureBean>();
		List<StepsStructureBean> steps = new ArrayList<StepsStructureBean>();
		Integer actualStudyId = null;
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
							activityStructureBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
							
							infoBean.setStudyId(studyId);
							//infoBean.setqId("");
							infoBean.setqId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
							infoBean.setName(StringUtils.isEmpty(activeTaskDto.getTaskName())==true?"":activeTaskDto.getTaskName());
							infoBean.setVersion(""); //column not there in the database
							infoBean.setLastModified(""); //column not available in the database
							infoBean.setStartDate(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeStart())==true?"":activeTaskDto.getActiveTaskLifetimeStart());
							infoBean.setEndDate(StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())==true?"":activeTaskDto.getActiveTaskLifetimeEnd());
							activityStructureBean.setInfo(infoBean);
							
							/*questionnaireConfig.setBranching();
							questionnaireConfig.setFrequency();
							questionnaireConfig.setRandomization();*/
							activityStructureBean.setQuestionnaireConfiguration(questionnaireConfig);
							
							activityStructureBean.setRandomizationSets(randomizationSet);
							activityStructureBean.setResourceContext(resourceContextList);
						}
					}else{
						query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id ="+activityInfoArray[1]);
						questionnaireDto = (QuestionnairesDto) query.uniqueResult();
						if(questionnaireDto != null){
							activityStructureBean.setType(StudyMetaDataConstants.TYPE_QUESTIONNAIRE);
							
							infoBean.setStudyId(studyId);
							//infoBean.setqId("");
							infoBean.setqId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId());
							infoBean.setName(StringUtils.isEmpty(questionnaireDto.getTitle())==true?"":questionnaireDto.getTitle());
							infoBean.setVersion(""); //column not there in the database
							infoBean.setLastModified(StringUtils.isEmpty(questionnaireDto.getModifiedDate())==true?"":questionnaireDto.getModifiedDate());
							infoBean.setStartDate(StringUtils.isEmpty(questionnaireDto.getStudyLifetimeStart())==true?"":questionnaireDto.getStudyLifetimeStart());
							infoBean.setEndDate(StringUtils.isEmpty(questionnaireDto.getStudyLifetimeEnd())==true?"":questionnaireDto.getStudyLifetimeEnd());
							activityStructureBean.setInfo(infoBean);
							
							/*questionnaireConfig.setBranching();
							questionnaireConfig.setFrequency();
							questionnaireConfig.setRandomization();*/
							
							Map<String, Integer> sequenceNoMap = new HashMap<String, Integer>();
							
							List<QuestionnairesStepsDto> questionaireStepsList = null;
							query = session.createQuery(" from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" ORDER BY QSDTO.sequenceNo ");
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
							activityStructureBean.setQuestionnaireConfiguration(questionnaireConfig);
							activityStructureBean.setRandomizationSets(randomizationSet);
							activityStructureBean.setResourceContext(resourceContextList);
						}
					}
					
					activityMetaDataResponse.setActivity(activityStructureBean);
					activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyActivityMetadata() :: ERROR", e);
			e.printStackTrace();
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
				studyDashboardResponse.setDashboard(dashboard);
				studyDashboardResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
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
			termsPolicyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			termsPolicyResponse.setPrivacy("http://192.168.0.50:8080/fdaImages/studyHtml/privacyAndPolicy.html");
			termsPolicyResponse.setTerms("http://192.168.0.50:8080/fdaImages/studyHtml/terms.html");
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
	
	/*-----------------------------Activity data methods starts----------------------------------*/
	/**
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
	 * This method is used to get the Activivty Steps details based on the sequence order no in quationaire steps
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<Integer, StepsStructureBean> getStepsInfoForQuestionaires(String type, List<InstructionsDto> instructionsDtoList, List<QuestionsDto> questionsDtoList, List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, TreeMap<Integer, StepsStructureBean> stepsSequenceTreeMap, Session session) throws Exception{
		LOGGER.info("INFO: StudyMetaDataDao - getStepsInfoForQuestionaires() :: Starts");
		TreeMap<Integer, StepsStructureBean> stepsSequenceOrderTreeMap = new TreeMap<Integer, StepsStructureBean>();
		try{
			if(type.equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)){
				if( instructionsDtoList != null && instructionsDtoList.size() > 0){
					for(InstructionsDto instructionsDto : instructionsDtoList){
						StepsStructureBean instructionBean = new StepsStructureBean();
						
						instructionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
						instructionBean.setResultType("");
						instructionBean.setKey("");
						instructionBean.setTitle(StringUtils.isEmpty(instructionsDto.getInstructionTitle())==true?"":instructionsDto.getInstructionTitle());
						instructionBean.setText(StringUtils.isEmpty(instructionsDto.getInstructionText())==true?"":instructionsDto.getInstructionText());
						instructionBean.setImage("");
						instructionBean.setSkippable(false);
						Map<String, Object> destinations = new HashMap<String, Object>();
						destinations.put("default", "dueDate");
						instructionBean.setDestinations(destinations);
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(instructionsDto.getId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION), instructionBean);
					}
				}
			}else if(type.equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)){
				if(questionsDtoList != null && questionsDtoList.size() > 0){
					for(QuestionsDto questionsDto : questionsDtoList){
						StepsStructureBean questionBean = new StepsStructureBean();
						
						questionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
						questionBean.setResultType("");
						questionBean.setKey("");
						questionBean.setTitle(StringUtils.isEmpty(questionsDto.getQuestion())==true?"":questionsDto.getQuestion());
						questionBean.setSkippable(false);
						questionBean.setGroupName("");
						questionBean.setPhi("");
						Map<String, Object> destinations = new HashMap<String, Object>();
						destinations.put("true", "folicAcid");
						destinations.put("false", "birthControlType");
						questionBean.setDestinations(destinations);
						
						Map<String, List<Object>> format = new HashMap<String, List<Object>>();
						List<Object> choices = new ArrayList<Object>();
						choices.add("pills");
						choices.add("tablets");
						choices.add("others");
						format.put("textChoices", choices);
						
						List<Object> obj = new ArrayList<Object>();
						obj.add(1);
						format.put("selectionStyle", obj);
						questionBean.setFormat(format);
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(questionsDto.getId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION), questionBean);
					}
				}
			}else{
				if( formsList != null && formsList.size() > 0){
					for(FormMappingDto formDto : formsList){
						StepsStructureBean formBean = new StepsStructureBean();
						List<StepsStructureBean> formSteps = new ArrayList<StepsStructureBean>();
						formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
						formBean.setResultType("");
						formBean.setKey("");
						formBean.setTitle("");
						formBean.setSkippable(false);
						
						/*get the question deatils for a form in one query 
						 * alter the query based on the column details needed
						 * select f.form_id, q.id, q.short_title, q.question from questions q, form_mapping f where q.id=f.question_id and f.form_id in (1,2);*/
						
						List<QuestionsDto> formQuestionsList = null;
						query = session.createQuery("from QuestionsDto QDTO where QDTO.id="+formDto.getQuestionId());
						formQuestionsList = query.list();
						if(formQuestionsList != null && formQuestionsList.size() > 0){
							for(QuestionsDto formQuestionDto : formQuestionsList){
								StepsStructureBean formQuestionBean = new StepsStructureBean();
								formQuestionBean.setResultType("");
								formQuestionBean.setKey("");
								formQuestionBean.setTitle(StringUtils.isEmpty(formQuestionDto.getQuestion())==true?"":formQuestionDto.getQuestion());
								formQuestionBean.setSkippable(false);
								
								Map<String, List<Object>> format = new HashMap<String, List<Object>>();
								List<Object> choices = new ArrayList<Object>();
								choices.add("medicationA");
								choices.add("medicationB");
								choices.add("medicationC");
								format.put("textChoices", choices);
								formQuestionBean.setFormat(format);
								formQuestionBean.setPhi("");
								
								formSteps.add(formQuestionBean);
							}
						}
						formBean.setSteps(formSteps);
						
						formBean.setGroupName("");
						formBean.setRepeatable(false);
						formBean.setRepeatableText("");
						
						stepsSequenceTreeMap.put(sequenceNoMap.get(String.valueOf(formDto.getFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM), formBean);
					}
				}
			}
			
			stepsSequenceOrderTreeMap = stepsSequenceTreeMap;
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - getStepsInfoForQuestionaires() :: ERROR", e);
			e.printStackTrace();
		}
		LOGGER.info("INFO: StudyMetaDataDao - getStepsInfoForQuestionaires() :: Ends");
		return stepsSequenceOrderTreeMap;
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
		try{
			switch (questionaire.getFrequency()) {
				case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
					ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
					oneTimeBean.setStartTime(StringUtils.isEmpty(questionaire.getStudyLifetimeStart())==true?"":questionaire.getStudyLifetimeStart());
					oneTimeBean.setEndTime(StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())==true?"":questionaire.getStudyLifetimeEnd());
					runDetailsBean.add(oneTimeBean);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
						ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
						if(questionaire.getRepeatQuestionnaire() != null && questionaire.getRepeatQuestionnaire() > 0){
							Integer repeatCount = questionaire.getRepeatQuestionnaire();
							//create the runs frequency list based on the repeat questionaire count
							String questionaireStartDate = "";
							String questionaireDay = questionaire.getDayOfTheWeek();
							while(repeatCount > 0){
								weeklyBean = new ActivityFrequencyScheduleBean();
								if(StringUtils.isEmpty(questionaireDay)){
									questionaireDay = questionaire.getDayOfTheWeek();
								}
								if(StringUtils.isEmpty(questionaireStartDate)){
									questionaireStartDate = questionaire.getStudyLifetimeStart();
								}
								if(StringUtils.isEmpty(questionaireStartDate)){
									questionaireStartDate = questionaire.getStudyLifetimeStart();
								}
								
								String dayOfTheWeek = "";
								String endDate = "";
								
								if(questionaireDay.equalsIgnoreCase(StudyMetaDataUtil.getDayByDate(questionaireStartDate))){
									dayOfTheWeek = questionaireDay;
								}
								
								if(!questionaireDay.equalsIgnoreCase(dayOfTheWeek)){
									while(!questionaireDay.equalsIgnoreCase(dayOfTheWeek)){
										questionaireStartDate = StudyMetaDataUtil.addDaysForDate(questionaireStartDate, 1);
										dayOfTheWeek = StudyMetaDataUtil.getDayByDate(questionaireStartDate);
									}
								}
								
								endDate = StudyMetaDataUtil.addWeeks(questionaireStartDate, 1);
								//check the current date is after the active task end date or not 
								//if not add the start and end date else send empty start and end date 
								if(StudyMetaDataConstants.SDF_DATE.parse(StudyMetaDataUtil.getCurrentDate()).before(StudyMetaDataConstants.SDF_DATE.parse(endDate))){
									weeklyBean.setStartTime(questionaireStartDate);
									weeklyBean.setEndTime(endDate);
								}
								runDetailsBean.add(weeklyBean);
								questionaireStartDate = endDate;
								questionaireDay = dayOfTheWeek;
								repeatCount--;
							}
						}else{
							weeklyBean.setStartTime(StringUtils.isEmpty(questionaire.getStudyLifetimeStart())==true?"":questionaire.getStudyLifetimeStart());
							weeklyBean.setStartTime(StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())==true?"":questionaire.getStudyLifetimeEnd());
							runDetailsBean.add(weeklyBean);
						}
					}
					
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
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
			e.printStackTrace();
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
				case StudyMetaDataConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
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
			e.printStackTrace();
		}
		LOGGER.info("INFO: StudyMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Ends");
		return frequencyDetails;
	}
	/*-----------------------------Manipulate chart data methods ends----------------------------------*/
}
