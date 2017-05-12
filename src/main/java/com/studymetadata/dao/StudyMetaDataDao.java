package com.studymetadata.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.ComprehensionTestQuestionDto;
import com.studymetadata.dto.ConsentDto;
import com.studymetadata.dto.ConsentInfoDto;
import com.studymetadata.dto.ConsentMasterInfoDto;
import com.studymetadata.dto.EligibilityDto;
import com.studymetadata.dto.FormDto;
import com.studymetadata.dto.FormMappingDto;
import com.studymetadata.dto.GatewayInfoDto;
import com.studymetadata.dto.GatewayWelcomeInfoDto;
import com.studymetadata.dto.QuestionnairesDto;
import com.studymetadata.dto.QuestionnairesStepsDto;
import com.studymetadata.dto.QuestionsDto;
import com.studymetadata.dto.ReferenceTablesDto;
import com.studymetadata.dto.ResourcesDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyPageDto;
import com.studymetadata.dto.StudySequenceDto;
import com.studymetadata.dto.StudyVersionDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataUtil;
import com.studymetadata.bean.AnchorDateBean;
import com.studymetadata.bean.ComprehensionBean;
import com.studymetadata.bean.ComprehensionDetailsBean;
import com.studymetadata.bean.ConsentBean;
import com.studymetadata.bean.ConsentDetailsBean;
import com.studymetadata.bean.ConsentDocumentBean;
import com.studymetadata.bean.ConsentDocumentResponse;
import com.studymetadata.bean.CorrectAnswersBean;
import com.studymetadata.bean.EligibilityBean;
import com.studymetadata.bean.EligibilityConsentResponse;
import com.studymetadata.bean.GatewayInfoResourceBean;
import com.studymetadata.bean.GatewayInfoResponse;
import com.studymetadata.bean.InfoBean;
import com.studymetadata.bean.QuestionInfoBean;
import com.studymetadata.bean.ResourceConfigurationBean;
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
	public boolean isValidAuthorizationId(String authorization) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidAuthorizationId() :: Starts");
		boolean hasValidAuthorization = false;
		String bundleIdAndAppToken = null;
		try{
			byte[] decodedBytes = Base64.getDecoder().decode(authorization);
			bundleIdAndAppToken =  new String(decodedBytes, StudyMetaDataConstants.TYPE_UTF8);
			final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
			final String bundleId = tokenizer.nextToken();
			final String appToken = tokenizer.nextToken();
			if(authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)){
				hasValidAuthorization = true;
			}
			/*if((bundleId.equals(authPropMap.get("android.bundleid")) && appToken.equals(authPropMap.get("android.apptoken"))) || (bundleId.equals(authPropMap.get("ios.bundleid")) && appToken.equals(authPropMap.get("ios.apptoken"))) || (bundleId.equals(authPropMap.get("labkey.bundleid")) && appToken.equals(authPropMap.get("labkey.apptoken")))){
				hasValidAuthorization = true;
			}*/
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
			platformType = StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
			if(StringUtils.isNotEmpty(platformType)){
				/*version related query*/
				//query = session.createQuery(" from ResourcesDto RDTO where RDTO.studyId in ( select SDTO.id from StudyDto SDTO where SDTO.platform like '%"+platformType+"%' and SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.live=1) ");
				query = session.createQuery(" from ResourcesDto RDTO where RDTO.studyId in ( select SDTO.id from StudyDto SDTO where SDTO.platform like '%"+platformType+"%' and SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.live=1) and RDTO.status=true ORDER BY RDTO.id DESC");
				resourcesList = query.list();
				if( null != resourcesList && !resourcesList.isEmpty()){
					List<GatewayInfoResourceBean> resourceBeanList = new ArrayList<>();
					for(ResourcesDto resource : resourcesList){
						GatewayInfoResourceBean resourceBean = new GatewayInfoResourceBean();
						resourceBean.setTitle(StringUtils.isEmpty(resource.getTitle())?"":resource.getTitle());
						if(!resource.isTextOrPdf()){
							resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
							resourceBean.setContent(StringUtils.isEmpty(resource.getRichText())?"":resource.getRichText());
						}else{
							resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourceBean.setContent(StringUtils.isEmpty(resource.getPdfUrl())?"":propMap.get("fda.smd.resource.pdfPath")+resource.getPdfUrl());
						}
						resourceBean.setResourcesId(resource.getId() == null?"":String.valueOf(resource.getId()));
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
			platformType = StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
			if(StringUtils.isNotEmpty(platformType)){
				session = sessionFactory.openSession();

				//fetch all Gateway studies based on the platform supported (iOS/android)
				/*version related query*/
				studyListQuery = " from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' and SDTO.status = '"+StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH+"' OR SDTO.live=1";
				//studyListQuery = " from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_GT+"' and SDTO.platform like '%"+platformType+"%' and SDTO.status != '"+StudyMetaDataConstants.STUDY_STATUS_PRE_LAUNCH+"'";
				query = session.createQuery(studyListQuery);
				studiesList = query.list();
				if(null != studiesList && !studiesList.isEmpty()){
					List<StudyBean> studyBeanList = new ArrayList<>();
					for(StudyDto studyDto : studiesList){
						StudyBean studyBean = new StudyBean();
						studyBean.setStudyVersion(studyDto.getVersion() == null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:studyDto.getVersion().toString());
						studyBean.setTagline(StringUtils.isEmpty(studyDto.getStudyTagline())?"":studyDto.getStudyTagline());
						
						//for sprint 1 if the admin completes overview, settings & admins and basic info details and marked as complete assume that the study is active 
						switch (studyDto.getStatus()) {
							case StudyMetaDataConstants.STUDY_STATUS_ACTIVE: studyBean.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
								break;
							case StudyMetaDataConstants.STUDY_STATUS_PAUSED: studyBean.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
								break;
							case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH: studyBean.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
								break;
							case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED: studyBean.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
								break;
							default:
								break;
						}
						
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
							settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ALL);
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
		//Integer actualStudyId = null;
		StudySequenceDto studySequenceDto = null;
		StudyDto studyDto = null;
		StudyVersionDto studyVersionDto = null;
		try{
			session = sessionFactory.openSession();

			//get studyId from studies table
			/*version related query*/
			//query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				query =  session.getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyDto.getCustomStudyId()).setFloat("studyVersion", studyDto.getVersion());
				query.setMaxResults(1);
				studyVersionDto = (StudyVersionDto) query.uniqueResult();
				
				//get the studySequence details by studyId to check the status of the modules for a study
				query = session.getNamedQuery("getStudySequenceDetailsByStudyId").setInteger("studyId", studyDto.getId());
				studySequenceDto = (StudySequenceDto) query.uniqueResult();
				if(studySequenceDto != null){

					//Check whether Eligibility module is done or not
					if(studySequenceDto.getEligibility().equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)){
						//Eligibility details by studyId
						query = session.getNamedQuery("eligibilityDtoByStudyId").setInteger("studyId", studyDto.getId());
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
							correctAnsHashMap.put("answer", false);
							correctAnsHashMap.put("key", "");
							correctAnswers.add(correctAnsHashMap);
							eligibility.setCorrectAnswers(correctAnswers);
							eligibilityConsentResponse.setEligibility(eligibility);
						}
					}

					//Consent Details
					//in MS-1 and MS-2 check whether the consent module is marked as complete/not
					//query = session.getNamedQuery("consentDtoByStudyId").setInteger("studyId", studyDto.getId());
					query = session.getNamedQuery("consentDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyVersionDto.getCustomStudyId()).setFloat("version", studyVersionDto.getConsentVersion());
					consentDto = (ConsentDto) query.uniqueResult();
					if( null != consentDto){
						consent.setVersion(consentDto.getVersion() == null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:consentDto.getVersion().toString());

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
						//query = session.getNamedQuery("consentInfoDtoByStudyId").setInteger("studyId", actualStudyId);
						query = session.getNamedQuery("consentInfoDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyVersionDto.getCustomStudyId()).setFloat("version", studyVersionDto.getConsentVersion());
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
								consentBean.setHtml(StringUtils.isEmpty(consentInfoDto.getElaborated())?"":consentInfoDto.getElaborated().replaceAll("&#34;", "'"));
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
						query = session.getNamedQuery("comprehensionQuestionByStudyId").setInteger("studyId", studyDto.getId());
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
							reviewBean.setReviewHTML(StringUtils.isEmpty(consentDto.getConsentDocContent())?"":consentDto.getConsentDocContent().replaceAll("&#34;", "'"));
						}
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
	public ConsentDocumentResponse consentDocument(String studyId, String consentVersion, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataDao - consentDocument() :: Starts");
		ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
		ConsentDto consent = null;
		//Integer actualStudyId = null;
		//String consentQuery = "from ConsentDto CDTO where CDTO.studyId=";
		StudyDto studyDto = null;
		StudyVersionDto studyVersionDto = null;
		String studyVersionQuery = "from StudyVersionDto SVDTO where SVDTO.customStudyId='"+studyId+"' ";
		try{
			session = sessionFactory.openSession();
			//get studyId from studies table
			/*version related query*/
			//query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			/*query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();*/
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto == null){
				query =  session.getNamedQuery("getPublishedStudyByCustomId").setString("customStudyId", studyId);
				studyDto = (StudyDto) query.uniqueResult();
			}
			
			if(studyDto != null){
				//consentQuery += actualStudyId;
				//query = session.createQuery(consentQuery);
				if(StringUtils.isNotEmpty(consentVersion)){
					studyVersionQuery += " and ROUND(SVDTO.consentVersion, 1)="+consentVersion+" ORDER BY SVDTO.versionId DESC";
				}else if(StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)){
					studyVersionQuery += " and ROUND(SVDTO.activityVersion, 1)="+activityVersion+" ORDER BY SVDTO.versionId DESC";
				}else{
					studyVersionQuery += " ORDER BY SVDTO.versionId DESC";
				}
				
				if(!studyDto.getStatus().equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)){
					query = session.createQuery(studyVersionQuery);
					query.setMaxResults(1);
					studyVersionDto = (StudyVersionDto) query.uniqueResult();
				}else{
					studyVersionDto = new StudyVersionDto();
					studyVersionDto.setConsentVersion(0f);
				}
				if(studyVersionDto != null){
					if(!studyDto.getStatus().equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)){
						query = session.getNamedQuery("consentDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyId).setFloat("version", studyVersionDto.getConsentVersion());
						consent = (ConsentDto) query.uniqueResult();
					}else{
						query = session.getNamedQuery("consentDtoByStudyId").setInteger("studyId", studyDto.getId());
						consent = (ConsentDto) query.uniqueResult();
					}

					//check the consentBo is empty or not
					if( consent != null){
						ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
						consentDocumentBean.setType("text/html");
						consentDocumentBean.setVersion((consent.getVersion() == null)?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:String.valueOf(consent.getVersion()));
						consentDocumentBean.setContent(StringUtils.isEmpty(consent.getConsentDocContent())?"":consent.getConsentDocContent().replaceAll("&#34;", "'"));
						consentDocumentResponse.setConsent(consentDocumentBean);
					}
					consentDocumentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
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
		//Integer actualStudyId = null;
		StudyDto studyDto = null;
		try{
			session = sessionFactory.openSession();
			//get studyId from studies table
			/*version related query*/
			//query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			/*query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();*/
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				query = session.getNamedQuery("getResourcesListByStudyId").setInteger("studyId", studyDto.getId());
				resourcesDtoList = query.list();
				if( null != resourcesDtoList && !resourcesDtoList.isEmpty()){
					List<ResourcesBean> resourcesBeanList = new ArrayList<>();
					for(ResourcesDto resourcesDto : resourcesDtoList){
						ResourcesBean resourcesBean = new ResourcesBean();
						resourcesBean.setAudience(resourcesDto.isResourceType()?StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_LIMITED:StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_ALL);
						resourcesBean.setTitle(StringUtils.isEmpty(resourcesDto.getTitle())?"":resourcesDto.getTitle());
						if(!resourcesDto.isTextOrPdf()){
							resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getRichText())?"":resourcesDto.getRichText());
						}else{
							resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
							resourcesBean.setContent(StringUtils.isEmpty(resourcesDto.getPdfUrl())?"":propMap.get("fda.smd.resource.pdfPath")+resourcesDto.getPdfUrl());
						}
						resourcesBean.setResourcesId(resourcesDto.getId() == null?"":String.valueOf(resourcesDto.getId()));

						//configuration details for the study
						if(!resourcesDto.isResourceVisibility()){
							Map<String, Object> availability = new LinkedHashMap<>();
							availability.put("availableDate", StringUtils.isEmpty(resourcesDto.getStartDate())?"":resourcesDto.getStartDate());
							availability.put("expiryDate", StringUtils.isEmpty(resourcesDto.getEndDate())?"":resourcesDto.getEndDate());
							availability.put("startDays", resourcesDto.getTimePeriodFromDays()==null?0:resourcesDto.getTimePeriodFromDays());
							availability.put("endDays", resourcesDto.getTimePeriodToDays()==null?0:resourcesDto.getTimePeriodToDays());
							resourcesBean.setAvailability(availability);
							/*ResourceConfigurationBean availability = new ResourceConfigurationBean();
							availability.setAvailableDate(StringUtils.isEmpty(resourcesDto.getStartDate())?"":resourcesDto.getStartDate());
							availability.setExpiryDate(StringUtils.isEmpty(resourcesDto.getEndDate())?"":resourcesDto.getEndDate());
							availability.setStartDays(resourcesDto.getTimePeriodFromDays()==null?0:resourcesDto.getTimePeriodFromDays());
							availability.setEndDays(resourcesDto.getTimePeriodToDays()==null?0:resourcesDto.getTimePeriodToDays());
							resourcesBean.setAvailability(availability);*/
						}
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
		List<StudyPageDto> studyPageDtoList = null;
		//Integer actualStudyId = null;
		StudyDto studyDto = null;
		Object[] obj = null;
		StudyVersionDto studyVersionDto = null;
		String questionQuery = "";
		String formQuery = "";
		Boolean isAnchorDateExists = false;
		try{
			session = sessionFactory.openSession();
			/*version related query*/
			//query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			/*query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();*/
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto == null){
				query =  session.getNamedQuery("getPublishedStudyByCustomId").setString("customStudyId", studyId);
				studyDto = (StudyDto) query.uniqueResult();
			}
			if(studyDto != null){
				if(!studyDto.getStatus().equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)){
					query =  session.getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyDto.getCustomStudyId()).setFloat("studyVersion", studyDto.getVersion());
					query.setMaxResults(1);
					studyVersionDto = (StudyVersionDto) query.uniqueResult();
				}
				
				//get study welcome info and page details by studyId
				studyInfoResponse.setStudyWebsite(StringUtils.isEmpty(studyDto.getStudyWebsite())?"":studyDto.getStudyWebsite());
				List<InfoBean> infoList = new ArrayList<>();
				query = session.getNamedQuery("studyPageDetailsByStudyId").setInteger("studyId", studyDto.getId());
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
				
				//check the anchor date details
				if(!studyDto.getStatus().equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)){
					/*questionQuery = "select q.id,q.version,qs.step_type,qs.step_short_title,qt.short_title from questionnaires q, questionnaires_steps qs, questions qt "
							+"where qt.response_type=10 and qt.use_anchor_date=true and qt.id=qs.instruction_form_id and qs.step_type='"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION+"'"
							+"and qs.questionnaires_id=q.id and q.active=true and q.status=true and q.custom_study_id='"+studyVersionDto.getCustomStudyId()+"' and ROUND(q.version, 1)="+studyVersionDto.getActivityVersion();
					query = session.createSQLQuery(questionQuery);
					query.setMaxResults(1);
					obj = (Object[]) query.uniqueResult();
					if(obj != null){
						isAnchorDateExists = true;
					}else{
						formQuery = "select q.id,q.version,qs.step_type,qs.step_short_title,qt.short_title from questionnaires q, questionnaires_steps qs, questions qt,form f, form_mapping fm "
								+" where qt.response_type=10 and qt.use_anchor_date=true and qt.id=fm.question_id and fm.form_id=f.form_id and f.active=true and qs.step_type='"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM+"'"
								+"and qs.questionnaires_id=q.id and q.active=true and q.status=true and q.custom_study_id='"+studyVersionDto.getCustomStudyId()+"' and ROUND(q.version, 1)="+studyVersionDto.getActivityVersion();
						query = session.createSQLQuery(formQuery);
						query.setMaxResults(1);
						obj = (Object[]) query.uniqueResult();
						if(obj != null){
							isAnchorDateExists = true;
						}
					}*/
					
					query = session.createQuery(" from QuestionnairesDto QDTO where QDTO.customStudyId='"+studyVersionDto.getCustomStudyId()+"' and ROUND(QDTO.version, 1)="+studyVersionDto.getActivityVersion());
					List<QuestionnairesDto> questionnairesList = query.list();
					if(questionnairesList != null && !questionnairesList.isEmpty()){
						List<Integer> questionnaireIdsList = new ArrayList<>();
						Map<Integer, QuestionnairesDto> questionnaireMap = new TreeMap<>();
						Map<String, QuestionnairesStepsDto> stepsMap = new TreeMap<>();
						Map<Integer, QuestionsDto> questionsMap = null;
						Map<Integer, FormMappingDto> formMappingMap = new TreeMap<>();
						
						for(QuestionnairesDto questionnaire : questionnairesList){
							questionnaireIdsList.add(questionnaire.getId());
							questionnaireMap.put(questionnaire.getId(), questionnaire);
						}
						
						if(!questionnaireIdsList.isEmpty()){
							List<Integer> questionIdsList = new ArrayList<>();
							List<Integer> formIdsList = new ArrayList<>();
							query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId in ("+StringUtils.join(questionnaireIdsList, ',')+") and QSDTO.stepType in ('"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION+"','"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM+"')");
							List<QuestionnairesStepsDto> questionnairesStepsList = query.list();
							if(questionnairesStepsList != null && !questionnairesStepsList.isEmpty()){
								for(QuestionnairesStepsDto stepsDto : questionnairesStepsList){
									if(stepsDto.getStepType().equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)){
										questionIdsList.add(stepsDto.getInstructionFormId());
										stepsMap.put(stepsDto.getInstructionFormId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, stepsDto);
									}else{
										formIdsList.add(stepsDto.getInstructionFormId());
										stepsMap.put(stepsDto.getInstructionFormId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, stepsDto);
									}
								}
								
								if(!questionIdsList.isEmpty()){
									query = session.createQuery("from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdsList, ',')+") and QDTO.responseType=10 and QDTO.useAnchorDate=true");
									query.setMaxResults(1);
									List<QuestionsDto> questionnsList = query.list();
									if(questionnsList != null && !questionnsList.isEmpty()){
										questionsMap = new TreeMap<>();
										for(QuestionsDto question : questionnsList){
											questionsMap.put(question.getId(), question);
										}
									}
								}
								
								if(questionsMap == null && !formIdsList.isEmpty()){
									List<Integer> formQuestionsList = new ArrayList<>();
									query = session.createQuery("from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId from FormDto FDTO where FDTO.formId in ("+StringUtils.join(formIdsList, ',')+") and FDTO.active=true) ORDER BY FMDTO.formId, FMDTO.sequenceNo");
									List<FormMappingDto> formMappingList = query.list();
									if(formMappingList!= null && !formMappingList.isEmpty()){
										for(FormMappingDto formMapping : formMappingList){
											formQuestionsList.add(formMapping.getQuestionId());
											formMappingMap.put(formMapping.getQuestionId(), formMapping);
										}
										
										if(!formQuestionsList.isEmpty()){
											query = session.createQuery("from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(formQuestionsList, ',')+") and QDTO.responseType=10 and QDTO.useAnchorDate=true");
											query.setMaxResults(1);
											List<QuestionsDto> questionnsList = query.list();
											if(questionnsList != null && !questionnsList.isEmpty()){
												questionsMap = new TreeMap<>();
												for(QuestionsDto question : questionnsList){
													questionsMap.put(question.getId(), question);
												}
											}
										}
									}
								}
								
								if(questionsMap != null){
									AnchorDateBean anchorDate = new AnchorDateBean();
									anchorDate.setType(StudyMetaDataConstants.ANCHORDATE_TYPE_QUESTION);
									for(Map.Entry<Integer, QuestionsDto> map : questionsMap.entrySet()){
										QuestionsDto questionDto = map.getValue();
										if(questionDto != null){
											QuestionnairesStepsDto questionnairesSteps;
											if(StringUtils.isNotEmpty(questionDto.getShortTitle())){
												FormMappingDto formMapping = formMappingMap.get(questionDto.getId());
												questionnairesSteps = stepsMap.get(formMapping.getFormId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM);
											}else{
												questionnairesSteps = stepsMap.get(questionDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
											}
											
											if(questionnairesSteps != null){
												QuestionnairesDto questionnairesDto = questionnaireMap.get(questionnairesSteps.getQuestionnairesId());
												if(questionnairesDto != null){
													QuestionInfoBean questionInfoBean = new QuestionInfoBean();
													questionInfoBean.setActivityId(questionnairesDto.getShortTitle());
													questionInfoBean.setActivityVersion(questionnairesDto.getVersion().toString());
													if(questionnairesSteps.getStepType().equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)){
														questionInfoBean.setKey(questionDto.getShortTitle());
													}else{
														questionInfoBean.setKey(questionnairesSteps.getStepShortTitle());
													}
													anchorDate.setQuestionInfo(questionInfoBean);
												}
											}
										}
									}
									studyInfoResponse.setAnchorDate(anchorDate);
								}
							}
						}
					}
				}
				
				/*if(isAnchorDateExists){
					AnchorDateBean anchorDate = new AnchorDateBean();
					anchorDate.setType(StudyMetaDataConstants.ANCHORDATE_TYPE_QUESTION);
					QuestionInfoBean questionInfoBean = new QuestionInfoBean();
					questionInfoBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+obj[0].toString());
					questionInfoBean.setActivityVersion(obj[1]==null?"":obj[1].toString());
					if(obj[2]!=null && obj[2].toString().equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)){
						questionInfoBean.setKey(obj[4]==null?"":obj[4].toString());
					}else{
						questionInfoBean.setKey(obj[3]==null?"":obj[3].toString());
					}
					anchorDate.setQuestionInfo(questionInfoBean);
					studyInfoResponse.setAnchorDate(anchorDate);
				}*/
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
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean isValidStudy(String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Starts");
		boolean isValidStudy = false;
		//Integer actualStudyId = null;
		StudyDto studyDto = null;
		try{
			session = sessionFactory.openSession();
			/*version related query*/
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			//query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto == null){
				query =  session.getNamedQuery("getPublishedStudyByCustomId").setString("customStudyId", studyId);
				studyDto = (StudyDto) query.uniqueResult();
			}
			isValidStudy = (studyDto == null)?false:true;
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
		}finally{
			if(session != null ){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidStudy() :: Ends");
		return isValidStudy;
	}
	
	/**
	 * @author Mohan
	 * @param activityId
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean isValidActivity(String activityId, String studyId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Starts");
		boolean isValidActivity = false;
		//String[] activityInfoArray = null;
		ActiveTaskDto activeTaskDto = null;
		QuestionnairesDto questionnaireDto = null;
		try{
			session = sessionFactory.openSession();
			query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.shortTitle='"+activityId+"' and ATDTO.live=1 and ATDTO.customStudyId='"+studyId+"'");
			activeTaskDto = (ActiveTaskDto) query.uniqueResult();
			isValidActivity = (activeTaskDto == null)?false:true;
			if(!isValidActivity){
				query = session.createQuery("from QuestionnairesDto QDTO where QDTO.shortTitle='"+activityId+"' and QDTO.active=true and QDTO.live=1 and QDTO.customStudyId='"+studyId+"'");
				questionnaireDto = (QuestionnairesDto) query.uniqueResult();
				isValidActivity = (questionnaireDto == null)?false:true;
			}
			/*activityInfoArray = activityId.split("-");
			if(activityInfoArray[0].equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)){
				query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.id ="+activityInfoArray[1]);
				activeTaskDto = (ActiveTaskDto) query.uniqueResult();
				isValidActivity = (activeTaskDto == null)?false:true;
			}else{
				query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id="+activityInfoArray[1]+" and QDTO.active=true");
				questionnaireDto = (QuestionnairesDto) query.uniqueResult();
				isValidActivity = (questionnaireDto == null)?false:true;
			}*/
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isValidActivity() :: ERROR", e);
		}finally{
			if(session != null ){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isValidActivity() :: Ends");
		return isValidActivity;
	}
	
	/**
	 * @author Mohan
	 * @param activityId
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean isActivityTypeQuestionnaire(String activityId) throws DAOException{
		LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Starts");
		boolean isActivityTypeQuestionnaire = true;
		ActiveTaskDto activeTaskDto = null;
		try{
			session = sessionFactory.openSession();
			query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.shortTitle='"+activityId+"' and ATDTO.live=1");
			activeTaskDto = (ActiveTaskDto) query.uniqueResult();
			if(activeTaskDto != null){
				isActivityTypeQuestionnaire = false;
			}
		}catch(Exception e){
			LOGGER.error("StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: ERROR", e);
		}finally{
			if(session != null ){
				session.close();
			}
		}
		LOGGER.info("INFO: StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Ends");
		return isActivityTypeQuestionnaire;
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
