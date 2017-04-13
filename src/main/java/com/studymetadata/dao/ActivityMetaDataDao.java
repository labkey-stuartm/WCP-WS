package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.bean.ActivitiesBean;
import com.studymetadata.bean.ActivityFrequencyBean;
import com.studymetadata.bean.ActivityFrequencyScheduleBean;
import com.studymetadata.bean.ActivityMetaDataResponse;
import com.studymetadata.bean.ActivityMetadataBean;
import com.studymetadata.bean.ActivityResponse;
import com.studymetadata.bean.ActivityStepsBean;
import com.studymetadata.bean.DestinationBean;
import com.studymetadata.bean.appendix.ActivityStructureBean;
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
import com.studymetadata.exception.DAOException;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class ActivityMetaDataDao {
	private static final Logger LOGGER = Logger.getLogger(ActivityMetaDataDao.class);

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
	 * @param studyId
	 * @return ActivityResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ActivityResponse studyActivityList(String studyId) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - studyActivityList() :: Starts");
		ActivityResponse activityResponse = new ActivityResponse();
		List<ActiveTaskDto> activeTaskDtoList = null;
		List<QuestionnairesDto> questionnairesList = null;
		List<ActivitiesBean> activitiesBeanList = new ArrayList<>();
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();

			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//get the Activities (type : Active Task list) by studyId
				query = session.getNamedQuery("activeTaskByStudyId").setInteger("studyId", actualStudyId);
				activeTaskDtoList = query.list();
				if( null != activeTaskDtoList && !activeTaskDtoList.isEmpty()){
					for(ActiveTaskDto activeTaskDto : activeTaskDtoList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(activeTaskDto.getTaskTitle())?"":activeTaskDto.getTaskTitle());
						activityBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);

						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						frequencyDetails = getFrequencyRunsDetailsForActiveTasks(activeTaskDto, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(activeTaskDto.getFrequency())?"":activeTaskDto.getFrequency());
						activityBean.setFrequency(frequencyDetails);

						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
						activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getActiveTaskLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
						activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getActiveTaskLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
						
						activitiesBeanList.add(activityBean);
					}
				}

				//get the Activities (type : Questionaires list) by studyId
				query = session.createQuery("from QuestionnairesDto QDTO where QDTO.studyId="+actualStudyId+" and QDTO.active=true");
				questionnairesList = query.list();
				if( questionnairesList != null && !questionnairesList.isEmpty()){
					for(QuestionnairesDto questionaire : questionnairesList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(questionaire.getTitle())?"":questionaire.getTitle());
						activityBean.setType(StudyMetaDataConstants.TYPE_QUESTIONNAIRE);

						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						frequencyDetails = getFrequencyRunsDetailsForQuestionaires(questionaire, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(questionaire.getFrequency())?"":questionaire.getFrequency());
						activityBean.setFrequency(frequencyDetails);

						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionaire.getId());
						activityBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
						activityBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
						
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
	 * @return ActivityResponse
	 * @throws DAOException
	 */
	public ActivityMetaDataResponse studyActivityMetadata(String studyId, String activityId, String activityVersion) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - studyActivityMetadata() :: Starts");
		ActivityMetaDataResponse activityMetaDataResponse = new ActivityMetaDataResponse();
		ActivityStructureBean activityStructureBean = new ActivityStructureBean();
		Integer actualStudyId = null;
		String[] activityInfoArray = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//check whether the activityId is valid or not (i.e. delimeter '-')
				if(activityId.contains("-")){
					activityInfoArray = activityId.split("-");
					if(activityInfoArray[0].equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)){
						activityStructureBean = activeTaskMetadata(studyId, activityInfoArray[1], session);
					}else{
						activityStructureBean = questionnaireMetadata(studyId, activityInfoArray[1], session);
					}
					activityMetaDataResponse.setActivity(activityStructureBean);
					activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
				}
			}else{
				activityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - studyActivityMetadata() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: ActivityMetaDataDao - studyActivityMetadata() :: Ends");
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
	public ActivityStructureBean activeTaskMetadata(String studyId, String activityId, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Starts");
		ActivityStructureBean activityStructureBean = new ActivityStructureBean();
		ActiveTaskDto activeTaskDto = null;
		List<ActivityStepsBean> steps = new ArrayList<>();
		try{
			query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.id ="+activityId);
			activeTaskDto = (ActiveTaskDto) query.uniqueResult();
			if( activeTaskDto != null){
				List<Integer> taskMasterAttrIdList = new ArrayList<>();
				List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList;
				List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;
				List<ActiveTaskListDto> activeTaskList = null;

				activityStructureBean.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);

				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
				
				metadata.setEndDate(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getActiveTaskLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				metadata.setLastModified(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ")); //column not there in the database
				metadata.setName(StringUtils.isEmpty(activeTaskDto.getDisplayName())?"":activeTaskDto.getDisplayName());
				metadata.setStartDate(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getActiveTaskLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				metadata.setStudyId(studyId);
				metadata.setVersion(activeTaskDto.getStudyVersion() == null?"1":activeTaskDto.getStudyVersion().toString());
				activityStructureBean.setMetadata(metadata);

				//get the active task attribute values based on the activityId
				query = session.createQuery(" from ActiveTaskAttrtibutesValuesDto ATAVDTO where ATAVDTO.activeTaskId="+activeTaskDto.getId());
				activeTaskAttrtibuteValuesList = query.list();
				if( activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty()){
					for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
						taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
					}

					if(taskMasterAttrIdList != null && !taskMasterAttrIdList.isEmpty()){
						//get the active task master info based on the active task attribute ids
						query = session.createQuery(" from ActiveTaskMasterAttributeDto ATMADTO where ATMADTO.masterId in ("+StringUtils.join(taskMasterAttrIdList, ",")+")");
						activeTaskMaterList = query.list();
					}

					//get the active task list details
					query = session.createQuery(" from ActiveTaskListDto ATDTO ");
					activeTaskList = query.list();
				}

				//check the active task details are exists or not
				Boolean attributeListFlag = activeTaskAttrtibuteValuesList != null && !activeTaskAttrtibuteValuesList.isEmpty();
				Boolean masterAttributeListFlag = activeTaskMaterList != null && !activeTaskMaterList.isEmpty();
				Boolean taskListFlag = activeTaskList != null && !activeTaskList.isEmpty();
				if(attributeListFlag && masterAttributeListFlag && taskListFlag){
					//get the steps details based on the activity type
					for(ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList){
						for(ActiveTaskMasterAttributeDto masterAttributeDto : activeTaskMaterList){
							if(attributeDto.getActiveTaskMasterAttrId().intValue() == masterAttributeDto.getMasterId().intValue()){
								for(ActiveTaskListDto taskDto : activeTaskList){
									if(taskDto.getActiveTaskListId().intValue() == masterAttributeDto.getTaskTypeId().intValue()){
										ActivityStepsBean activeTaskStep = new ActivityStepsBean();
										activeTaskStep.setType(StudyMetaDataConstants.TYPE_ACTIVE_TASK);
										activeTaskStep.setResultType(StringUtils.isEmpty(taskDto.getType())?"":taskDto.getType());
										activeTaskStep.setKey(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
										activeTaskStep.setText(StringUtils.isEmpty(masterAttributeDto.getDisplayName())?"":masterAttributeDto.getDisplayName());
										activeTaskStep.setOptions(activeTaskOptions()); //activeTask options list
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
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - activeTaskMetadata() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Ends");
		return activityStructureBean;
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
	public ActivityStructureBean questionnaireMetadata(String studyId, String activityId, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - questionnaireMetadata() :: Starts");
		ActivityStructureBean activityStructureBean = new ActivityStructureBean();
		Map<String, Integer> sequenceNoMap = new HashMap<>();
		Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap = new HashMap<>();
		TreeMap<Integer, ActivityStepsBean> stepsSequenceTreeMap = new TreeMap<>();
		QuestionnairesDto questionnaireDto = null;
		List<QuestionnairesStepsDto> questionaireStepsList = null;
		List<ActivityStepsBean> steps = new ArrayList<>();
		List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList = null;
		try{
			query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id="+activityId+" and QDTO.active=true");
			questionnaireDto = (QuestionnairesDto) query.uniqueResult();
			if(questionnaireDto != null){
				activityStructureBean.setType(StudyMetaDataConstants.TYPE_QUESTIONNAIRE);
	
				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId());
				
				metadata.setEndDate(StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getStudyLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				metadata.setLastModified(StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
				metadata.setName(StringUtils.isEmpty(questionnaireDto.getTitle())?"":questionnaireDto.getTitle());
				metadata.setStartDate(StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getStudyLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				metadata.setStudyId(studyId);
				metadata.setVersion(questionnaireDto.getStudyVersion() == null?"1":questionnaireDto.getStudyVersion().toString());
				activityStructureBean.setMetadata(metadata);
	
				query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" and QSDTO.active=true ORDER BY QSDTO.sequenceNo");
				questionaireStepsList = query.list();
				if(questionaireStepsList != null && !questionaireStepsList.isEmpty()){
					List<Integer> instructionIdList = new ArrayList<>();
					List<Integer> questionIdList = new ArrayList<>();
					List<Integer> formIdList = new ArrayList<>();
					for(QuestionnairesStepsDto questionaireSteps : questionaireStepsList){
						switch (questionaireSteps.getStepType()) {
							case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION: instructionIdList.add(questionaireSteps.getInstructionFormId());
								sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, questionaireSteps.getSequenceNo());
								questionnaireStepDetailsMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, questionaireSteps);
								break;
							case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION: questionIdList.add(questionaireSteps.getInstructionFormId());
								sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, questionaireSteps.getSequenceNo());
								questionnaireStepDetailsMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, questionaireSteps);
								break;
							case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM: formIdList.add(questionaireSteps.getInstructionFormId());
								sequenceNoMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, questionaireSteps.getSequenceNo());
								questionnaireStepDetailsMap.put(String.valueOf(questionaireSteps.getInstructionFormId())+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, questionaireSteps);
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
						query = session.createQuery(" from InstructionsDto IDTO where IDTO.id in ("+StringUtils.join(instructionIdList, ",")+") and IDTO.active=true");
						instructionsDtoList = query.list();
						if(instructionsDtoList != null && !instructionsDtoList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, instructionsDtoList, null, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, null);
						}
					}
	
					//get the questionaire List
					if(!questionIdList.isEmpty()){
						List<QuestionsDto> questionsList;
						query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdList, ",")+") and QDTO.active=true");
						questionsList = query.list();
						if( questionsList != null && !questionsList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, null, questionsList, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList);
						}
					}
	
					//get the forms list
					if(!formIdList.isEmpty()){
						for(Integer formId : formIdList){
							List<FormMappingDto> formList;
							query = session.createQuery(" from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId from FormDto FDTO where FDTO.formId="+formId+" and FDTO.active=true) ORDER BY FMDTO.sequenceNo ");
							formList = query.list();
							if(formList != null && !formList.isEmpty()){
								stepsSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM, null, null, formList, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList);
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
				case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
					runDetailsBean = getActiveTaskFrequencyDetailsForOneTime(activeTask, runDetailsBean);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					runDetailsBean = getActiveTaskFrequencyDetailsForDaily(activeTask, runDetailsBean, session);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					//runDetailsBean = getActiveTaskFrequencyDetailsForWeekly(activeTask, runDetailsBean);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
					//runDetailsBean = getActiveTaskFrequencyDetailsForMonthly(activeTask, runDetailsBean);
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
				oneTimeBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTask.getActiveTaskLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				oneTimeBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTask.getActiveTaskLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
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
				query = session.createQuery(" from ActiveTaskFrequencyDto ATFDTO where ATFDTO.activeTaskId="+activeTask.getId());
				activeTaskDailyFrequencyList = query.list();
				if(activeTaskDailyFrequencyList != null && !activeTaskDailyFrequencyList.isEmpty()){
					for(ActiveTaskFrequencyDto activeTaskFrequency : activeTaskDailyFrequencyList){
						ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
						String activeTaskStartDate = activeTaskFrequency.getFrequencyDate()+" "+activeTaskFrequency.getFrequencyTime();
						String activeTaskEndDate = StudyMetaDataUtil.addDaysToDate(activeTaskFrequency.getFrequencyDate(), 1)+" "+activeTaskFrequency.getFrequencyTime();
						dailyBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskStartDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
						dailyBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskEndDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
						runDetailsBean.add(dailyBean);
					}
					
					/*Integer repeatCount = (activeTask.getRepeatActiveTask() == null||activeTask.getRepeatActiveTask() == 0)?1:activeTask.getRepeatActiveTask();
					String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
					while(repeatCount > 0){
						String activeTaskEndDate;
						String dayEndDate;
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
								runDetailsBean.add(dailyBean);
							}

							//skip the loop if the enddate of activeTask is before the month end date
							if(skipLoop){
								break;
							}
						}

						activeTaskStartDate = dayEndDate;
						repeatCount--;
					}*/
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
					String startDate = customFrequencyDto.getFrequencyStartDate()+" "+customFrequencyDto.getFrequencyTime()+":00";
					String endDate = customFrequencyDto.getFrequencyEndDate()+" "+customFrequencyDto.getFrequencyTime()+":00";
					manuallyScheduleBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(startDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
					manuallyScheduleBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(endDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
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
				case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
					runDetailsBean = getQuestionnaireFrequencyDetailsForOneTime(questionaire, runDetailsBean);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
					runDetailsBean = getQuestionnaireFrequencyDetailsForDaily(questionaire, runDetailsBean, session);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY:
					//runDetailsBean = getQuestionnaireFrequencyDetailsForWeekly(questionaire, runDetailsBean);
					break;
				case StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY:
					//runDetailsBean = getQuestionnaireFrequencyDetailsForMonthly(questionaire, runDetailsBean);
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
				oneTimeBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeStart(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
				oneTimeBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getStudyLifetimeEnd(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
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
				query = session.createQuery(" from QuestionnairesFrequenciesDto QFDTO where QFDTO.questionnairesId="+questionaire.getId());
				dailyFrequencyList = query.list();
				if(dailyFrequencyList != null && !dailyFrequencyList.isEmpty()){
					for(QuestionnairesFrequenciesDto questionnaireFrequencyDto : dailyFrequencyList){
						ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
						String activeTaskStartDate = questionnaireFrequencyDto.getFrequencyDate()+" "+questionnaireFrequencyDto.getFrequencyTime();
						String activeTaskEndDate = StudyMetaDataUtil.addDaysToDate(questionnaireFrequencyDto.getFrequencyDate(), 1)+" "+questionnaireFrequencyDto.getFrequencyTime();
						dailyBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskStartDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
						dailyBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskEndDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'hh:mm:ssZ"));
						runDetailsBean.add(dailyBean);
					}
					
					//old approach was replaced
					/*Integer repeatCount = (questionaire.getRepeatQuestionnaire() == null||questionaire.getRepeatQuestionnaire() == 0)?1:questionaire.getRepeatQuestionnaire();
					String questionaireStartDate = questionaire.getStudyLifetimeStart();
					while(repeatCount > 0){
						String questionaireEndDate;
						String dayEndDate;
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
								runDetailsBean.add(dailyBean);
							}
							//skip the loop if the enddate of questionaire is before the month end date
							if(skipLoop){
								break;
							}
						}

						questionaireStartDate = dayEndDate;
						repeatCount--;
					}*/
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
					manuallyScheduleBean.setEndTime(StudyMetaDataUtil.getFormattedDateTimeZone(customFrequencyDto.getFrequencyEndDate(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
					manuallyScheduleBean.setStartTime(StudyMetaDataUtil.getFormattedDateTimeZone(customFrequencyDto.getFrequencyStartDate(), "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssZ"));
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
	public SortedMap<Integer, ActivityStepsBean> getStepsInfoForQuestionnaires(String type, List<InstructionsDto> instructionsDtoList, List<QuestionsDto> questionsDtoList, List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, ActivityStepsBean> stepsSequenceTreeMap, Session session, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: Starts");
		TreeMap<Integer, ActivityStepsBean> stepsOrderSequenceTreeMap = new TreeMap<>();
		try{
			switch (type) {
				case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
					stepsOrderSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getInstructionDetailsForQuestionnaire(instructionsDtoList, sequenceNoMap, stepsSequenceTreeMap, questionnaireStepDetailsMap);
					break;
				case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
					stepsOrderSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getQuestionDetailsForQuestionnaire(questionsDtoList, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList);
					break;
				case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
					stepsOrderSequenceTreeMap = (TreeMap<Integer, ActivityStepsBean>) getFormDetailsForQuestionnaire(formsList, sequenceNoMap, session, stepsSequenceTreeMap, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList);
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
	public SortedMap<Integer, ActivityStepsBean> getInstructionDetailsForQuestionnaire(List<InstructionsDto> instructionsDtoList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, ActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: Starts");
		try{
			if( instructionsDtoList != null && !instructionsDtoList.isEmpty()){
				for(InstructionsDto instructionsDto : instructionsDtoList){
					QuestionnairesStepsDto instructionStepDetails = questionnaireStepDetailsMap.get((instructionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION).toString());
					ActivityStepsBean instructionBean = new ActivityStepsBean();

					instructionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
					instructionBean.setResultType("");
					instructionBean.setKey(instructionStepDetails.getInstructionFormId().toString());
					instructionBean.setTitle(StringUtils.isEmpty(instructionsDto.getInstructionTitle())?"":instructionsDto.getInstructionTitle());
					instructionBean.setText(StringUtils.isEmpty(instructionsDto.getInstructionText())?"":instructionsDto.getInstructionText());
					instructionBean.setSkippable((instructionStepDetails.getSkiappable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(instructionStepDetails.getSkiappable()))?false:true);
					instructionBean.setGroupName("");
					instructionBean.setRepeatable((instructionStepDetails.getRepeatable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(instructionStepDetails.getRepeatable()))?false:true);
					instructionBean.setRepeatableText(instructionStepDetails.getRepeatableText() == null?"":instructionStepDetails.getRepeatableText());

					List<DestinationBean> destinations = new ArrayList<>();
					DestinationBean dest = new DestinationBean();
					dest.setCondition("");
					dest.setDestination((instructionStepDetails.getDestinationStep()==null || instructionStepDetails.getDestinationStep().intValue()==0)?"":instructionStepDetails.getDestinationStep().toString());
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
	public SortedMap<Integer, ActivityStepsBean> getQuestionDetailsForQuestionnaire(List<QuestionsDto> questionsDtoList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, ActivityStepsBean> stepsSequenceTreeMap, Session session, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: Starts");
		List<QuestionResponseSubTypeDto> destinationConditionList = null;
		try{
			if(questionsDtoList != null && !questionsDtoList.isEmpty()){
				for(QuestionsDto questionsDto : questionsDtoList){
					QuestionnairesStepsDto questionStepDetails = questionnaireStepDetailsMap.get((questionsDto.getId()+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION).toString());
					ActivityStepsBean questionBean = new ActivityStepsBean();

					questionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
					if(questionsDto.getResponseType() != null){
						for(QuestionResponsetypeMasterInfoDto masterInfo : questionResponseTypeMasterInfoList){
							if(masterInfo.getId().intValue() == questionsDto.getResponseType().intValue()){
								questionBean.setResultType(masterInfo.getResponseTypeCode());
								questionBean.setFormat(getQuestionaireQuestionFormatByType(questionsDto, masterInfo.getResponseTypeCode(), session));
								break;
							}
						}
					}else{
						questionBean.setResultType(""); //NA
					}
					questionBean.setText("");
					questionBean.setKey(questionStepDetails.getInstructionFormId().toString());
					questionBean.setTitle(questionsDto.getQuestion() == null?"":questionsDto.getQuestion());
					questionBean.setSkippable((questionStepDetails.getSkiappable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(questionStepDetails.getSkiappable()))?false:true); //NA
					questionBean.setGroupName(""); //NA
					questionBean.setRepeatable((questionStepDetails.getRepeatable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(questionStepDetails.getRepeatable()))?false:true); //NA
					questionBean.setRepeatableText(questionStepDetails.getRepeatableText() == null?"":questionStepDetails.getRepeatableText()); //NA
					
					//destination logic based on the branching
					List<DestinationBean> destinationsList = new ArrayList<>();
					query = session.createQuery("from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionsDto.getId());
					destinationConditionList = query.list();
					if(destinationConditionList != null && !destinationConditionList.isEmpty()){
						for(QuestionResponseSubTypeDto destinationDto : destinationConditionList){
							DestinationBean destination = new DestinationBean();
							destination.setCondition(StringUtils.isEmpty(destinationDto.getValue())?"":destinationDto.getValue());
							destination.setDestination((destinationDto.getDestinationStepId() == null || destinationDto.getDestinationStepId().intValue() == 0)?"":destinationDto.getDestinationStepId().toString());
							destinationsList.add(destination);
						}
					}else{
						DestinationBean destinationBean = new DestinationBean();
						destinationBean.setCondition("");
						destinationBean.setDestination((questionStepDetails.getDestinationStep()==null || questionStepDetails.getDestinationStep().intValue()==0)?"":questionStepDetails.getDestinationStep().toString());
						destinationsList.add(destinationBean);
					}
					questionBean.setDestinations(destinationsList);
					questionBean.setHealthDataKey("");

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
	public SortedMap<Integer, ActivityStepsBean> getFormDetailsForQuestionnaire(List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, Session session, SortedMap<Integer, ActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList) throws DAOException{
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
					ActivityStepsBean formBean = new ActivityStepsBean();
					List<ActivityStepsBean> formSteps = new ArrayList<>();
					HashMap<Integer, ActivityStepsBean> formStepsMap = new HashMap<>();
					
					formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
					formBean.setResultType("grouped");
					formBean.setKey(formStepDetails.getInstructionFormId().toString());
					formBean.setTitle("");
					formBean.setText("");
					formBean.setSkippable((formStepDetails.getSkiappable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(formStepDetails.getSkiappable()))?false:true);
					formBean.setGroupName("");
					formBean.setRepeatable((formStepDetails.getRepeatable() == null || StudyMetaDataConstants.NO.equalsIgnoreCase(formStepDetails.getRepeatable()))?false:true);
					formBean.setRepeatableText(formStepDetails.getRepeatableText() == null?"":formStepDetails.getRepeatableText());

					
					List<DestinationBean> destinations = new ArrayList<>();
					DestinationBean dest = new DestinationBean();
					dest.setCondition("");
					dest.setDestination((formStepDetails.getDestinationStep()==null ||formStepDetails.getDestinationStep().intValue()==0)?"":formStepDetails.getDestinationStep().toString());
					destinations.add(dest);
					formBean.setDestinations(destinations);
					List<QuestionsDto> formQuestionsList;
					query = session.createQuery("from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(formQuestionIdsList, ',')+")");
					formQuestionsList = query.list();
					if(formQuestionsList != null && !formQuestionsList.isEmpty()){
						for(QuestionsDto formQuestionDto : formQuestionsList){
							ActivityStepsBean formQuestionBean = new ActivityStepsBean();
							formQuestionBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
							if(formQuestionDto.getResponseType() != null){
								for(QuestionResponsetypeMasterInfoDto masterInfo : questionResponseTypeMasterInfoList){
									if(masterInfo.getId().intValue() == formQuestionDto.getResponseType().intValue()){
										formQuestionBean.setResultType(masterInfo.getResponseTypeCode());
										formQuestionBean.setFormat(getQuestionaireQuestionFormatByType(formQuestionDto, masterInfo.getResponseTypeCode(), session));
										break;
									}
								}
							}else{
								formQuestionBean.setResultType(""); //NA
							}
							formQuestionBean.setKey(formQuestionDto.getId().toString());
							formQuestionBean.setTitle(StringUtils.isEmpty(formQuestionDto.getQuestion())?"":formQuestionDto.getQuestion());
							formQuestionBean.setSkippable(false); //NA
							formQuestionBean.setGroupName(""); //NA
							formQuestionBean.setRepeatable(false); //NA
							formQuestionBean.setRepeatableText(""); //NA

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
	 */
	public Map<String, Object> getActiveTaskStepFormatByType(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue, String taskType) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getActiveTaskStepFormatByType() :: Starts");
		Map<String, Object> activeTaskFormat = new HashMap<>();
		try{
			if(StringUtils.isNotEmpty(taskType)){
				switch (taskType) {
				case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
					if(StringUtils.isNotEmpty(attributeValues.getAttributeVal())){
						StringTokenizer tokenizer = new StringTokenizer(attributeValues.getAttributeVal(), ":");
						activeTaskFormat.put("duration", tokenizer.nextToken()+"."+tokenizer.nextToken()); //in hours
					}else{
						activeTaskFormat.put("duration", "0.0"); //in hours
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
	}
	
	/**
	 * @author Mohan
	 * @param questionDto
	 * @return Map<String, Object>
	 * @throws DAOException
	 */
	public Map<String, Object> spatialSpanMemoryDetails(ActiveTaskAttrtibutesValuesDto attributeValues, ActiveTaskMasterAttributeDto masterAttributeValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Starts");
		Map<String, Object> activeTaskFormat = new HashMap<>();
		try{
			activeTaskFormat.put("initialSpan", 0);
			activeTaskFormat.put("minimumSpan", 0);
			activeTaskFormat.put("maximumSpan", 0);
			activeTaskFormat.put("playSpeed", 0);
			activeTaskFormat.put("maximumTests", 0);
			activeTaskFormat.put("maximumConsecutiveFailures", 0);
			activeTaskFormat.put("customTargetImage", "");
			activeTaskFormat.put("customTargetPluralName", "");
			activeTaskFormat.put("requireReversal", false);
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - spatialSpanMemoryDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - spatialSpanMemoryDetails() :: Ends");
		return activeTaskFormat;
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
		Map<String, Object> questionFormat = new HashMap<>();
		QuestionReponseTypeDto reponseType = null;
		try{
			if(StringUtils.isNotEmpty(questionResultType)){
				query = session.createQuery(" from QuestionReponseTypeDto QRTDTO where QRTDTO.questionsResponseTypeId="+questionDto.getId());
				reponseType = (QuestionReponseTypeDto) query.uniqueResult();
				switch (questionResultType) {
					case StudyMetaDataConstants.QUESTION_SCALE:
						questionFormat = formatQuestionScaleDetails(questionDto, reponseType);
						break;
					case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
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
					case StudyMetaDataConstants.QUESTION_NUMERIC:
						questionFormat = formatQuestionNumericDetails(questionDto, reponseType);
						break;
					case StudyMetaDataConstants.QUESTION_DATE:
						questionFormat = formatQuestionDateDetails(questionDto, reponseType);
						break;
					case StudyMetaDataConstants.QUESTION_TEXT: 
						questionFormat = formatQuestionTextDetails(questionDto, reponseType);
						break;
					case StudyMetaDataConstants.QUESTION_EMAIL:
						questionFormat.put("placeholder", "");
						break;
					case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:  
						questionFormat.put("default", 0);
						questionFormat.put("step", 0); //In minutes 1-30
						break;
					case StudyMetaDataConstants.QUESTION_HEIGHT:  
						questionFormat.put("measurementSystem", ""); //Local/Metric/US
						questionFormat.put("placeholder", "");
						break;
					case StudyMetaDataConstants.QUESTION_LOCATION:
						questionFormat.put("useCurrentLocation", false);
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
		Map<String, Object> questionFormat = new HashMap<>();
		try{
			questionFormat.put("maxValue", (reponseType==null || reponseType.getMaxValue()==null)?0:reponseType.getMaxValue());
			questionFormat.put("minValue", (reponseType==null || reponseType.getMinValue()==null)?0:reponseType.getMinValue());
			questionFormat.put("default", (reponseType==null || reponseType.getMinValue()==null)?0:reponseType.getMinValue());
			questionFormat.put("step", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
			questionFormat.put("vertical", (reponseType==null || !reponseType.getVertical())?false:true);
			questionFormat.put("maxDesc", (reponseType==null || reponseType.getMaxDescription()==null)?"":reponseType.getMaxDescription());
			questionFormat.put("minDesc", (reponseType==null || reponseType.getMinDescription()==null)?"":reponseType.getMinDescription());
			questionFormat.put("maxImage", (reponseType==null || reponseType.getMaxImage()==null)?"":reponseType.getMaxImage());
			questionFormat.put("minImage", (reponseType==null || reponseType.getMinImage()==null)?"":reponseType.getMinImage());
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
		Map<String, Object> questionFormat = new HashMap<>();
		try{
			questionFormat.put("maxValue", (reponseType==null || reponseType.getMaxValue()==null)?0:reponseType.getMaxValue());
			questionFormat.put("minValue", (reponseType==null || reponseType.getMinValue()==null)?0:reponseType.getMinValue());
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
			questionFormat.put("maxFractionDigits", (reponseType==null || reponseType.getMaxFractionDigits()==null)?0:reponseType.getMaxFractionDigits());
			questionFormat.put("vertical", (reponseType==null || !reponseType.getVertical())?false:true);
			questionFormat.put("maxDesc", (reponseType==null || reponseType.getMaxDescription()==null)?"":reponseType.getMaxDescription());
			questionFormat.put("minDesc", (reponseType==null || reponseType.getMinDescription()==null)?"":reponseType.getMinDescription());
			questionFormat.put("maxImage", (reponseType==null || reponseType.getMaxImage()==null)?"":reponseType.getMaxImage());
			questionFormat.put("minImage", (reponseType==null || reponseType.getMinImage()==null)?"":reponseType.getMinImage());
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
		Map<String, Object> questionFormat = new HashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<HashMap<String, Object>> textChoicesList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					HashMap<String, Object> textScaleMap = new HashMap<>();
					textScaleMap.put("text", subType.getText()==null?"":subType.getText());
					textScaleMap.put("value", subType.getValue()==null?"":subType.getValue());
					textScaleMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					textScaleMap.put("exclusive", !subType.getExclusive()?false:true);
					textChoicesList.add(textScaleMap);
				}
			}
			questionFormat.put("textChoices", textChoicesList);
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
			questionFormat.put("vertical", (reponseType==null || !reponseType.getVertical())?false:true);
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
		Map<String, Object> questionFormat = new HashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<HashMap<String, Object>> valuePickerList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					HashMap<String, Object> valuePickerMap = new HashMap<>();
					valuePickerMap.put("text", subType.getText()==null?"":subType.getText());
					valuePickerMap.put("value", subType.getValue()==null?"":subType.getValue());
					valuePickerMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					valuePickerMap.put("exclusive", !subType.getExclusive()?false:true);
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
		Map<String, Object> questionFormat = new HashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<HashMap<String, Object>> imageChoicesList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					HashMap<String, Object> imageChoiceMap = new HashMap<>();
					imageChoiceMap.put("image", subType.getImage()==null?"":subType.getImage());
					imageChoiceMap.put("selectedImage", subType.getSelectedImage()==null?"":subType.getSelectedImage());
					imageChoiceMap.put("text", subType.getText()==null?"":subType.getText());
					imageChoiceMap.put("value", subType.getValue()==null?"":subType.getValue());
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
		Map<String, Object> questionFormat = new HashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<HashMap<String, Object>> textChoiceMapList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					HashMap<String, Object> textChoiceMap = new HashMap<>();
					textChoiceMap.put("text", subType.getText()==null?"":subType.getText());
					textChoiceMap.put("value", subType.getValue()==null?"":subType.getValue());
					textChoiceMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					textChoiceMap.put("exclusive", !subType.getExclusive()?false:true);
					textChoiceMapList.add(textChoiceMap);
				}
			}
			questionFormat.put("textChoices", textChoiceMapList);
			questionFormat.put("selectionStyle", (reponseType==null || reponseType.getSelectionStyle()==null)?"":reponseType.getSelectionStyle()); //Single/Multiple
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
		Map<String, Object> questionFormat = new HashMap<>();
		try{
			questionFormat.put("style", (reponseType==null || reponseType.getStyle()==null)?"":reponseType.getStyle());
			questionFormat.put("unit", (reponseType==null || reponseType.getUnit()==null)?"":reponseType.getUnit());
			questionFormat.put("minValue", (reponseType==null || reponseType.getMinValue()==null)?0:reponseType.getMinValue());
			questionFormat.put("maxValue", (reponseType==null || reponseType.getMaxValue()==null)?0:reponseType.getMaxValue());
			questionFormat.put("placeholder", (reponseType==null || reponseType.getPlaceholder()==null)?0:reponseType.getPlaceholder());
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
		Map<String, Object> questionFormat = new HashMap<>();
		try{
			questionFormat.put("style", (reponseType==null || reponseType.getStyle()==null)?"":reponseType.getStyle()); //Date/Date-Time
			questionFormat.put("minDate", (reponseType==null || reponseType.getMinDate()==null)?"":reponseType.getMinDate()); //yyyy-MM-dd'T'HH:mm:ss.SSSZ
			questionFormat.put("maxDate", (reponseType==null || reponseType.getMaxDate()==null)?"":reponseType.getMaxDate()); //yyyy-MM-dd'T'HH:mm:ss.SSSZ
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?"":reponseType.getDefaultValue()); //Date
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
		Map<String, Object> questionFormat = new HashMap<>();
		try{
			questionFormat.put("maxLength", (reponseType==null || reponseType.getMaxLength()==null)?0:reponseType.getMaxLength());
			questionFormat.put("validationRegex", (reponseType==null || reponseType.getValidationRegex()==null)?"":reponseType.getValidationRegex());
			questionFormat.put("invalidMessage", (reponseType==null || reponseType.getInvalidMessage()==null)?"":reponseType.getInvalidMessage());
			questionFormat.put("multipleLines", (reponseType==null || !reponseType.getMultipleLines())?false:true);
			questionFormat.put("placeholder", (reponseType==null || reponseType.getPlaceholder()==null)?"":reponseType.getPlaceholder());
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - formatQuestionTextDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionTextDetails() :: Ends");
		return questionFormat;
	}
	
	/*-----------------------------Activity data methods ends----------------------------------*/
}
