package com.studymetadata.dao;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.bean.ActiveTaskActivityMetaDataResponse;
import com.studymetadata.bean.ActiveTaskActivityStepsBean;
import com.studymetadata.bean.ActiveTaskActivityStructureBean;
import com.studymetadata.bean.ActivitiesBean;
import com.studymetadata.bean.ActivityFrequencyBean;
import com.studymetadata.bean.ActivityFrequencyScheduleBean;
import com.studymetadata.bean.ActivityMetadataBean;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.DestinationBean;
import com.studymetadata.bean.FetalKickCounterFormatBean;
import com.studymetadata.bean.QuestionnaireActivityMetaDataResponse;
import com.studymetadata.bean.QuestionnaireActivityStepsBean;
import com.studymetadata.bean.SpatialSpanMemoryFormatBean;
import com.studymetadata.bean.TowerOfHanoiFormatBean;
import com.studymetadata.bean.appendix.QuestionnaireActivityStructureBean;
import com.studymetadata.dto.ActiveTaskAttrtibutesValuesDto;
import com.studymetadata.dto.ActiveTaskCustomFrequenciesDto;
import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.ActiveTaskFrequencyDto;
import com.studymetadata.dto.ActiveTaskListDto;
import com.studymetadata.dto.ActiveTaskMasterAttributeDto;
import com.studymetadata.dto.FormMappingDto;
import com.studymetadata.dto.InstructionsDto;
import com.studymetadata.dto.QuestionReponseTypeDto;
import com.studymetadata.dto.QuestionResponseSubTypeDto;
import com.studymetadata.dto.QuestionResponsetypeMasterInfoDto;
import com.studymetadata.dto.QuestionnairesCustomFrequenciesDto;
import com.studymetadata.dto.QuestionnairesDto;
import com.studymetadata.dto.QuestionnairesFrequenciesDto;
import com.studymetadata.dto.QuestionnairesStepsDto;
import com.studymetadata.dto.QuestionsDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyVersionDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class ActivityMetaDataDao {
	private static final Logger LOGGER = Logger.getLogger(ActivityMetaDataDao.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

	@SuppressWarnings("unchecked")
	HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	/*Session session = null;
	Transaction transaction = null;*/
	Query query = null;
	String queryString = "";

	/**
	 * @author Mohan
	 * @param studyId
	 * @param authorization
	 * @return ActivityResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ActivityResponse studyActivityList(String studyId, String authorization) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - studyActivityList() :: Starts");
		Session session = null;
		ActivityResponse activityResponse = new ActivityResponse();
		List<ActiveTaskDto> activeTaskDtoList = null;
		List<QuestionnairesDto> questionnairesList = null;
		List<ActivitiesBean> activitiesBeanList = new ArrayList<>();
		StudyDto studyDto = null;
		StudyVersionDto studyVersionDto = null;
		String deviceType = "";
		try{
			deviceType = StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				query =  session.getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyDto.getCustomStudyId()).setFloat("studyVersion", studyDto.getVersion());
				query.setMaxResults(1);
				studyVersionDto = (StudyVersionDto) query.uniqueResult();

				//get the Activities (type : Active Task list) by studyId
				//query = session.getNamedQuery("getActiveTaskDetailsByCustomStudyIdAndIsLive").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1);
				query = session.getNamedQuery("getActiveTaskDetailsByCustomStudyId").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1).setInteger("active", 0);
				activeTaskDtoList = query.list();
				if( null != activeTaskDtoList && !activeTaskDtoList.isEmpty()){
					for(ActiveTaskDto activeTaskDto : activeTaskDtoList){
						boolean isSupporting = true;
						/*NOTE: send Spatial Span Memory and Tower of Hanoi to only ios users, 
						if the user logs in the android device send only Fetal Kick Counter Active Task*/
						if(StringUtils.isNotEmpty(deviceType) &&  deviceType.equalsIgnoreCase(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID) && !activeTaskDto.getTaskTypeId().equals(1)){
							isSupporting = false;
						}
						
						if(isSupporting){
							ActivitiesBean activityBean = new ActivitiesBean();
							activityBean.setTitle(StringUtils.isEmpty(activeTaskDto.getDisplayName())?"":activeTaskDto.getDisplayName());
							activityBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
							activityBean.setState((activeTaskDto.getActive()!=null&&activeTaskDto.getActive()>0)?StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE:StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);

							activityBean.setActivityVersion((activeTaskDto.getVersion() == null || activeTaskDto.getVersion() < 1.0f)?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:activeTaskDto.getVersion().toString());
							activityBean.setBranching(false);
							activityBean.setLastModified(StringUtils.isEmpty(activeTaskDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

							ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
							frequencyDetails = getFrequencyRunsDetailsForActiveTasks(activeTaskDto, frequencyDetails, session);
							frequencyDetails.setType(StringUtils.isEmpty(activeTaskDto.getFrequency())?"":activeTaskDto.getFrequency());
							activityBean.setFrequency(frequencyDetails);

							//get the time details for the activity by activityId
							activityBean = getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);

							//modifiedDateTime will be the endDateTime for the deleted activities
							if(activeTaskDto.getActive()==null || activeTaskDto.getActive()==0){
								activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
							}

							activityBean.setActivityId(activeTaskDto.getShortTitle());
							activitiesBeanList.add(activityBean);
						}
					}
				}

				//get the Activities (type : Questionaires list) by studyId
				//query = session.getNamedQuery("getQuestionnaireDetailsByCustomStudyIdAndIsLive").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1);
				query = session.getNamedQuery("getQuestionnaireDetailsByCustomStudyId").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1).setBoolean("active", false);
				questionnairesList = query.list();
				if( questionnairesList != null && !questionnairesList.isEmpty()){
					for(QuestionnairesDto questionaire : questionnairesList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(questionaire.getTitle())?"":questionaire.getTitle());
						activityBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);
						activityBean.setState(questionaire.getActive()?StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE:StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);

						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						frequencyDetails = getFrequencyRunsDetailsForQuestionaires(questionaire, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(questionaire.getFrequency())?"":questionaire.getFrequency());
						activityBean.setFrequency(frequencyDetails);
						activityBean.setActivityId(questionaire.getShortTitle());
						activityBean.setActivityVersion((questionaire.getVersion() == null || questionaire.getVersion() < 1.0f)?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:questionaire.getVersion().toString());
						activityBean.setBranching((questionaire.getBranching() == null || !questionaire.getBranching())?false:true);
						activityBean.setLastModified(StringUtils.isEmpty(questionaire.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
						activityBean = getTimeDetailsByActivityIdForQuestionnaire(questionaire, activityBean, session);

						//modifiedDateTime will be the endDateTime for the deleted activities
						if(!questionaire.getActive()){
							activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
						}
						activitiesBeanList.add(activityBean);
					}
				}

				//check the activities list for the studyId
				if(!activitiesBeanList.isEmpty()){
					activityResponse.setActivities(activitiesBeanList);
				}
				activityResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				activityResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - studyActivityList() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: ActivityMetaDataDao - studyActivityList() :: Ends");
		return activityResponse;
	}

	/**
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return ActiveTaskActivityMetaDataResponse
	 * @throws DAOException
	 */
	public ActiveTaskActivityMetaDataResponse studyActiveTaskActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: Starts");
		Session session = null;
		ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse = new ActiveTaskActivityMetaDataResponse();
		ActiveTaskActivityStructureBean activeTaskactivityStructureBean = new ActiveTaskActivityStructureBean();
		StudyDto studyDto = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				activeTaskactivityStructureBean = activeTaskMetadata(studyId, activityId, session, activityVersion);
				activeTaskActivityMetaDataResponse.setActivity(activeTaskactivityStructureBean);
				activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: Ends");
		return activeTaskActivityMetaDataResponse;
	}

	/**
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param activityVersion
	 * @return QuestionnaireActivityMetaDataResponse
	 * @throws DAOException
	 */
	public QuestionnaireActivityMetaDataResponse studyQuestionnaireActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: Starts");
		Session session = null;
		QuestionnaireActivityMetaDataResponse activityMetaDataResponse = new QuestionnaireActivityMetaDataResponse();
		QuestionnaireActivityStructureBean activityStructureBean = new QuestionnaireActivityStructureBean();
		StudyDto studyDto = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				activityStructureBean = questionnaireMetadata(studyId, activityId, session, activityVersion);
				activityMetaDataResponse.setActivity(activityStructureBean);
				activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}else{
				activityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: Ends");
		return activityMetaDataResponse;
	}

	/*-----------------------------Activity data methods starts----------------------------------*/

	/**
	 * This method is used to get the activeTaskList
	 * 
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param session
	 * @return ActivityStructureBean
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ActiveTaskActivityStructureBean activeTaskMetadata(String studyId, String activityId, Session session, String activityVersion) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Starts");
		ActiveTaskActivityStructureBean activeTaskActivityStructureBean = new ActiveTaskActivityStructureBean();
		ActiveTaskDto activeTaskDto = null;
		List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();
		ActiveTaskListDto taskDto = null;
		try{
			query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.action=true and ATDTO.customStudyId='"+studyId+"' and ATDTO.shortTitle='"+StudyMetaDataUtil.replaceSingleQuotes(activityId)+"' and ROUND(ATDTO.version, 1)="+Float.parseFloat(activityVersion)+" ORDER BY ATDTO.id DESC");
			query.setMaxResults(1);
			activeTaskDto = (ActiveTaskDto) query.uniqueResult();
			if( activeTaskDto != null){
				List<Integer> taskMasterAttrIdList = new ArrayList<>();
				List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList;
				List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;
				List<ActiveTaskListDto> activeTaskList = null;

				activeTaskActivityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);

				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(activeTaskDto.getShortTitle());

				ActivitiesBean activityBean = new ActivitiesBean();
				activityBean = getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);
				metadata.setStartDate(activityBean.getStartTime());
				metadata.setEndDate(activityBean.getEndTime());
				metadata.setLastModified(StringUtils.isEmpty(activeTaskDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")); //column not there in the database
				metadata.setName(StringUtils.isEmpty(activeTaskDto.getShortTitle())?"":activeTaskDto.getShortTitle());
				metadata.setStudyId(studyId);
				metadata.setVersion(activeTaskDto.getVersion() == null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:activeTaskDto.getVersion().toString());
				//modifiedDateTime will be the endDateTime for the deleted activities
				if(activeTaskDto.getActive()==null || activeTaskDto.getActive()==0){
					metadata.setEndDate(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}
				activeTaskActivityStructureBean.setMetadata(metadata);

				//get the active task attribute values based on the activityId
				query = session.createQuery("from ActiveTaskAttrtibutesValuesDto ATAVDTO where ATAVDTO.activeTaskId="+activeTaskDto.getId()
						+" and ATAVDTO.activeTaskMasterAttrId in (select ATMADTO.masterId from ActiveTaskMasterAttributeDto ATMADTO where ATMADTO.attributeType='"+StudyMetaDataConstants.ACTIVE_TASK_ATTRIBUTE_TYPE_CONFIGURE+"')"
						+" ORDER BY ATAVDTO.activeTaskMasterAttrId");
				activeTaskAttrtibuteValuesList = query.list();
				if( activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty()){
					for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
						taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
					}

					if(!taskMasterAttrIdList.isEmpty()){
						//get the active task master info based on the active task attribute ids
						query = session.createQuery(" from ActiveTaskMasterAttributeDto ATMADTO where ATMADTO.masterId in ("+StringUtils.join(taskMasterAttrIdList, ",")+")");
						//query = session.getNamedQuery("getActiveTaskMasterListFromIds").setParameterList("taskMasterAttrIdList", taskMasterAttrIdList);
						activeTaskMaterList = query.list();
						if(activeTaskMaterList != null && !activeTaskMaterList.isEmpty()){
							//get the active task list details
							taskDto = (ActiveTaskListDto) session.createQuery("from ActiveTaskListDto ATDTO where ATDTO.activeTaskListId="+activeTaskMaterList.get(0).getTaskTypeId()).uniqueResult();
						}
					}
				}
				//check the active task details are exists or not
				Boolean attributeListFlag = activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty();
				Boolean masterAttributeListFlag = activeTaskMaterList != null && !activeTaskMaterList.isEmpty();
				Boolean taskListFlag = taskDto != null;
				if(attributeListFlag && masterAttributeListFlag && taskListFlag){
					//get the steps details based on the activity type
					ActiveTaskActivityStepsBean activeTaskActiveTaskStep = new ActiveTaskActivityStepsBean();
					//Map<String, Object> activeTaskFormat = new LinkedHashMap<>();
					FetalKickCounterFormatBean fetalKickCounterFormat = new FetalKickCounterFormatBean();
					SpatialSpanMemoryFormatBean spatialSpanMemoryFormat = new SpatialSpanMemoryFormatBean();
					TowerOfHanoiFormatBean towerOfHanoiFormat = new TowerOfHanoiFormatBean();
					boolean skipLoopFlag = false;
					for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
						if(!skipLoopFlag){
							for(ActiveTaskMasterAttributeDto masterAttributeDto : activeTaskMaterList){
								if(!skipLoopFlag){
									if(attributeDto.getActiveTaskMasterAttrId().equals(masterAttributeDto.getMasterId())){
										if(taskDto.getActiveTaskListId().equals(masterAttributeDto.getTaskTypeId())){
											activeTaskActiveTaskStep.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
											activeTaskActiveTaskStep.setResultType(StringUtils.isEmpty(taskDto.getType())?"":taskDto.getType());
											activeTaskActiveTaskStep.setKey(activeTaskDto.getShortTitle());
											activeTaskActiveTaskStep.setText(StringUtils.isEmpty(activeTaskDto.getInstruction())?"":activeTaskDto.getInstruction());
											//activeTaskActiveTaskStep.setOptions(activeTaskOptions()); //activeTask options list

											//get the response steps
											switch (taskDto.getType()) {
											case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
												fetalKickCounterFormat = (FetalKickCounterFormatBean) fetalKickCounterDetails(attributeDto, masterAttributeDto, fetalKickCounterFormat);
												activeTaskActiveTaskStep.setFormat(fetalKickCounterFormat);


												//if the last attribute then skip the loop
												if(attributeDto.getActiveTaskMasterAttrId().equals(activeTaskMaterList.get(activeTaskMaterList.size()-1).getMasterId())){
													skipLoopFlag = true;
												}
												break;
											case StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY:
												spatialSpanMemoryFormat = (SpatialSpanMemoryFormatBean) spatialSpanMemoryDetails(attributeDto, masterAttributeDto, spatialSpanMemoryFormat);
												activeTaskActiveTaskStep.setFormat(spatialSpanMemoryFormat);

												//if the last attribute then skip the loop
												if(attributeDto.getActiveTaskMasterAttrId().equals(activeTaskMaterList.get(activeTaskMaterList.size()-1).getMasterId())){
													skipLoopFlag = true;
												}
												break;
											case StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI:
												towerOfHanoiFormat.setNumberOfDisks(StringUtils.isEmpty(attributeDto.getAttributeVal())?0:Integer.parseInt(attributeDto.getAttributeVal()));
												skipLoopFlag = true;
												activeTaskActiveTaskStep.setFormat(towerOfHanoiFormat);
												break;
											default:
												break;
											}
										}
									}
								}
							}
						}
					}
					steps.add(activeTaskActiveTaskStep);
					activeTaskActivityStructureBean.setSteps(steps);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - activeTaskMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Ends");
		return activeTaskActivityStructureBean;
	}

	/**
	 * This method is used to get the questionnairesList
	 * 
	 * @author Mohan
	 * @param studyId
	 * @param activityId
	 * @param session
	 * @return ActivityStructureBean
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public QuestionnaireActivityStructureBean questionnaireMetadata(String studyId, String activityId, Session session, String activityVersion) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - questionnaireMetadata() :: Starts");
		QuestionnaireActivityStructureBean activityStructureBean = new QuestionnaireActivityStructureBean();
		Map<String, Integer> sequenceNoMap = new HashMap<>();
		Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap = new HashMap<>();
		TreeMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap = new TreeMap<>();
		QuestionnairesDto questionnaireDto = null;
		List<QuestionnairesStepsDto> questionaireStepsList = null;
		List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
		List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList = null;
		try{
			query = session.createQuery("from QuestionnairesDto QDTO where QDTO.customStudyId='"+studyId+"' and  QDTO.shortTitle='"+StudyMetaDataUtil.replaceSingleQuotes(activityId)+"' and QDTO.status=true and ROUND(QDTO.version, 1)="+Float.parseFloat(activityVersion)+" ORDER BY QDTO.id DESC");
			query.setMaxResults(1);
			questionnaireDto = (QuestionnairesDto) query.uniqueResult();
			if(questionnaireDto != null){
				activityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);

				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(questionnaireDto.getShortTitle());

				ActivitiesBean activityBean = new ActivitiesBean();
				activityBean = getTimeDetailsByActivityIdForQuestionnaire(questionnaireDto, activityBean, session);

				metadata.setStartDate(activityBean.getStartTime());
				metadata.setEndDate(activityBean.getEndTime());
				metadata.setLastModified(StringUtils.isEmpty(questionnaireDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				metadata.setName(StringUtils.isEmpty(questionnaireDto.getShortTitle())?"":questionnaireDto.getShortTitle());
				metadata.setStudyId(studyId);
				metadata.setVersion(questionnaireDto.getVersion() == null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:questionnaireDto.getVersion().toString());
				//modifiedDateTime will be the endDateTime for the deleted activities
				if(!questionnaireDto.getActive()){
					metadata.setEndDate(StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}
				activityStructureBean.setMetadata(metadata);

				query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" and QSDTO.status=true ORDER BY QSDTO.sequenceNo");
				questionaireStepsList = query.list();
				if(questionaireStepsList != null && !questionaireStepsList.isEmpty()){
					List<Integer> instructionIdList = new ArrayList<>();
					List<Integer> questionIdList = new ArrayList<>();
					List<Integer> formIdList = new ArrayList<>();
					for(int i=0; i<questionaireStepsList.size(); i++){
						if(!questionnaireDto.getBranching()){
							if((questionaireStepsList.size()-1) == i){
								questionaireStepsList.get(i).setDestinationStep(0);
							}else{
								questionaireStepsList.get(i).setDestinationStep(questionaireStepsList.get(i+1).getStepId());
							}
						}
					}

					questionaireStepsList = getDestinationStepType(questionaireStepsList);
					for(QuestionnairesStepsDto questionnairesStep : questionaireStepsList){
						switch (questionnairesStep.getStepType()) {
						case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION: instructionIdList.add(questionnairesStep.getInstructionFormId());
						sequenceNoMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, questionnairesStep.getSequenceNo());
						questionnaireStepDetailsMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, questionnairesStep);
						break;
						case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION: questionIdList.add(questionnairesStep.getInstructionFormId());
						sequenceNoMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, questionnairesStep.getSequenceNo());
						questionnaireStepDetailsMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, questionnairesStep);
						break;
						case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM: formIdList.add(questionnairesStep.getInstructionFormId());
						sequenceNoMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, questionnairesStep.getSequenceNo());
						questionnaireStepDetailsMap.put(String.valueOf(questionnairesStep.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, questionnairesStep);
						break;
						default:
							break;
						}
					}

					//get the question master info
					query = session.createQuery(" from QuestionResponsetypeMasterInfoDto ");
					questionResponseTypeMasterInfoList = query.list();

					//get the instructionsList
					if(!instructionIdList.isEmpty()){
						List<InstructionsDto> instructionsDtoList;
						query = session.createQuery(" from InstructionsDto IDTO where IDTO.id in ("+StringUtils.join(instructionIdList, ",")+") and IDTO.status=true");
						//query = session.getNamedQuery("getInstructionsListFromIds").setParameterList("instructionIdList", instructionIdList);
						instructionsDtoList = query.list();
						if(instructionsDtoList != null && !instructionsDtoList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, instructionsDtoList, null, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, null, questionaireStepsList, questionnaireDto);
						}
					}

					//get the questionaire List
					if(!questionIdList.isEmpty()){
						List<QuestionsDto> questionsList;
						query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdList, ",")+") and QDTO.status=true");
						//query = session.getNamedQuery("getQuestionsListFromIds").setParameterList("questionIdList", questionIdList);
						questionsList = query.list();
						if( questionsList != null && !questionsList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, null, questionsList, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionaireStepsList, questionnaireDto);
						}
					}

					//get the forms list
					if(!formIdList.isEmpty()){
						for(Integer formId : formIdList){
							List<FormMappingDto> formList;
							query = session.createQuery(" from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId from FormDto FDTO where FDTO.formId="+formId+") and FMDTO.active=true ORDER BY FMDTO.sequenceNo ");
							formList = query.list();
							if(formList != null && !formList.isEmpty()){
								stepsSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, null, null, formList, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionaireStepsList, questionnaireDto);
							}
						}
					}

					//iterate the treemap to get the activities based on the sequence order
					for(Integer key : stepsSequenceTreeMap.keySet()){
						steps.add(stepsSequenceTreeMap.get(key));
					}

					activityStructureBean.setSteps(steps);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - questionnaireMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - questionnaireMetadata() :: Ends");
		return activityStructureBean;
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
		LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Starts");
		List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
		try{
			switch (activeTask.getFrequency()) {
			case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
				runDetailsBean = getActiveTaskFrequencyDetailsForDaily(activeTask, runDetailsBean, session);
				break;
			case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
				runDetailsBean = getActiveTaskFrequencyDetailsForManuallySchedule(activeTask, runDetailsBean, session);
				break;
			default:
				break;
			}
			frequencyDetails.setRuns(runDetailsBean);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: Ends");
		return frequencyDetails;
	}

	/**
	 * This method is used to get the Active Task Frequency details for One Time
	 * 
	 * @author Mohan
	 * @param activeTask
	 * @param runDetailsBean
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForOneTime(ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: Starts");
		try{
			if(activeTask != null){
				ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
				oneTimeBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTask.getActiveTaskLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				oneTimeBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTask.getActiveTaskLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				runDetailsBean.add(oneTimeBean);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Active Task Frequency details for Daily
	 * 
	 * @author Mohan
	 * @param activeTask
	 * @param runDetailsBean
	 * @param session
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForDaily(ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: Starts");
		try{
			if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())){
				List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList;
				query = session.createQuery(" from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId="+activeTask.getId()+" ORDER BY ATFDTO.frequencyTime ");
				activeTaskDailyFrequencyList = query.list();
				if(activeTaskDailyFrequencyList != null && !activeTaskDailyFrequencyList.isEmpty()){
					for(int i=0; i<activeTaskDailyFrequencyList.size(); i++){
						ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
						String activeTaskStartTime;
						String activeTaskEndTime;
						activeTaskStartTime = activeTaskDailyFrequencyList.get(i).getFrequencyTime();
						if(i == (activeTaskDailyFrequencyList.size()-1)){
							activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
						}else{
							activeTaskEndTime = StudyMetaDataUtil.addSeconds(StudyMetaDataUtil.getCurrentDate()+" "+activeTaskDailyFrequencyList.get(i+1).getFrequencyTime(), -1);
							activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
						}
						dailyBean.setStartTime(activeTaskStartTime);
						dailyBean.setEndTime(activeTaskEndTime);
						runDetailsBean.add(dailyBean);
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Active Task Frequency details for Weekly
	 * 
	 * @author Mohan
	 * @param activeTask
	 * @param runDetailsBean
	 * @param session
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForWeekly(ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: Starts");
		try{
			if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd()) && StringUtils.isNotEmpty(activeTask.getDayOfTheWeek())){
				Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
				//create the runs frequency list based on the repeat activeTask count
				String activeTaskDay = activeTask.getDayOfTheWeek();
				String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
				while(repeatCount > 0){
					ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
					String activeTaskEndDate;
					String day = "";
					String weekEndDate;
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
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Active Task Frequency details for Monthly
	 * 
	 * @author Mohan
	 * @param activeTask
	 * @param runDetailsBean
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForMonthly(ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: Starts");
		try{
			if(StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart()) && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())){
				Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
				String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
				while(repeatCount > 0){
					ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
					String activeTaskEndDate;
					String monthEndDate;
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
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Active Task Frequency details for Manually Schedule
	 * 
	 * @author Mohan
	 * @param activeTask
	 * @param runDetailsBean
	 * @param session
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForManuallySchedule(ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: Starts");
		try{
			//get the custom frequency details based on the activeTaskId
			List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList; 
			query = session.createQuery("from ActiveTaskCustomFrequenciesDto ATCFDTO where ATCFDTO.activeTaskId="+activeTask.getId());
			manuallyScheduleFrequencyList = query.list();
			if(manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()){
				for(ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList){
					ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
					String startDate = customFrequencyDto.getFrequencyStartDate()+" "+customFrequencyDto.getFrequencyTime();
					String endDate = customFrequencyDto.getFrequencyEndDate()+" "+customFrequencyDto.getFrequencyTime();
					manuallyScheduleBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					manuallyScheduleBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					runDetailsBean.add(manuallyScheduleBean);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the frequency details for Questionaires based on the frequncy type selected i.e One Time, Within a Day, Daily, Weekly, Monthly, Manually Schedule
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @return ActivityFrequencyBean
	 * @throws DAOException
	 * 
	 */
	public ActivityFrequencyBean getFrequencyRunsDetailsForQuestionaires(QuestionnairesDto questionaire, ActivityFrequencyBean frequencyDetails, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Starts");
		List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
		try{
			switch (questionaire.getFrequency()) {
			case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
				runDetailsBean = getQuestionnaireFrequencyDetailsForDaily(questionaire, runDetailsBean, session);
				break;
			case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
				runDetailsBean = getQuestionnaireFrequencyDetailsForManuallySchedule(questionaire, runDetailsBean, session);
				break;
			default:
				break;
			}
			frequencyDetails.setRuns(runDetailsBean);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: Ends");
		return frequencyDetails;
	}

	/**
	 * This method is used to get the Questionnaire Frequency details for One Time
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param runDetailsBean
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForOneTime(QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: Starts");
		try{
			if(questionaire != null){
				ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
				oneTimeBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				oneTimeBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				runDetailsBean.add(oneTimeBean);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Questionnaire Frequency details for Daily
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param runDetailsBean
	 * @param session
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForDaily(QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: Starts");
		List<QuestionnairesFrequenciesDto> dailyFrequencyList = null;
		try{
			if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
				//get the list of frequency time based on the questionaire id 
				query = session.createQuery(" from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId="+questionaire.getId()+" ORDER BY QFDTO.frequencyTime ");
				dailyFrequencyList = query.list();
				if(dailyFrequencyList != null && !dailyFrequencyList.isEmpty()){
					for(int i=0; i<dailyFrequencyList.size(); i++){
						ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
						String activeTaskStartTime;
						String activeTaskEndTime;
						activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();
						if(i == (dailyFrequencyList.size()-1)){
							activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
						}else{
							activeTaskEndTime = StudyMetaDataUtil.addSeconds(StudyMetaDataUtil.getCurrentDate()+" "+dailyFrequencyList.get(i+1).getFrequencyTime(), -1);
							activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
						}
						dailyBean.setStartTime(activeTaskStartTime);
						dailyBean.setEndTime(activeTaskEndTime);
						runDetailsBean.add(dailyBean);
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Questionnaire Frequency details for Weekly
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param runDetailsBean
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForWeekly(QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: Starts");
		try{
			if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd()) && StringUtils.isNotEmpty(questionaire.getDayOfTheWeek())){
				Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
				//create the runs frequency list based on the repeat questionaire count
				String questionaireDay = questionaire.getDayOfTheWeek();
				String questionaireStartDate = questionaire.getStudyLifetimeStart();
				while(repeatCount > 0){
					ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
					String questionaireEndDate;
					String day = "";
					String weekEndDate;
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
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Questionnaire Frequency details for Monthly
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param runDetailsBean
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForMonthly(QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: Starts");
		try{
			if(StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart()) && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())){
				Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
				String questionaireStartDate = questionaire.getStudyLifetimeStart();
				while(repeatCount > 0){
					ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
					String questionaireEndDate;
					String monthEndDate;
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
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: Ends");
		return runDetailsBean;
	}

	/**
	 * This method is used to get the Questionnaire Frequency details for Manually Schedule
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param runDetailsBean
	 * @param session
	 * @return List<ActivityFrequencyScheduleBean>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForManuallySchedule(QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: Starts");
		try{
			//get the custom frequency details based on the questionaireId
			List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList; 
			query = session.createQuery("from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId="+questionaire.getId());
			manuallyScheduleFrequencyList = query.list();
			if(manuallyScheduleFrequencyList != null && !manuallyScheduleFrequencyList.isEmpty()){
				for(QuestionnairesCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList){
					ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
					manuallyScheduleBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(customFrequencyDto.getFrequencyEndDate()+" "+customFrequencyDto.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					manuallyScheduleBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(customFrequencyDto.getFrequencyStartDate()+" "+customFrequencyDto.getFrequencyTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					runDetailsBean.add(manuallyScheduleBean);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: Ends");
		return runDetailsBean;
	}

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
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getStepsInfoForQuestionnaires(String type, List<InstructionsDto> instructionsDtoList, List<QuestionsDto> questionsDtoList, List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Session session, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList, List<QuestionnairesStepsDto> questionaireStepsList, QuestionnairesDto questionnaireDto) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: Starts");
		TreeMap<Integer, QuestionnaireActivityStepsBean> stepsOrderSequenceTreeMap = new TreeMap<>();
		try{
			switch (type) {
			case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getInstructionDetailsForQuestionnaire(instructionsDtoList, sequenceNoMap, stepsSequenceTreeMap, questionnaireStepDetailsMap, questionnaireDto);
				break;
			case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getQuestionDetailsForQuestionnaire(questionsDtoList, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionaireStepsList, questionnaireDto);
				break;
			case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getFormDetailsForQuestionnaire(formsList, sequenceNoMap, session, stepsSequenceTreeMap, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionnaireDto);
				break;
			default:
				break;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: Ends");
		return stepsOrderSequenceTreeMap;
	}

	/**
	 * This method is used to map the instruction details
	 * 
	 * @author Mohan
	 * @param instructionsDtoList
	 * @param sequenceNoMap
	 * @return
	 * @throws DAOException
	 */
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getInstructionDetailsForQuestionnaire(List<InstructionsDto> instructionsDtoList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, QuestionnairesDto questionnaireDto) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: Starts");
		try{
			if( instructionsDtoList != null && !instructionsDtoList.isEmpty()){
				for(InstructionsDto instructionsDto : instructionsDtoList){
					QuestionnairesStepsDto instructionStepDetails = questionnaireStepDetailsMap.get((instructionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION).toString());
					QuestionnaireActivityStepsBean instructionBean = new QuestionnaireActivityStepsBean();

					instructionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
					instructionBean.setResultType("");
					instructionBean.setKey(StringUtils.isEmpty(instructionStepDetails.getStepShortTitle())?"":instructionStepDetails.getStepShortTitle());
					instructionBean.setTitle(StringUtils.isEmpty(instructionsDto.getInstructionTitle())?"":instructionsDto.getInstructionTitle());
					instructionBean.setText(StringUtils.isEmpty(instructionsDto.getInstructionText())?"":instructionsDto.getInstructionText());
					instructionBean.setSkippable((StringUtils.isEmpty(instructionStepDetails.getSkiappable()) || instructionStepDetails.getSkiappable().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true);
					instructionBean.setGroupName("");
					instructionBean.setRepeatable(false);
					instructionBean.setRepeatableText(instructionStepDetails.getRepeatableText() == null?"":instructionStepDetails.getRepeatableText());

					List<DestinationBean> destinations = new ArrayList<>();
					DestinationBean dest = new DestinationBean();
					dest.setCondition("");
					dest.setDestination((instructionStepDetails.getDestinationStepType()==null || instructionStepDetails.getDestinationStepType().isEmpty())?"":instructionStepDetails.getDestinationStepType());
					destinations.add(dest);
					instructionBean.setDestinations(destinations);

					stepsSequenceTreeMap.put(sequenceNoMap.get((instructionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION).toString()), instructionBean);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: Ends");
		return stepsSequenceTreeMap;
	}

	/**
	 * This method is used to map the question details
	 * 
	 * @author Mohan
	 * @param questionsDtoList
	 * @param sequenceNoMap
	 * @return
	 * @throws DAOException
	 */
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getQuestionDetailsForQuestionnaire(List<QuestionsDto> questionsDtoList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Session session, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList, List<QuestionnairesStepsDto> questionaireStepsList, QuestionnairesDto questionnaireDto) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: Starts");
		List<QuestionResponseSubTypeDto> destinationConditionList = null;
		try{
			if(questionsDtoList != null && !questionsDtoList.isEmpty()){
				for(QuestionsDto questionsDto : questionsDtoList){
					QuestionnairesStepsDto questionStepDetails = questionnaireStepDetailsMap.get((questionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION).toString());
					QuestionnaireActivityStepsBean questionBean = new QuestionnaireActivityStepsBean();

					questionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
					if(questionsDto.getResponseType() != null){
						for(QuestionResponsetypeMasterInfoDto masterInfo : questionResponseTypeMasterInfoList){
							if(masterInfo.getId().equals(questionsDto.getResponseType())){
								questionBean.setResultType(masterInfo.getResponseTypeCode());
								questionBean.setFormat(getQuestionaireQuestionFormatByType(questionsDto, masterInfo.getResponseTypeCode(), session));
								break;
							}
						}
					}else{
						questionBean.setResultType(""); //NA
					}
					questionBean.setText(StringUtils.isEmpty(questionsDto.getDescription())?"":questionsDto.getDescription());
					questionBean.setKey(StringUtils.isEmpty(questionStepDetails.getStepShortTitle())?"":questionStepDetails.getStepShortTitle());
					questionBean.setTitle(StringUtils.isEmpty(questionsDto.getQuestion())?"":questionsDto.getQuestion());
					questionBean.setSkippable((StringUtils.isEmpty(questionStepDetails.getSkiappable()) || questionStepDetails.getSkiappable().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true); //NA
					questionBean.setGroupName(""); //NA
					questionBean.setRepeatable(false); //NA
					questionBean.setRepeatableText(questionStepDetails.getRepeatableText() == null?"":questionStepDetails.getRepeatableText()); //NA

					//destination logic based on the branching
					List<DestinationBean> destinationsList = new ArrayList<>();
					//choice based branching allowed only for textchoice, textscale, imagechoice, boolean not for valuepicker
					if(!questionsDto.getResponseType().equals(4)){
						query = session.createQuery("from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionsDto.getId());
						destinationConditionList = query.list();
						if(destinationConditionList != null && !destinationConditionList.isEmpty()){
							for(QuestionResponseSubTypeDto destinationDto : destinationConditionList){
								DestinationBean destination = new DestinationBean();
								if(questionBean.getResultType().equalsIgnoreCase(StudyMetaDataConstants.QUESTION_BOOLEAN)){
									destination.setCondition(StringUtils.isEmpty(destinationDto.getValue())?"":destinationDto.getValue().toLowerCase());
								}else{
									destination.setCondition(StringUtils.isEmpty(destinationDto.getValue())?"":destinationDto.getValue());
								}

								if(questionnaireDto.getBranching()){
									if(destinationDto.getDestinationStepId()!=null&&destinationDto.getDestinationStepId().intValue()>0){
										destination = getDestinationStepTypeForResponseSubType(destination, destinationDto, questionaireStepsList);
									}else if(destinationDto.getDestinationStepId()!=null&&destinationDto.getDestinationStepId().equals(0)){
										destination.setDestination("");
									}else{
										destination.setDestination((questionStepDetails.getDestinationStepType()==null || questionStepDetails.getDestinationStepType().isEmpty())?"":questionStepDetails.getDestinationStepType());
									}
								}else{
									destination.setDestination((questionStepDetails.getDestinationStepType()==null || questionStepDetails.getDestinationStepType().isEmpty())?"":questionStepDetails.getDestinationStepType());
								}
								destinationsList.add(destination);
							}
						}
					}

					//conditional branching logic for sprint_1B
					if(Arrays.asList(StudyMetaDataConstants.CB_RESPONSE_TYPE.split(",")).contains(questionBean.getResultType()) && questionnaireDto.getBranching()){
						query = session.createQuery(" from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId="+questionsDto.getId()+" ORDER BY QRTDTO.responseTypeId DESC");
						QuestionReponseTypeDto reponseType = (QuestionReponseTypeDto) query.setMaxResults(1).uniqueResult();
						if(reponseType != null && StringUtils.isNotEmpty(reponseType.getFormulaBasedLogic()) && reponseType.getFormulaBasedLogic().equalsIgnoreCase(StudyMetaDataConstants.YES)){
							//isValueOfX is saved
							boolean isValueOfXSaved = false;
							if(destinationConditionList != null && !destinationConditionList.isEmpty() && destinationConditionList.size()==2){
								if(StringUtils.isNotEmpty(destinationConditionList.get(0).getValueOfX()) && 
										StringUtils.isNotEmpty(destinationConditionList.get(1).getValueOfX()) && 
										StringUtils.isNotEmpty(destinationConditionList.get(0).getOperator()) && 
										StringUtils.isNotEmpty(destinationConditionList.get(1).getOperator())){
									isValueOfXSaved = true;
									for(int i=0; i<destinationConditionList.size(); i++){
										destinationsList.get(i).setCondition(StringUtils.isEmpty(destinationConditionList.get(i).getValueOfX())?"":destinationConditionList.get(i).getValueOfX());
										destinationsList.get(i).setOperator(StringUtils.isEmpty(destinationConditionList.get(i).getOperator())?"":destinationConditionList.get(i).getOperator());
									}
								}
							}

							if(!isValueOfXSaved){
								destinationsList = getConditionalBranchingDestinations(reponseType, destinationsList, questionBean);
								Transaction transaction = null;
								try {
									transaction = session.beginTransaction();
									for(int i=0; i<destinationsList.size(); i++){
										QuestionResponseSubTypeDto destinationDto = destinationConditionList.get(i);
										destinationDto.setValueOfX(destinationsList.get(i).getCondition());
										destinationDto.setOperator(destinationsList.get(i).getOperator());
										session.save(destinationDto);
									}
									transaction.commit();
								} catch (Exception e) {
									if(transaction != null){
										transaction.rollback();
									}
									LOGGER.error("ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: ERROR", e);
								}
							}
						}
					}

					//get branching exists or not
					DestinationBean destination = new DestinationBean();
					destination.setCondition("");
					destination.setDestination((questionStepDetails.getDestinationStepType()==null || questionStepDetails.getDestinationStepType().isEmpty())?"":questionStepDetails.getDestinationStepType());
					destinationsList.add(destination);

					questionBean.setDestinations(destinationsList);

					/*phase_1B related code starts*/
					//add data check for the healthKitData for the question is having the healthKitDataKey
					questionBean.setHealthDataKey("");
					if(StringUtils.isNotEmpty(questionsDto.getAllowHealthKit()) && StudyMetaDataConstants.YES.equalsIgnoreCase(questionsDto.getAllowHealthKit()) && StringUtils.isNotEmpty(questionsDto.getHealthkitDatatype())){
						questionBean.setHealthDataKey(questionsDto.getHealthkitDatatype().trim());
						/*query = session.getNamedQuery("getHealthKitDisplayNameByKeyText").setString("key", questionsDto.getHealthkitDatatype().trim());
						Object healthDataKey = query.uniqueResult();
						if(healthDataKey!=null){
							questionBean.setHealthDataKey(healthDataKey.toString().trim());
						}*/
					}
					stepsSequenceTreeMap.put(sequenceNoMap.get((questionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION).toString()), questionBean);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: Ends");
		return stepsSequenceTreeMap;
	}

	/**
	 * This method is used to map the form details
	 * 
	 * @author Mohan
	 * @param formsList
	 * @param sequenceNoMap
	 * @param session
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getFormDetailsForQuestionnaire(List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, Session session, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList, QuestionnairesDto questionnaireDto) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: Starts");
		try{
			if( formsList != null && !formsList.isEmpty()){
				List<Integer> formQuestionIdsList = new ArrayList<>();
				TreeMap<Integer, Integer> formQuestionMap = new TreeMap<>();
				for(FormMappingDto formDto : formsList){
					formQuestionIdsList.add(formDto.getQuestionId());
					formQuestionMap.put(formDto.getSequenceNo(), formDto.getQuestionId());
				}

				if(!formQuestionIdsList.isEmpty()){
					QuestionnairesStepsDto formStepDetails = questionnaireStepDetailsMap.get((formsList.get(0).getFormId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM).toString());
					QuestionnaireActivityStepsBean formBean = new QuestionnaireActivityStepsBean();
					List<QuestionnaireActivityStepsBean> formSteps = new ArrayList<>();
					HashMap<Integer, QuestionnaireActivityStepsBean> formStepsMap = new HashMap<>();

					formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
					formBean.setResultType(StudyMetaDataConstants.RESULT_TYPE_GROUPED);
					formBean.setKey(StringUtils.isEmpty(formStepDetails.getStepShortTitle())?"":formStepDetails.getStepShortTitle());
					formBean.setTitle("");
					formBean.setText("");
					formBean.setSkippable((StringUtils.isEmpty(formStepDetails.getSkiappable()) || formStepDetails.getSkiappable().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true);
					formBean.setGroupName("");
					formBean.setRepeatable((formStepDetails.getRepeatable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(formStepDetails.getRepeatable()))?false:true);
					formBean.setRepeatableText(formStepDetails.getRepeatableText() == null?"":formStepDetails.getRepeatableText());

					List<DestinationBean> destinations = new ArrayList<>();
					DestinationBean dest = new DestinationBean();
					dest.setCondition("");
					dest.setDestination((formStepDetails.getDestinationStepType()==null || formStepDetails.getDestinationStepType().isEmpty())?"":formStepDetails.getDestinationStepType());
					destinations.add(dest);
					formBean.setDestinations(destinations);

					List<QuestionsDto> formQuestionsList;
					query = session.createQuery("from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(formQuestionIdsList, ',')+")");
					formQuestionsList = query.list();
					if(formQuestionsList != null && !formQuestionsList.isEmpty()){
						for(QuestionsDto formQuestionDto : formQuestionsList){
							QuestionnaireActivityStepsBean formQuestionBean = new QuestionnaireActivityStepsBean();
							formQuestionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
							if(formQuestionDto.getResponseType() != null){
								for(QuestionResponsetypeMasterInfoDto masterInfo : questionResponseTypeMasterInfoList){
									if(masterInfo.getId().equals(formQuestionDto.getResponseType())){
										formQuestionBean.setResultType(masterInfo.getResponseTypeCode());
										formQuestionBean.setFormat(getQuestionaireQuestionFormatByType(formQuestionDto, masterInfo.getResponseTypeCode(), session));
										break;
									}
								}
							}else{
								formQuestionBean.setResultType(""); //NA
							}
							formQuestionBean.setKey(StringUtils.isEmpty(formQuestionDto.getShortTitle())?"":formQuestionDto.getShortTitle());
							formQuestionBean.setTitle(StringUtils.isEmpty(formQuestionDto.getQuestion())?"":formQuestionDto.getQuestion());
							formQuestionBean.setSkippable((StringUtils.isEmpty(formQuestionDto.getSkippable()) || formQuestionDto.getSkippable().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true); //NA
							formQuestionBean.setGroupName(""); //NA
							formQuestionBean.setRepeatable(false); //NA
							formQuestionBean.setRepeatableText(""); //NA
							formQuestionBean.setText(StringUtils.isEmpty(formQuestionDto.getDescription())?"":formQuestionDto.getDescription());
							formQuestionBean.setHealthDataKey("");

							formStepsMap.put(formQuestionDto.getId(), formQuestionBean);
						}
					}

					//get the questions of form order by sequence number
					for(Integer key : formQuestionMap.keySet()){
						formSteps.add(formStepsMap.get(formQuestionMap.get(key)));
					}
					formBean.setSteps(formSteps);

					stepsSequenceTreeMap.put(sequenceNoMap.get((formsList.get(0).getFormId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM).toString()), formBean);
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: Ends");
		return stepsSequenceTreeMap;
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
	 *//*
	public Map<String, Object> getActiveTaskStepFormatByType(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue, String taskType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskStepFormatByType() :: Starts");
		Map<String, Object> activeTaskFormat = new HashMap<>();
		try{
			if(StringUtils.isNotEmpty(taskType)){
				switch (taskType) {
					case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
						if(StringUtils.isNotEmpty(attributeValues.getAttributeVal())){
							String[] durationArray = attributeValues.getAttributeVal().split(":");
							activeTaskFormat.put("duration", (Integer.parseInt(durationArray[0])*3600)+(Integer.parseInt(durationArray[1])*60));
						}else{
							activeTaskFormat.put("duration", 0);
						}
						break;
					case StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY:
						activeTaskFormat = spatialSpanMemoryDetails(attributeValues, masterAttributeValue);
						break;
					case StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI:
						activeTaskFormat.put("numberOfDisks", "");
						break;
					default:
						break;
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getActiveTaskStepFormatByType() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskStepFormatByType() :: Ends");
		return activeTaskFormat;
	}*/

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Object
	 * @throws DAOException
	 */
	public Object fetalKickCounterDetails(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue, FetalKickCounterFormatBean fetalKickCounterFormat) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - fetalKickCounterDetails() :: Starts");
		try{
			if(masterAttributeValue.getOrderByTaskType().equals(1)){
				if(StringUtils.isNotEmpty(attributeValues.getAttributeVal())){
					String[] durationArray = attributeValues.getAttributeVal().split(":");
					fetalKickCounterFormat.setDuration((Integer.parseInt(durationArray[0])*3600)+(Integer.parseInt(durationArray[1])*60));
				}
			}else{
				fetalKickCounterFormat.setKickCount(StringUtils.isEmpty(attributeValues.getAttributeVal())?StudyMetaDataConstants.MAX_KICK_COUNT:Integer.parseInt(attributeValues.getAttributeVal()));
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - fetalKickCounterDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - fetalKickCounterDetails() :: Ends");
		return fetalKickCounterFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Object
	 * @throws DAOException
	 */
	public Object spatialSpanMemoryDetails(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue, SpatialSpanMemoryFormatBean spatialSpanMemoryFormat) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Starts");
		try{
			switch (masterAttributeValue.getAttributeName().trim()) {
			case StudyMetaDataConstants.SSM_INITIAL: spatialSpanMemoryFormat.setInitialSpan(StringUtils.isEmpty(attributeValues.getAttributeVal())?0:Integer.parseInt(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_MINIMUM: spatialSpanMemoryFormat.setMinimumSpan(StringUtils.isEmpty(attributeValues.getAttributeVal())?0:Integer.parseInt(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_MAXIMUM: spatialSpanMemoryFormat.setMaximumSpan(StringUtils.isEmpty(attributeValues.getAttributeVal())?0:Integer.parseInt(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_PLAY_SPEED: spatialSpanMemoryFormat.setPlaySpeed(StringUtils.isEmpty(attributeValues.getAttributeVal())?0f:Float.parseFloat(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_MAX_TEST: spatialSpanMemoryFormat.setMaximumTests(StringUtils.isEmpty(attributeValues.getAttributeVal())?0:Integer.parseInt(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_MAX_CONSECUTIVE_FAILURES: spatialSpanMemoryFormat.setMaximumConsecutiveFailures(StringUtils.isEmpty(attributeValues.getAttributeVal())?0:Integer.parseInt(attributeValues.getAttributeVal()));
			break;
			case StudyMetaDataConstants.SSM_REQUIRE_REVERSAL: 
				spatialSpanMemoryFormat.setRequireReversal(StringUtils.isNotEmpty(attributeValues.getAttributeVal())&&attributeValues.getAttributeVal().equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)?true:false);
				break;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - spatialSpanMemoryDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Ends");
		return spatialSpanMemoryFormat;
	}

	/**
	 * This method is used to fetch the activeTask options
	 * 
	 * @author Mohan
	 * @return String[]
	 * @throws DAOException
	 */
	public String[] activeTaskOptions() throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskOptions() :: Starts");
		String[] activeTaskOptionsArray = new String[8];
		try{
			//Static options for activeTask
			activeTaskOptionsArray[0] = "excludeInstructions";
			activeTaskOptionsArray[1] = "excludeConclusion";
			activeTaskOptionsArray[2] = "excludeAccelerometer";
			activeTaskOptionsArray[3] = "excludeDeviceMotion";
			activeTaskOptionsArray[4] = "excludePedometer";
			activeTaskOptionsArray[5] = "excludeLocation";
			activeTaskOptionsArray[6] = "excludeHeartRate";
			activeTaskOptionsArray[7] = "excludeAudio";
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - activeTaskOptions() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskOptions() :: Ends");
		return activeTaskOptionsArray;
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
	public Map<String, Object> getQuestionaireQuestionFormatByType(QuestionsDto questionDto, String questionResultType, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		QuestionReponseTypeDto reponseType = null;
		try{
			if(StringUtils.isNotEmpty(questionResultType)){
				query = session.createQuery(" from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId="+questionDto.getId()+" ORDER BY QRTDTO.responseTypeId DESC");
				reponseType = (QuestionReponseTypeDto) query.setMaxResults(1).uniqueResult();
				switch (questionResultType) {
				case StudyMetaDataConstants.QUESTION_SCALE: //conditional branching
					questionFormat = formatQuestionScaleDetails(questionDto, reponseType);
					break;
				case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE: //conditional branching
					questionFormat = formatQuestionContinuousScaleDetails(questionDto, reponseType);
					break;
				case StudyMetaDataConstants.QUESTION_TEXT_SCALE:
					questionFormat = formatQuestionTextScaleDetails(questionDto, reponseType, session);
					break;
				case StudyMetaDataConstants.QUESTION_VALUE_PICKER:
					questionFormat = formatQuestionValuePickerDetails(questionDto, reponseType, session);
					break;
				case StudyMetaDataConstants.QUESTION_IMAGE_CHOICE:
					questionFormat = formatQuestionImageChoiceDetails(questionDto, reponseType, session);
					break;
				case StudyMetaDataConstants.QUESTION_TEXT_CHOICE:
					questionFormat = formatQuestionTextChoiceDetails(questionDto, reponseType, session);
					break;
				case StudyMetaDataConstants.QUESTION_NUMERIC: //conditional branching
					questionFormat = formatQuestionNumericDetails(questionDto, reponseType);
					break;
				case StudyMetaDataConstants.QUESTION_DATE:
					questionFormat = formatQuestionDateDetails(questionDto, reponseType);
					break;
				case StudyMetaDataConstants.QUESTION_TEXT: 
					questionFormat = formatQuestionTextDetails(questionDto, reponseType);
					break;
				case StudyMetaDataConstants.QUESTION_EMAIL:
					questionFormat.put("placeholder", (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))?"":reponseType.getPlaceholder());
					break;
				case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:	//conditional branching
					questionFormat.put("default", (reponseType == null ||  StringUtils.isEmpty(reponseType.getDefalutTime()))?0:getTimeInSeconds(reponseType.getDefalutTime()));
					questionFormat.put("step", (reponseType == null || reponseType.getStep() == null)?1:getTimeIntervalStep(reponseType.getStep())); //In minutes 1-30
					break;
				case StudyMetaDataConstants.QUESTION_HEIGHT: //conditional branching
					questionFormat.put("measurementSystem", (reponseType == null || reponseType.getMeasurementSystem() == null)?"":reponseType.getMeasurementSystem()); //Local/Metric/US
					questionFormat.put("placeholder", (reponseType == null || StringUtils.isEmpty(reponseType.getPlaceholder()))?"":reponseType.getPlaceholder());
					break;
				case StudyMetaDataConstants.QUESTION_LOCATION:
					questionFormat.put("useCurrentLocation", (reponseType == null || (reponseType.getUseCurrentLocation() == null || !reponseType.getUseCurrentLocation()))?false:true);
					break;
				default:
					break;
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionScaleDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionScaleDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("maxValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxValue()))?10000:Integer.parseInt(reponseType.getMaxValue()));
			questionFormat.put("minValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMinValue()))?-10000:Integer.parseInt(reponseType.getMinValue()));
			questionFormat.put("step", (reponseType==null || reponseType.getStep()==null)?1:getScaleStepSize(reponseType.getStep(), (Integer) questionFormat.get("maxValue"), (Integer) questionFormat.get("minValue")));
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?(Integer) questionFormat.get("minValue"):getScaleDefaultValue(reponseType.getStep(), (Integer) questionFormat.get("maxValue"), (Integer) questionFormat.get("minValue"), Integer.parseInt(reponseType.getDefaultValue())));
			questionFormat.put("vertical", (reponseType==null || reponseType.getVertical()==null || !reponseType.getVertical())?false:true);
			questionFormat.put("maxDesc", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxDescription()))?"":reponseType.getMaxDescription());
			questionFormat.put("minDesc", (reponseType==null || StringUtils.isEmpty(reponseType.getMinDescription()))?"":reponseType.getMinDescription());
			//update in phase 1B
			questionFormat.put("maxImage", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxImage()))?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+reponseType.getMaxImage()));
			questionFormat.put("minImage", (reponseType==null || StringUtils.isEmpty(reponseType.getMinImage()))?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+reponseType.getMinImage()));
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionScaleDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionScaleDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionContinuousScaleDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("maxValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxValue()))?10000:Double.parseDouble(reponseType.getMaxValue()));
			questionFormat.put("minValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMinValue()))?-10000:Double.parseDouble(reponseType.getMinValue()));
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?(Double) questionFormat.get("minValue"):Double.parseDouble(reponseType.getDefaultValue()));
			questionFormat.put("maxFractionDigits", (reponseType==null || reponseType.getMaxFractionDigits()==null)?0:reponseType.getMaxFractionDigits());
			questionFormat.put("vertical", (reponseType==null || reponseType.getVertical()==null || !reponseType.getVertical())?false:true);
			questionFormat.put("maxDesc", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxDescription()))?"":reponseType.getMaxDescription());
			questionFormat.put("minDesc", (reponseType==null || StringUtils.isEmpty(reponseType.getMinDescription()))?"":reponseType.getMinDescription());
			//update in phase_1B
			questionFormat.put("maxImage", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxImage()))?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+reponseType.getMaxImage()));
			questionFormat.put("minImage", (reponseType==null || StringUtils.isEmpty(reponseType.getMinImage()))?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+reponseType.getMinImage()));
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionTextScaleDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextScaleDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> textChoicesList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> textScaleMap = new LinkedHashMap<>();
					textScaleMap.put("text", StringUtils.isEmpty(subType.getText())?"":subType.getText());
					textScaleMap.put("value", StringUtils.isEmpty(subType.getValue())?"":subType.getValue());
					textScaleMap.put("detail", StringUtils.isEmpty(subType.getDetail())?"":subType.getDetail());
					textScaleMap.put("exclusive", (subType.getExclusive() == null || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))?true:false);
					textChoicesList.add(textScaleMap);
				}
			}
			questionFormat.put("textChoices", textChoicesList);
			questionFormat.put("default", (reponseType==null || StringUtils.isEmpty(reponseType.getDefaultValue()))?1:Integer.parseInt(reponseType.getDefaultValue()));
			questionFormat.put("vertical", (reponseType==null || reponseType.getVertical()==null || !reponseType.getVertical())?false:true);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionTextScaleDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextScaleDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionValuePickerDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionValuePickerDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> valuePickerList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> valuePickerMap = new LinkedHashMap<>();
					valuePickerMap.put("text", StringUtils.isEmpty(subType.getText())?"":subType.getText());
					valuePickerMap.put("value", StringUtils.isEmpty(subType.getValue())?"":subType.getValue());
					valuePickerMap.put("detail", StringUtils.isEmpty(subType.getDetail())?"":subType.getDetail());
					valuePickerMap.put("exclusive", (StringUtils.isEmpty(subType.getExclusive()) || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))?true:false);
					valuePickerList.add(valuePickerMap);
				}
			}
			questionFormat.put("textChoices", valuePickerList);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionValuePickerDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionValuePickerDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionImageChoiceDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> imageChoicesList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> imageChoiceMap = new LinkedHashMap<>();
					imageChoiceMap.put("image", StringUtils.isEmpty(subType.getImage())?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+subType.getImage()));
					imageChoiceMap.put("selectedImage", StringUtils.isEmpty(subType.getSelectedImage())?"":getBase64Image(propMap.get("fda.smd.questionnaire.image").trim()+subType.getSelectedImage()));
					imageChoiceMap.put("text", StringUtils.isEmpty(subType.getText())?"":subType.getText());
					imageChoiceMap.put("value", StringUtils.isEmpty(subType.getValue())?"":subType.getValue());
					imageChoicesList.add(imageChoiceMap);
				}
			}
			questionFormat.put("imageChoices", imageChoicesList);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionTextChoiceDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
					textChoiceMap.put("text", StringUtils.isEmpty(subType.getText())?"":subType.getText());
					textChoiceMap.put("value", StringUtils.isEmpty(subType.getValue())?"":subType.getValue());
					textChoiceMap.put("detail", StringUtils.isEmpty(subType.getDetail())?"":subType.getDetail());
					textChoiceMap.put("exclusive", (subType.getExclusive() == null || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true);
					textChoiceMapList.add(textChoiceMap);
				}
			}
			questionFormat.put("textChoices", textChoiceMapList);
			questionFormat.put("selectionStyle", (reponseType==null || StringUtils.isEmpty(reponseType.getSelectionStyle()))?"":reponseType.getSelectionStyle()); //Single/Multiple
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionNumericDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionNumericDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("style", (reponseType==null || StringUtils.isEmpty(reponseType.getStyle()))?StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:reponseType.getStyle());
			questionFormat.put("unit", (reponseType==null || StringUtils.isEmpty(reponseType.getUnit()))?"":reponseType.getUnit());
			if(questionFormat.get("style").toString().equalsIgnoreCase(StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER)){
				questionFormat.put("minValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMinValue()))?0:Integer.parseInt(reponseType.getMinValue()));
				questionFormat.put("maxValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxValue()))?10000:Integer.parseInt(reponseType.getMaxValue()));
			}else{
				questionFormat.put("minValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMinValue()))?0d:Double.parseDouble(reponseType.getMinValue()));
				questionFormat.put("maxValue", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxValue()))?10000d:Double.parseDouble(reponseType.getMaxValue()));
			}
			questionFormat.put("placeholder", (reponseType==null || StringUtils.isEmpty(reponseType.getPlaceholder()))?"":reponseType.getPlaceholder());
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionNumericDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionNumericDetails() :: Ends");
		return questionFormat;
	}

	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionDateDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionDateDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		String dateFormat = "";
		try{
			questionFormat.put("style", (reponseType==null || StringUtils.isEmpty(reponseType.getStyle()))?"":reponseType.getStyle());
			if(reponseType != null && StringUtils.isNotEmpty(reponseType.getStyle()) && reponseType.getStyle().equalsIgnoreCase(StudyMetaDataConstants.QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATE)){
				dateFormat = "yyyy-MM-dd";
			}else{
				dateFormat = "yyyy-MM-dd HH:mm:ss";
			}
			questionFormat.put("minDate", (reponseType==null || StringUtils.isEmpty(reponseType.getMinDate()))?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getMinDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			questionFormat.put("maxDate", (reponseType==null || StringUtils.isEmpty(reponseType.getMaxDate()))?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getMaxDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			questionFormat.put("default", (reponseType==null || StringUtils.isEmpty(reponseType.getDefaultDate()))?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getDefaultDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")); //Date
			questionFormat.put("dateRange", (reponseType==null || StringUtils.isEmpty(reponseType.getSelectionStyle())?"":this.getDateRangeType(reponseType.getSelectionStyle())));
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionDateDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionDateDetails() :: Ends");
		return questionFormat;
	}
	
	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> formatQuestionTextDetails(QuestionsDto questionDto, QuestionReponseTypeDto reponseType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextDetails() :: Starts");
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("maxLength", (reponseType==null || reponseType.getMaxLength()==null)?0:reponseType.getMaxLength());
			questionFormat.put("validationRegex", (reponseType==null || StringUtils.isEmpty(reponseType.getValidationRegex()))?"":reponseType.getValidationRegex());
			questionFormat.put("invalidMessage", (reponseType==null || StringUtils.isEmpty(reponseType.getInvalidMessage()))?"Invalid Input. Please try again.":reponseType.getInvalidMessage());
			questionFormat.put("multipleLines", (reponseType==null || reponseType.getMultipleLines()==null || !reponseType.getMultipleLines())?false:true);
			questionFormat.put("placeholder", (reponseType==null || StringUtils.isEmpty(reponseType.getPlaceholder()))?"":reponseType.getPlaceholder());
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionTextDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextDetails() :: Ends");
		return questionFormat;
	}

	/*-----------------------------Activity data methods ends----------------------------------*/
	/**
	 * This method is used to get the start and end date time of active task
	 * 
	 * @author Mohan
	 * @param activeTaskDto
	 * @param activityBean
	 * @param session
	 * @return ActivitiesBean
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ActivitiesBean getTimeDetailsByActivityIdForActiveTask(ActiveTaskDto activeTaskDto, ActivitiesBean activityBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: Starts");
		String startDateTime = "";
		String endDateTime = "";
		try{
			startDateTime = activeTaskDto.getActiveTaskLifetimeStart()+" "+StudyMetaDataConstants.DEFAULT_MIN_TIME;
			endDateTime = StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())?"":activeTaskDto.getActiveTaskLifetimeEnd()+" "+StudyMetaDataConstants.DEFAULT_MAX_TIME;
			if(StringUtils.isNotEmpty(activeTaskDto.getFrequency())){
				if((activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) || (activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) || (activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))){
					ActiveTaskFrequencyDto activeTaskFrequency;
					query = session.createQuery("from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId="+activeTaskDto.getId());
					activeTaskFrequency = (ActiveTaskFrequencyDto) query.uniqueResult();
					if(activeTaskFrequency != null){
						if(StringUtils.isNotEmpty(activeTaskFrequency.getFrequencyTime())){
							startDateTime = activeTaskDto.getActiveTaskLifetimeStart()+" "+activeTaskFrequency.getFrequencyTime();
							if(!activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME) && !activeTaskFrequency.isStudyLifeTime()){
								endDateTime = activeTaskDto.getActiveTaskLifetimeEnd()+" "+activeTaskFrequency.getFrequencyTime();
							}
						}
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StringUtils.isEmpty(endDateTime)?"":StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}else if(activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)){
					List<ActiveTaskFrequencyDto> activeTaskFrequencyList = null;
					query = session.createQuery("from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId="+activeTaskDto.getId()+" ORDER BY ATFDTO.frequencyTime");
					activeTaskFrequencyList = query.list();
					if(activeTaskFrequencyList != null && !activeTaskFrequencyList.isEmpty()){
						startDateTime = activeTaskDto.getActiveTaskLifetimeStart()+" "+activeTaskFrequencyList.get(0).getFrequencyTime();
						endDateTime = activeTaskDto.getActiveTaskLifetimeEnd()+" "+StudyMetaDataConstants.DEFAULT_MAX_TIME;
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}else if(activeTaskDto.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)){
					List<ActiveTaskCustomFrequenciesDto> activeTaskCustomFrequencyList = null;
					query = session.createQuery("from ActiveTaskCustomFrequenciesDto ATCFDTO where ATCFDTO.activeTaskId="+activeTaskDto.getId()+" ORDER BY ATCFDTO.frequencyTime");
					activeTaskCustomFrequencyList = query.list();
					if(activeTaskCustomFrequencyList != null && !activeTaskCustomFrequencyList.isEmpty()){
						String startDate = activeTaskCustomFrequencyList.get(0).getFrequencyStartDate();
						String endDate = activeTaskCustomFrequencyList.get(0).getFrequencyEndDate();
						for(ActiveTaskCustomFrequenciesDto customFrequency : activeTaskCustomFrequencyList){
							if(StudyMetaDataConstants.SDF_DATE.parse(startDate).after(StudyMetaDataConstants.SDF_DATE.parse(customFrequency.getFrequencyStartDate()))){
								startDate = customFrequency.getFrequencyStartDate();
							}

							if(StudyMetaDataConstants.SDF_DATE.parse(endDate).before(StudyMetaDataConstants.SDF_DATE.parse(customFrequency.getFrequencyEndDate()))){
								endDate = customFrequency.getFrequencyEndDate();
							}
						}
						startDateTime = startDate+" "+activeTaskCustomFrequencyList.get(0).getFrequencyTime();
						endDateTime = endDate+" "+activeTaskCustomFrequencyList.get(activeTaskCustomFrequencyList.size()-1).getFrequencyTime();
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: Ends");
		return activityBean;
	}

	/**
	 * This method is used to get the start and end date time of questionnaire
	 * 
	 * @author Mohan
	 * @param questionaire
	 * @param activityBean
	 * @param session
	 * @return ActivitiesBean
	 * @throws DAOException
	 */
	public ActivitiesBean getTimeDetailsByActivityIdForQuestionnaire(QuestionnairesDto questionaire, ActivitiesBean activityBean, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: Starts");
		String startDateTime = "";
		String endDateTime = "";
		try{
			startDateTime = questionaire.getStudyLifetimeStart()+" "+StudyMetaDataConstants.DEFAULT_MIN_TIME;
			endDateTime = StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())?"":questionaire.getStudyLifetimeEnd()+" "+StudyMetaDataConstants.DEFAULT_MAX_TIME;
			if(StringUtils.isNotEmpty(questionaire.getFrequency())){
				if((questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) || (questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) || (questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))){
					QuestionnairesFrequenciesDto questionnairesFrequency;
					query = session.createQuery("from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId="+questionaire.getId());
					questionnairesFrequency = (QuestionnairesFrequenciesDto) query.uniqueResult();
					if(questionnairesFrequency != null){
						if(StringUtils.isNotEmpty(questionnairesFrequency.getFrequencyTime())){
							startDateTime = questionaire.getStudyLifetimeStart()+" "+questionnairesFrequency.getFrequencyTime();
							if(!questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME) && !questionnairesFrequency.getIsStudyLifeTime()){
								endDateTime = questionaire.getStudyLifetimeEnd()+" "+questionnairesFrequency.getFrequencyTime();
							}
						}
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StringUtils.isEmpty(endDateTime)?"":StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}else if(questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)){
					List<QuestionnairesFrequenciesDto> questionnairesFrequencyList = null;
					query = session.createQuery("from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId="+questionaire.getId()+" ORDER BY QFDTO.frequencyTime");
					questionnairesFrequencyList = query.list();
					if(questionnairesFrequencyList != null && !questionnairesFrequencyList.isEmpty()){
						startDateTime = questionaire.getStudyLifetimeStart()+" "+questionnairesFrequencyList.get(0).getFrequencyTime();
						endDateTime = questionaire.getStudyLifetimeEnd()+" "+StudyMetaDataConstants.DEFAULT_MAX_TIME;
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}else if(questionaire.getFrequency().equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)){
					List<QuestionnairesCustomFrequenciesDto> questionnaireCustomFrequencyList = null;
					query = session.createQuery("from QuestionnairesCustomFrequenciesDto QCFDTO where QCFDTO.questionnairesId="+questionaire.getId()+" ORDER BY QCFDTO.frequencyTime");
					questionnaireCustomFrequencyList = query.list();
					if(questionnaireCustomFrequencyList != null && !questionnaireCustomFrequencyList.isEmpty()){
						String startDate = questionnaireCustomFrequencyList.get(0).getFrequencyStartDate();
						String endDate = questionnaireCustomFrequencyList.get(0).getFrequencyEndDate();
						for(QuestionnairesCustomFrequenciesDto customFrequency : questionnaireCustomFrequencyList){
							if(StudyMetaDataConstants.SDF_DATE.parse(startDate).after(StudyMetaDataConstants.SDF_DATE.parse(customFrequency.getFrequencyStartDate()))){
								startDate = customFrequency.getFrequencyStartDate();
							}

							if(StudyMetaDataConstants.SDF_DATE.parse(endDate).before(StudyMetaDataConstants.SDF_DATE.parse(customFrequency.getFrequencyEndDate()))){
								endDate = customFrequency.getFrequencyEndDate();
							}
						}
						startDateTime = startDate+" "+questionnaireCustomFrequencyList.get(0).getFrequencyTime();
						endDateTime = endDate+" "+questionnaireCustomFrequencyList.get(questionnaireCustomFrequencyList.size()-1).getFrequencyTime();
					}
					activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: Ends");
		return activityBean;
	}

	/**
	 * This method is used to get the type of destination step based on the stepId
	 * 
	 * @author Mohan
	 * @param questionaireStepsList
	 * @return List<QuestionnairesStepsDto>
	 * @throws DAOException
	 */
	public List<QuestionnairesStepsDto> getDestinationStepType(List<QuestionnairesStepsDto> questionaireStepsList) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepType() :: Starts");
		List<QuestionnairesStepsDto> questionnaireStepsTypeList = new ArrayList<>();
		try{
			for(QuestionnairesStepsDto questionnaireStepsDto : questionaireStepsList){
				for(QuestionnairesStepsDto stepsDto : questionaireStepsList){
					if(questionnaireStepsDto.getDestinationStep().equals(stepsDto.getStepId())){
						questionnaireStepsDto.setDestinationStepType(StringUtils.isEmpty(stepsDto.getStepShortTitle())?"":stepsDto.getStepShortTitle());
						break;
					}
				}
				questionnaireStepsTypeList.add(questionnaireStepsDto);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getDestinationStepType() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepType() :: Ends");
		return questionnaireStepsTypeList;
	}

	/**
	 * This method is used to get the destinationstep details for the question response subtype
	 * 
	 * @author Mohan
	 * @param destinationBean
	 * @param destinationDto
	 * @param questionaireStepsList
	 * @return DestinationBean
	 * @throws DAOException
	 */
	public DestinationBean getDestinationStepTypeForResponseSubType(DestinationBean destinationBean, QuestionResponseSubTypeDto destinationDto, List<QuestionnairesStepsDto> questionaireStepsList) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: Starts");
		try{
			for(QuestionnairesStepsDto stepsDto : questionaireStepsList){
				if(destinationDto.getDestinationStepId().equals(stepsDto.getStepId())){
					destinationBean.setDestination(StringUtils.isEmpty(stepsDto.getStepShortTitle())?"":stepsDto.getStepShortTitle());
					break;
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: Ends");
		return destinationBean;
	}

	/**
	 * This method is used to get the base64 string for image
	 * 
	 * @author Mohan
	 * @param imagePath
	 * @return String
	 * @throws DAOException
	 */
	public String getBase64Image(String imagePath) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getBase64Image() :: Starts");
		String base64Image = "";
		byte[] imageBytes = null;
		try{
			URL url = new URL(imagePath);
			if(url.getProtocol().equalsIgnoreCase("https")){
				HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
				InputStream ins = con.getInputStream();
				imageBytes = IOUtils.toByteArray(ins);
			}else{
				imageBytes = IOUtils.toByteArray(new URL(imagePath));
			}
			base64Image = Base64.getEncoder().encodeToString(imageBytes);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getBase64Image() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getBase64Image() :: Ends");
		return base64Image;
	}

	/**
	 * This method is used to get the actual stepcount based on the maxValue and minValue
	 * 
	 * @author Mohan
	 * @param step
	 * @param maxValue
	 * @param minValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getScaleStepCount(Integer step, Integer maxValue, Integer minValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepCount() :: Starts");
		Integer scaleStepCount = 1;
		Integer maxStepCount = 13;
		List<Integer> stepCountList = new ArrayList<>();
		try{
			Integer diff = maxValue - minValue;
			while(maxStepCount > 0){
				if((diff%maxStepCount) == 0){
					stepCountList.add(maxStepCount);
				}
				maxStepCount--;
			}
			if(stepCountList.contains(step)){
				scaleStepCount = step;
				return scaleStepCount;
			}
			scaleStepCount = stepCountList.get(0);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getScaleStepCount() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepCount() :: Ends");
		return scaleStepCount;
	}

	/**
	 * This method is used to get the step size based on the number of steps, maxvalue and minvalue
	 * 
	 * @author Mohan
	 * @param step
	 * @param maxValue
	 * @param minValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getScaleStepSize(Integer step, Integer maxValue, Integer minValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepSize() :: Starts");
		Integer scaleStepCount = step;
		try{
			scaleStepCount = (maxValue-minValue)/step;
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getScaleStepSize() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleStepSize() :: Ends");
		return scaleStepCount;
	}

	/**
	 * This method is used to get the default value based on the maxValue, minValue and stepcount
	 * 
	 * @author Mohan
	 * @param step
	 * @param maxValue
	 * @param minValue
	 * @param defaultValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getScaleDefaultValue(Integer step, Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleDefaultValue() :: Starts");
		Integer stepSize = (maxValue-minValue)/step;
		Integer scaleDefaultValue = minValue;
		try{
			scaleDefaultValue += (stepSize*defaultValue);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getScaleDefaultValue() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getScaleDefaultValue() :: Ends");
		return scaleDefaultValue;
	}

	/**
	 * This method is used to get the maximum fraction digits based on the maxValue and minValue
	 * 
	 * @author Mohan
	 * @param maxValue
	 * @param minValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getContinuousScaleMaxFractionDigits(Integer maxValue, Integer minValue, Integer actualFractionDigits) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Starts");
		Integer maxFracDigits=0;
		Integer minTemp=0;
		Integer maxTemp=0;
		try{
			//max value check
			if(maxValue>0&&maxValue<=1){
				maxTemp = 4;
			}else if(maxValue>1&&maxValue<=10){
				maxTemp = 3;
			}else if(maxValue>10&&maxValue<=100){
				maxTemp = 2;
			}else if(maxValue>100&&maxValue<=1000){
				maxTemp = 1;
			}else if(maxValue>1000&&maxValue<=10000){
				maxTemp = 0;
			}

			//min value check
			if(minValue>=-10000&&minValue<-1000){
				minTemp = 0;
			}else if(minValue>=-1000&&minValue<-100){
				minTemp = 1;
			}else if(minValue>=-100&&minValue<-10){
				minTemp = 2;
			}else if(minValue>=-10&&minValue<-1){
				minTemp = 3;
			}else if(minValue>=-1){
				minTemp = 4;
			}
			maxFracDigits = (maxTemp>minTemp)?minTemp:maxTemp;

			if(actualFractionDigits<=maxFracDigits){
				maxFracDigits = actualFractionDigits;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Ends");
		return maxFracDigits;
	}

	/**
	 * This method is used to check the default value for constinuous scale response type
	 * 
	 * @author Mohan
	 * @param maxValue
	 * @param minValue
	 * @param defaultValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getContinuousScaleDefaultValue(Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleDefaultValue() :: Starts");
		Integer continuousScaleDefaultValue = minValue;
		try{
			if(defaultValue!=null&&(defaultValue>=minValue)&&(defaultValue<=maxValue)){
				continuousScaleDefaultValue = defaultValue;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getContinuousScaleDefaultValue() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleDefaultValue() :: Ends");
		return continuousScaleDefaultValue;
	}

	/**
	 * This method is used to get the actual step count for the time interval response type
	 * 
	 * @author Mohan
	 * @param stepValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getTimeIntervalStep(Integer stepValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeIntervalStep() :: Starts");
		Integer step = 1;
		String stepIds = "1,2,3,4,5,6,10,12,15,20,30";
		try{
			String[] stepArray = stepIds.split(",");
			for(int i=0; i<stepArray.length; i++){
				if(stepValue > Integer.parseInt(stepArray[i])){
					step = Integer.parseInt(stepArray[i]);
				}else{
					step = Integer.parseInt(stepArray[i]);
					break;
				}
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getTimeIntervalStep() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeIntervalStep() :: Ends");
		return step;
	}

	/*phase_1B starts*/
	/**
	 * This method is used to get the seconds from the input time
	 * 
	 * @author Mohan
	 * @param time
	 * @return Long
	 * @throws DAOException
	 */
	public Long getTimeInSeconds(String time) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeInSeconds() :: Starts");
		Long defaultTime = 0l;
		try{
			String[] timeArray = time.split(":");
			defaultTime += (long) (Integer.parseInt(timeArray[0])*3600);
			defaultTime += (long) (Integer.parseInt(timeArray[1])*60);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getTimeInSeconds() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getTimeInSeconds() :: Ends");
		return defaultTime;
	}
	
	
	/*********************************** phase_1B conditional branching related methods to sovleForX ***********************************/
	
	/**
	 * This method is used to evaluate the formula to solve for 'X'
	 * 
	 * @author Mohan
	 * @param conditionFormula
	 * @param destinationsList
	 * @param questionBean
	 * @return List<DestinationBean>
	 * @throws DAOException
	 */
	public List<DestinationBean> getConditionalBranchingDestinations(QuestionReponseTypeDto reponseType, List<DestinationBean> destinationsList, QuestionnaireActivityStepsBean questionBean) throws DAOException {
		LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Starts");
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		String conditionFormula = "";
		String operator = "";
		StringTokenizer tokenizer = null;
		String LHS = "";
		String RHS = "";
		String tempFormula = "";
		boolean flag = false;
		Double maxFractionDigit = 1D;
		Double minValue = 0D;
		Double maxValue = 0D;
		Double valueOfX = 0D;
		Integer digitFormat = 0;
		Map<String, Object> prerequisitesMap = new HashMap<>();
		List<DestinationBean> updatedDestinationsList = destinationsList;
		String formatXValue = "";
		try{
			//validate whether formula is empty or not before solving for 'X'
			if(StringUtils.isNotEmpty(reponseType.getConditionFormula())){
				conditionFormula = reponseType.getConditionFormula();
				//check the expression contains '=', if yes replace it with '==' to evaluate the expression
				if(!reponseType.getConditionFormula().contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL) 
						&& !reponseType.getConditionFormula().contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL) 
						&& reponseType.getConditionFormula().contains("=")){
					conditionFormula = reponseType.getConditionFormula().replaceAll("=", StudyMetaDataConstants.CBO_OPERATOR_EQUAL);
				}
				LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Formula ----------> "+conditionFormula);

				//get the minimum and maximum value range for response type
				prerequisitesMap = conditionalBranchingPrerequisites(questionBean);
				minValue = (Double) prerequisitesMap.get("minValue");
				maxValue = (Double) prerequisitesMap.get("maxValue");
				maxFractionDigit = (Double) prerequisitesMap.get("maxFractionDigit");
				digitFormat = (Integer) prerequisitesMap.get("digitFormat");
				formatXValue = "%."+digitFormat+"f";
				valueOfX = minValue;

				//find position of X in the equation i.e LHS or RHS
				operator = getOperatorFromConditionalFormula(conditionFormula);

				//evaluate the position of X in the equation
				if(StringUtils.isNotEmpty(operator)){
					tokenizer = new StringTokenizer(conditionFormula, operator);
					LHS = tokenizer.nextToken().trim();
					RHS = tokenizer.nextToken().trim();
				}

				//find minimum value of X
				while(valueOfX <= maxValue) {
					tempFormula = conditionFormula.replaceAll("x", valueOfX>=0?valueOfX.toString():"("+valueOfX.toString()+")");
					flag = (boolean) engine.eval(tempFormula);
					if(flag){
						LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Condition Formula  ----------> "+tempFormula+" ----------> "+valueOfX);

						//validation for equal to condition
						if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)){
							updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
									StudyMetaDataConstants.CBO_EQUAL_TO, StudyMetaDataConstants.CBO_NOT_EQUAL_TO);
							break;
						}

						//validation for greater than condition
						if(LHS.contains("x") && !RHS.contains("x")){
							if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN)){
								updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
										StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO, StudyMetaDataConstants.CBO_LESSER_THAN);
								break;
							}
						}else{
							if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN)){
								updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
										StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO, StudyMetaDataConstants.CBO_LESSER_THAN);
								break;
							}
						}
					}else{
						LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Condition Formula  ----------> "+tempFormula+" ----------> "+valueOfX);

						//validation for not equal to condition
						if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)){
							updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
									StudyMetaDataConstants.CBO_NOT_EQUAL_TO, StudyMetaDataConstants.CBO_EQUAL_TO);
							break;
						}

						//validation for lesser than condition
						if(LHS.contains("x") && !RHS.contains("x")){
							if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN)){
								updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
										StudyMetaDataConstants.CBO_LESSER_THAN, StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
								break;
							}
						}else{
							if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN)){
								updatedDestinationsList = getConditionalBranchingFormat(destinationsList, valueOfX.toString(), 
										StudyMetaDataConstants.CBO_LESSER_THAN, StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
								break;
							}
						}
					}
					valueOfX += maxFractionDigit;
					valueOfX = Double.parseDouble(String.format(formatXValue, valueOfX));
				}

				//format the value of X according to type
				updatedDestinationsList = formatValueOfX(updatedDestinationsList, questionBean);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getConditionalBranchingDestinations() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingDestinations() :: Ends");
		return updatedDestinationsList;
	}
	
	/**
	 * This method is used to get the branching prerequisites
	 * 
	 * @author Mohan
	 * @param questionBean
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> conditionalBranchingPrerequisites(QuestionnaireActivityStepsBean questionBean) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - conditionalBranchingPrerequisites() :: Starts");
		Map<String, Object> prerequisitesMap = new HashMap<>();
		Double maxFractionDigit = 1D;
		Double minValue = 0D;
		Double maxValue = 0D;
		Integer digitFormat = 0;
		try{
			//get the minimum and maximum value range for response type
			switch (questionBean.getResultType()) {
			case StudyMetaDataConstants.QUESTION_SCALE:
				minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
				maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());
				maxFractionDigit = 1D;
				digitFormat = 0;
				break;
			case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
				minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
				maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());
				switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
				case 0:	maxFractionDigit = 1D;
				digitFormat = 0;
				break;
				case 1:	maxFractionDigit = 0.1D;
				digitFormat = 1;
				break;
				case 2:	maxFractionDigit = 0.01D;
				digitFormat = 2;
				break;
				case 3:	maxFractionDigit = 0.001D;
				digitFormat = 3;
				break;
				case 4:	maxFractionDigit = 0.0001D;
				digitFormat = 4;
				break;
				default:
					break;
				}
				break;
			case StudyMetaDataConstants.QUESTION_NUMERIC:
				minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
				maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());
				switch (questionBean.getFormat().get("style").toString()) {
				case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:	maxFractionDigit = 1D;
				digitFormat = 0;
				break;
				case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:	maxFractionDigit = 0.01D;
				digitFormat = 2;
				break;
				default:
					break;
				}
				break;
			case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
				maxFractionDigit = 1D;
				minValue = 0D;
				maxValue = (double) (24 * 60); //in minutes
				digitFormat = 0;
				break;
			case StudyMetaDataConstants.QUESTION_HEIGHT:
				maxFractionDigit = 1D;
				minValue = 0D;
				maxValue = 300D;
				digitFormat = 0;
				break;
			default:
				break;
			}

			prerequisitesMap.put("minValue", minValue);
			prerequisitesMap.put("maxValue", maxValue);
			prerequisitesMap.put("maxFractionDigit", maxFractionDigit);
			prerequisitesMap.put("digitFormat", digitFormat);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - conditionalBranchingPrerequisites() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - conditionalBranchingPrerequisites() :: Ends");
		return prerequisitesMap;
	}


	/**
	 * This method is used to get the operator from the conditional formula to solve for 'X'
	 * 
	 * @author Mohan
	 * @param conditionFormula
	 * @return String
	 * @throws DAOException
	 */
	public String getOperatorFromConditionalFormula(String conditionFormula) throws DAOException {
		LOGGER.info("INFO: ActivityMetaDataDao - getOperatorFromConditionalFormula() :: Starts");
		String operator = "";
		try {
			if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)){
				operator = StudyMetaDataConstants.CBO_OPERATOR_EQUAL;
			}else if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)){
				operator = StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL;
			}else if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN)){
				operator = StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN;
			}else if(conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN)){
				operator = StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN;
			}
		} catch (Exception e) {
			LOGGER.error("ActivityMetaDataDao - getOperatorFromConditionalFormula() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getOperatorFromConditionalFormula() :: Ends");
		return operator;
	}

	/**
	 * This method is used to get the conditional branching format for valueOfX, true condition and false condition
	 * 
	 * @author Mohan
	 * @param destinationsList
	 * @param valueOfX
	 * @param trueOperator
	 * @param falseOperator
	 * @return List<DestinationBean>
	 * @throws DAOException
	 */
	public List<DestinationBean> getConditionalBranchingFormat(List<DestinationBean> destinationsList, String valueOfX, String trueOperator, String falseOperator) throws DAOException {
		LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingFormat() :: Starts");
		try {
			if(destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2){
				destinationsList.get(0).setCondition(valueOfX);
				destinationsList.get(0).setOperator(trueOperator);

				destinationsList.get(1).setCondition(valueOfX);
				destinationsList.get(1).setOperator(falseOperator);
			}
		} catch (Exception e) {
			LOGGER.error("ActivityMetaDataDao - getConditionalBranchingFormat() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getConditionalBranchingFormat() :: Ends");
		return destinationsList;
	}
	
	/**
	 * This method is used to format the valueOfX based on the responseType and responseSubType
	 * 
	 * @author Mohan
	 * @param destinationsList
	 * @param questionBean
	 * @return List<DestinationBean>
	 * @throws DAOException
	 */
	public List<DestinationBean> formatValueOfX(List<DestinationBean> destinationsList, QuestionnaireActivityStepsBean questionBean) throws DAOException {
		LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfX() :: Starts");
		List<DestinationBean> updatedDestinationsList = destinationsList;
		try {
			if(destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2){
				if(questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE)){
					switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
					case 0:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
						break;
					case 1:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.1f", questionBean);
						break;
					case 2:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.2f", questionBean);
						break;
					case 3:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.3f", questionBean);
						break;
					case 4:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
						break;
					default:
						break;
					}
				}else if(questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_NUMERIC)){
					switch (questionBean.getFormat().get("style").toString()) {
					case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
						break;
					case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:
						updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
						break;
					default:
						break;
					}
				}else {
					updatedDestinationsList = formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ActivityMetaDataDao - formatValueOfX() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfX() :: Ends");
		return updatedDestinationsList;
	}
	
	/**
	 * This method is used to get the format for valueOfX
	 * 
	 * @author Mohan
	 * @param destinationsList
	 * @param format
	 * @param questionBean
	 * @return List<DestinationBean>
	 * @throws DAOException
	 */
	public List<DestinationBean> formatValueOfXByStringFormat(List<DestinationBean> destinationsList, String stringFormat, QuestionnaireActivityStepsBean questionBean) throws DAOException {
		LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfXByStringFormat() :: Starts");
		try {
			if(destinationsList != null && !destinationsList.isEmpty() && destinationsList.size() >= 2){
				if(questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_TIME_INTERVAL)){
					destinationsList.get(0).setCondition(String.format(stringFormat, Double.parseDouble(destinationsList.get(0).getCondition())*60));
					destinationsList.get(1).setCondition(String.format(stringFormat, Double.parseDouble(destinationsList.get(1).getCondition())*60));
				}else{
					destinationsList.get(0).setCondition(String.format(stringFormat, Double.parseDouble(destinationsList.get(0).getCondition())));
					destinationsList.get(1).setCondition(String.format(stringFormat, Double.parseDouble(destinationsList.get(1).getCondition())));
				}
			}
		} catch (Exception e) {
			LOGGER.error("ActivityMetaDataDao - formatValueOfXByStringFormat() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatValueOfXByStringFormat() :: Ends");
		return destinationsList;
	}
	
	/**
	 * This method is used to get the date range from the selection style
	 * 
	 * @author Mohan
	 * @param dateRange
	 * @return String
	 */
	public String getDateRangeType(String dateRange) {
		LOGGER.info("INFO: ActivityMetaDataDao - getDateRangeType() :: Starts");
		String dateRangeType = "";
		try{
			switch (dateRange) {
			case StudyMetaDataConstants.WCP_DATE_RANGE_UNTILL_CURRENT:
				dateRangeType = StudyMetaDataConstants.DATE_RANGE_UNTILL_CURRENT;
				break;
			case StudyMetaDataConstants.WCP_DATE_RANGE_AFTER_CURRENT:
				dateRangeType = StudyMetaDataConstants.DATE_RANGE_AFTER_CURRENT;
				break;
			case StudyMetaDataConstants.WCP_DATE_RANGE_CUSTOM:
				dateRangeType = StudyMetaDataConstants.DATE_RANGE_CUSTOM;
				break;
			default:
				break;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getDateRangeType() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getDateRangeType() :: Ends");
		return dateRangeType;
	}
}
