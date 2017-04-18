package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.dto.ComprehensionTestQuestionDto;
import com.studymetadata.dto.ConsentDto;
import com.studymetadata.dto.ConsentInfoDto;
import com.studymetadata.dto.ConsentMasterInfoDto;
import com.studymetadata.dto.EligibilityDto;
import com.studymetadata.dto.GatewayInfoDto;
import com.studymetadata.dto.GatewayWelcomeInfoDto;
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
				if( null != gatewayWelcomeInfoList && !gatewayWelcomeInfoList.isEmpty()){
					List<InfoBean> infoBeanList = new ArrayList<>();
					for(GatewayWelcomeInfoDto gatewayWelcomeInfo : gatewayWelcomeInfoList){
						InfoBean infoBean = new InfoBean();
						infoBean.setTitle(StringUtils.isEmpty(gatewayWelcomeInfo.getAppTitle())?"":gatewayWelcomeInfo.getAppTitle());
						infoBean.setImage(StringUtils.isEmpty(gatewayWelcomeInfo.getImagePath())?"":propMap.get("fda.smd.study.thumbnailPath")+gatewayWelcomeInfo.getImagePath()+StudyMetaDataUtil.getMilliSecondsForImagePath());
						infoBean.setText(StringUtils.isEmpty(gatewayWelcomeInfo.getDescription())?"":gatewayWelcomeInfo.getDescription());
						if(infoBeanList.isEmpty()){
							infoBean.setType(StudyMetaDataConstants.TYPE_VIDEO);
							infoBean.setVideoLink(StringUtils.isEmpty(gatewayInfo.getVideoUrl())?"":gatewayInfo.getVideoUrl());
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
				if( null != resourcesList && !resourcesList.isEmpty()){
					List<ResourcesBean> resourceBeanList = new ArrayList<>();
					for(ResourcesDto resource : resourcesList){
						ResourcesBean resourceBean = new ResourcesBean();
						resourceBean.setTitle(StringUtils.isEmpty(resource.getTitle())?"":resource.getTitle());
						if(null != resource.getTextOrPdf() && resource.getTextOrPdf() == 0){
							resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
							resourceBean.setContent(StringUtils.isEmpty(resource.getRichText())?"":resource.getRichText());
						}else{
							resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourceBean.setContent(StringUtils.isEmpty(resource.getPdfUrl())?"":propMap.get("fda.smd.resource.pdfPath")+resource.getPdfUrl());
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
			if(session != null){
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
				studyListQuery = " from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' "
						+" and SDTO.id IN (select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.basicInfo='Y' and SSDTO.overView='Y' and SSDTO.settingAdmins='Y' ) ";
				query = session.createQuery(studyListQuery);
				studiesList = query.list();
				if(null != studiesList && !studiesList.isEmpty()){
					List<StudyBean> studyBeanList = new ArrayList<>();
					for(StudyDto studyDto : studiesList){
						StudyBean studyBean = new StudyBean();
						studyBean.setStudyVersion(studyDto.getStudyVersion() == null?"1":studyDto.getStudyVersion().toString());
						studyBean.setTagline(StringUtils.isEmpty(studyDto.getStudyTagline())?"":studyDto.getStudyTagline());

						//for sprint 1 if the admin completes overview, settings & admins and basic info details and marked as complete assume that the study is active 
						studyBean.setStatus(StudyMetaDataConstants.STUDY_STATUS_ACTIVE.toLowerCase());
						studyBean.setTitle(StringUtils.isEmpty(studyDto.getName())?"":studyDto.getName());
						studyBean.setLogo(StringUtils.isEmpty(studyDto.getThumbnailImage())?"":propMap.get("fda.smd.study.thumbnailPath")+studyDto.getThumbnailImage()+StudyMetaDataUtil.getMilliSecondsForImagePath());
						//set the custom studyid of studies table
						studyBean.setStudyId(StringUtils.isEmpty(studyDto.getCustomStudyId())?"":studyDto.getCustomStudyId());

						//get category and sponser details
						if(StringUtils.isNotEmpty(studyDto.getCategory()) && StringUtils.isNotEmpty(studyDto.getResearchSponsor())){
							List<ReferenceTablesDto> referenceTablesList;
							query = session.createQuery(" from ReferenceTablesDto RTDTO where RTDTO.id IN ("+studyDto.getCategory()+","+studyDto.getResearchSponsor()+")");
							referenceTablesList = query.list();
							if(null != referenceTablesList && !referenceTablesList.isEmpty()){
								for(ReferenceTablesDto reference : referenceTablesList){
									if(reference.getCategory().equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)){
										studyBean.setCategory(StringUtils.isEmpty(reference.getValue())?"":reference.getValue());
									}else{
										studyBean.setSponsorName(StringUtils.isEmpty(reference.getValue())?"":reference.getValue());
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
							default:
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
			if(session != null){
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
						if( eligibilityDto != null){
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
							eligibility.setTokenTitle(StringUtils.isEmpty(eligibilityDto.getInstructionalText())?"":eligibilityDto.getInstructionalText());

							//set correctAnswers details for eligibility
							List<HashMap<String,Object>> correctAnswers = new ArrayList<>();
							HashMap<String,Object> correctAnsHashMap = new HashMap<>();
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
						consent.setVersion(consentDto.getStudyVersion() == null?"1":consentDto.getStudyVersion().toString());

						//Sharing
						SharingBean sharingBean = new SharingBean();
						sharingBean.setTitle(StringUtils.isEmpty(consentDto.getTitle())?"":consentDto.getTitle());
						sharingBean.setText(StringUtils.isEmpty(consentDto.getTaglineDescription())?"":consentDto.getTaglineDescription());
						sharingBean.setLearnMore(StringUtils.isEmpty(consentDto.getLearnMoreText())?"":consentDto.getLearnMoreText());
						sharingBean.setLongDesc(StringUtils.isEmpty(consentDto.getLongDescription())?"":consentDto.getLongDescription());
						sharingBean.setShortDesc(StringUtils.isEmpty(consentDto.getShortDescription())?"":consentDto.getShortDescription());
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
						if( null != consentInfoDtoList && !consentInfoDtoList.isEmpty()){
							List<ConsentBean> consentBeanList = new ArrayList<>();
							for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
								ConsentBean consentBean = new ConsentBean();
								consentBean.setText(StringUtils.isEmpty(consentInfoDto.getBriefSummary())?"":consentInfoDto.getBriefSummary());
								consentBean.setTitle(StringUtils.isEmpty(consentInfoDto.getDisplayTitle())?"":consentInfoDto.getDisplayTitle());
								if(consentInfoDto.getConsentItemTitleId() != null){
									if(consentMasterInfoList != null && !consentMasterInfoList.isEmpty()){
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
								consentBean.setHtml(StringUtils.isEmpty(consentInfoDto.getElaborated())?"":consentInfoDto.getElaborated());
								consentBean.setUrl(StringUtils.isEmpty(consentInfoDto.getUrl())?"":consentInfoDto.getUrl());
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
						if( null != comprehensionQuestionList && !comprehensionQuestionList.isEmpty()){
							ComprehensionDetailsBean comprehensionDetailsBean = new ComprehensionDetailsBean();
							//set pass score for comprehension test
							if(consentDto != null && consentDto.getComprehensionTestMinimumScore() != null){
								comprehensionDetailsBean.setPassScore(consentDto.getComprehensionTestMinimumScore());
							}else{
								comprehensionDetailsBean.setPassScore(0);
							}

							List<ComprehensionBean> comprehensionList = new ArrayList<>();
							for(ComprehensionTestQuestionDto comprehensionQuestionDto : comprehensionQuestionList){
								QuestionStepStructureBean questionStepStructure = new QuestionStepStructureBean();
								ComprehensionBean comprehensionBean = new ComprehensionBean();
								questionStepStructure.setTitle(StringUtils.isEmpty(comprehensionQuestionDto.getQuestionText())?"":comprehensionQuestionDto.getQuestionText());
								comprehensionBean.setQuestionStepStructureBean(questionStepStructure);
								comprehensionList.add(comprehensionBean);
							}
							comprehensionDetailsBean.setQuestions(comprehensionList);

							List<CorrectAnswersBean> correctAnswerBeanList = new ArrayList<>();
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
							reviewBean.setSignatureContent(StringUtils.isEmpty(consentDto.getConsentDocContent())?"":consentDto.getConsentDocContent());
						}
						reviewBean.setSignatureTitle("");
						reviewBean.setReasonForConsent(StudyMetaDataConstants.REASON_FOR_CONSENT);
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
			if(session != null){
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
	@SuppressWarnings("unchecked")
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
				consentQuery += actualStudyId;
				query = session.createQuery(consentQuery);
				consent = (ConsentDto) query.uniqueResult();

				//check the consentBo is empty or not
				if( consent != null){
					ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
					consentDocumentBean.setType("text/html");
					consent.setStudyVersion(consent.getStudyVersion() == null?1:consent.getStudyVersion());

					if(consent.getConsentDocType().equals(StudyMetaDataConstants.CONSENT_DOC_TYPE_NEW)){
						consentDocumentBean.setContent(StringUtils.isEmpty(consent.getConsentDocContent())?"":consent.getConsentDocContent());
					}else{
						query = session.getNamedQuery("consentInfoDtoByStudyId").setInteger("studyId", actualStudyId);
						consentInfoDtoList = query.list();
						if( consentInfoDtoList != null && !consentInfoDtoList.isEmpty()){
							StringBuilder contentBuilder = new StringBuilder();
							for(ConsentInfoDto consentInfoDto : consentInfoDtoList){
								contentBuilder.append("<span style=&#34;font-size:20px;&#34;><strong>"+consentInfoDto.getDisplayTitle()+"</strong></span><br/><span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>"+consentInfoDto.getElaborated()+"</span><br/>");
							}
							consentDocumentBean.setContent(contentBuilder.toString());
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
			if(session != null){
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
				if( null != resourcesDtoList && !resourcesDtoList.isEmpty()){
					List<ResourcesBean> resourcesBeanList = new ArrayList<>();
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
						resourcesBean.setTitle(StringUtils.isEmpty(resourcesDto.getTitle())?"":resourcesDto.getTitle());
						if(null != resourcesDto.getTextOrPdf() && resourcesDto.getTextOrPdf() == 0){
							resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getRichText())?"":resourcesDto.getRichText());
						}else{
							resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getPdfUrl())?"":propMap.get("fda.smd.resource.pdfPath")+resourcesDto.getPdfUrl());
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
			if(session != null){
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
				studyDto = (StudyDto) query.uniqueResult();
				if(null != studyDto){
					studyInfoResponse.setStudyWebsite(StringUtils.isEmpty(studyDto.getStudyWebsite())?"":studyDto.getStudyWebsite());
					List<InfoBean> infoList = new ArrayList<>();
					query = session.getNamedQuery("studyPageDetailsByStudyId").setInteger("studyId", actualStudyId);
					studyPageDtoList = query.list();
					if( null != studyPageDtoList && !studyPageDtoList.isEmpty()){
						for(StudyPageDto studyPageInfo : studyPageDtoList){
							InfoBean info = new InfoBean();
							if(infoList.isEmpty()){
								info.setType(StudyMetaDataConstants.TYPE_VIDEO);
								info.setVideoLink(StringUtils.isEmpty(studyDto.getMediaLink())?"":studyDto.getMediaLink());
							}else{
								info.setType(StudyMetaDataConstants.TYPE_TEXT);
								info.setVideoLink("");
							}
							info.setTitle(StringUtils.isEmpty(studyPageInfo.getTitle())?"":studyPageInfo.getTitle());
							info.setImage(StringUtils.isEmpty(studyPageInfo.getImagePath())?"":propMap.get("fda.smd.study.pagePath")+studyPageInfo.getImagePath()+StudyMetaDataUtil.getMilliSecondsForImagePath());
							info.setText(StringUtils.isEmpty(studyPageInfo.getDescription())?"":studyPageInfo.getDescription());
							infoList.add(info);
						}
					}else{
						//for MS1 default value is added for study page1
						InfoBean info = new InfoBean();
						if(infoList.isEmpty()){
							info.setType(StudyMetaDataConstants.TYPE_VIDEO);
							info.setVideoLink(StringUtils.isEmpty(studyDto.getMediaLink())?"":studyDto.getMediaLink());
						}else{
							info.setType(StudyMetaDataConstants.TYPE_TEXT);
							info.setVideoLink("");
						}
						info.setTitle(StringUtils.isEmpty(studyDto.getName())?"":studyDto.getName());
						info.setImage(StringUtils.isEmpty(studyDto.getThumbnailImage())?"":propMap.get("fda.smd.study.thumbnailPath")+studyDto.getThumbnailImage()+StudyMetaDataUtil.getMilliSecondsForImagePath());
						info.setText(StringUtils.isEmpty(studyDto.getFullName())?"":studyDto.getFullName());
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
						default:
							break;
					}
					withdrawConfig.setMessage(StringUtils.isEmpty(studyDto.getAllowRejoinText())?"":studyDto.getAllowRejoinText());
					studyInfoResponse.setWithdrawalConfig(withdrawConfig);
				}

				studyInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				studyInfoResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataDao - studyInfo() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataDao - studyInfo() :: Ends");
		return studyInfoResponse;
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
				default: consentTitle = displaytitle;
					break;
			}
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataDao - getconsentDocumentDisplayTitle() :: ERROR", e);
		}
		LOGGER.info("INFO: StudyMetaDataDao - getconsentDocumentDisplayTitle() :: Ends");
		return consentTitle;
	}
	
}
