package com.studymetadata.dao;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
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
import com.studymetadata.bean.QuestionnaireActivityMetaDataResponse;
import com.studymetadata.bean.QuestionnaireActivityStepsBean;
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
import com.studymetadata.exception.DAOException;
import com.studymetadata.exception.OrchestrationException;
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

			/*version related query*/
			//query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			//query =  session.getNamedQuery("getStudyVersionDetailsByCustomStudyId").setString("customStudyId", studyId);
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				//get the Activities (type : Active Task list) by studyId
				query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.action=1 and ATDTO.studyId in (select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.studyExcActiveTask='Y' and SSDTO.studyId="+actualStudyId+")");
				//query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.studyId="+actualStudyId);
				activeTaskDtoList = query.list();
				if( null != activeTaskDtoList && !activeTaskDtoList.isEmpty()){
					for(ActiveTaskDto activeTaskDto : activeTaskDtoList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(activeTaskDto.getDisplayName())?"":activeTaskDto.getDisplayName());
						activityBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);

						activityBean.setActivityVersion((activeTaskDto.getStudyVersion() == null || activeTaskDto.getStudyVersion().intValue() == 0)?"1":activeTaskDto.getStudyVersion().toString());
						activityBean.setBranching(false);
						activityBean.setLastModified(StringUtils.isEmpty(activeTaskDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
						
						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						frequencyDetails = getFrequencyRunsDetailsForActiveTasks(activeTaskDto, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(activeTaskDto.getFrequency())?"":activeTaskDto.getFrequency());
						activityBean.setFrequency(frequencyDetails);

						//get the time details for the activity by activityId
						activityBean = getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);
						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());
						activitiesBeanList.add(activityBean);
					}
				}

				//get the Activities (type : Questionaires list) by studyId
				query = session.createQuery("from QuestionnairesDto QDTO where QDTO.active=true and QDTO.status=true and QDTO.studyId in (select SSDTO.studyId from StudySequenceDto SSDTO where SSDTO.studyExcQuestionnaries='Y' and SSDTO.studyId="+actualStudyId+")");
				//query = session.createQuery("from QuestionnairesDto QDTO where QDTO.studyId="+actualStudyId+" and QDTO.active=true");
				questionnairesList = query.list();
				if( questionnairesList != null && !questionnairesList.isEmpty()){
					for(QuestionnairesDto questionaire : questionnairesList){
						ActivitiesBean activityBean = new ActivitiesBean();
						activityBean.setTitle(StringUtils.isEmpty(questionaire.getTitle())?"":questionaire.getTitle());
						activityBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);

						ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
						frequencyDetails = getFrequencyRunsDetailsForQuestionaires(questionaire, frequencyDetails, session);
						frequencyDetails.setType(StringUtils.isEmpty(questionaire.getFrequency())?"":questionaire.getFrequency());
						activityBean.setFrequency(frequencyDetails);

						activityBean.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionaire.getId());
						activityBean.setActivityVersion((questionaire.getVersion() == null || questionaire.getVersion() == 0)?"1":questionaire.getVersion().toString());
						activityBean.setBranching((questionaire.getBranching() == null || !questionaire.getBranching())?false:true);
						activityBean.setLastModified(StringUtils.isEmpty(questionaire.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(questionaire.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
						activityBean = getTimeDetailsByActivityIdForQuestionnaire(questionaire, activityBean, session);
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
		ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse = new ActiveTaskActivityMetaDataResponse();
		ActiveTaskActivityStructureBean activeTaskactivityStructureBean = new ActiveTaskActivityStructureBean();
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				activeTaskactivityStructureBean = activeTaskMetadata(studyId, activityId, session);
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
		QuestionnaireActivityMetaDataResponse activityMetaDataResponse = new QuestionnaireActivityMetaDataResponse();
		QuestionnaireActivityStructureBean activityStructureBean = new QuestionnaireActivityStructureBean();
		Integer actualStudyId = null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getStudyIdByCustomStudyId").setString("customStudyId", studyId);
			actualStudyId = (Integer) query.uniqueResult();
			if(actualStudyId != null){
				activityStructureBean = questionnaireMetadata(studyId, activityId, session);
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
	public ActiveTaskActivityStructureBean activeTaskMetadata(String studyId, String activityId, Session session) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - activeTaskMetadata() :: Starts");
		ActiveTaskActivityStructureBean activeTaskActivityStructureBean = new ActiveTaskActivityStructureBean();
		ActiveTaskDto activeTaskDto = null;
		List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();
		try{
			//query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.id ="+activityId);
			query = session.createQuery("from ActiveTaskDto ATDTO where ATDTO.id ="+activityId+" and ATDTO.action=1 ");
			activeTaskDto = (ActiveTaskDto) query.uniqueResult();
			if( activeTaskDto != null){
				List<Integer> taskMasterAttrIdList = new ArrayList<>();
				List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList;
				List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;
				List<ActiveTaskListDto> activeTaskList = null;

				activeTaskActivityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);

				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskDto.getId());

				ActivitiesBean activityBean = new ActivitiesBean();
				activityBean = getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);

				metadata.setStartDate(activityBean.getStartTime());
				metadata.setEndDate(activityBean.getEndTime());
				
				metadata.setLastModified(StringUtils.isEmpty(activeTaskDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(activeTaskDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")); //column not there in the database
				metadata.setName(StringUtils.isEmpty(activeTaskDto.getShortTitle())?"":activeTaskDto.getShortTitle());
				metadata.setStudyId(studyId);
				metadata.setVersion(activeTaskDto.getStudyVersion() == null?"1":activeTaskDto.getStudyVersion().toString());
				activeTaskActivityStructureBean.setMetadata(metadata);

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
									if(taskDto.getActiveTaskListId().intValue() == masterAttributeDto.getTaskTypeId().intValue() && masterAttributeDto.getAttributeType().equalsIgnoreCase(StudyMetaDataConstants.ACTIVE_TASK_ATTRIBUTE_TYPE_CONFIGURE)){
										ActiveTaskActivityStepsBean activeTaskActiveTaskStep = new ActiveTaskActivityStepsBean();
										activeTaskActiveTaskStep.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
										activeTaskActiveTaskStep.setResultType(StringUtils.isEmpty(taskDto.getType())?"":taskDto.getType());
										activeTaskActiveTaskStep.setKey(activeTaskDto.getShortTitle());
										activeTaskActiveTaskStep.setText(StringUtils.isEmpty(activeTaskDto.getInstruction())?"":activeTaskDto.getInstruction());
										//activeTaskActiveTaskStep.setOptions(activeTaskOptions()); //activeTask options list
										activeTaskActiveTaskStep.setFormat(getActiveTaskStepFormatByType(attributeDto, masterAttributeDto, taskDto.getType()));
										steps.add(activeTaskActiveTaskStep);
									}
								}
							}
						}
						
					}
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
	public QuestionnaireActivityStructureBean questionnaireMetadata(String studyId, String activityId, Session session) throws DAOException{
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
			query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id="+activityId+" and QDTO.active=true and QDTO.status=true");
			//query = session.createQuery("from QuestionnairesDto QDTO where QDTO.id="+activityId+" and QDTO.active=true");
			questionnaireDto = (QuestionnairesDto) query.uniqueResult();
			if(questionnaireDto != null){
				activityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);

				ActivityMetadataBean metadata = new ActivityMetadataBean();
				metadata.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId());

				ActivitiesBean activityBean = new ActivitiesBean();
				activityBean = getTimeDetailsByActivityIdForQuestionnaire(questionnaireDto, activityBean, session);

				metadata.setStartDate(activityBean.getStartTime());
				metadata.setEndDate(activityBean.getEndTime());
				metadata.setLastModified(StringUtils.isEmpty(questionnaireDto.getModifiedDate())?"":StudyMetaDataUtil.getFormattedDateTimeZone(questionnaireDto.getModifiedDate(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				metadata.setName(StringUtils.isEmpty(questionnaireDto.getShortTitle())?"":questionnaireDto.getShortTitle());
				metadata.setStudyId(studyId);
				metadata.setVersion(questionnaireDto.getVersion() == null?"1":questionnaireDto.getVersion().toString());
				activityStructureBean.setMetadata(metadata);

				query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" and QSDTO.active=true and QSDTO.status=true ORDER BY QSDTO.sequenceNo");
				//query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId="+questionnaireDto.getId()+" and QSDTO.active=true ORDER BY QSDTO.sequenceNo");
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
						query = session.createQuery(" from InstructionsDto IDTO where IDTO.id in ("+StringUtils.join(instructionIdList, ",")+") and IDTO.active=true and IDTO.status=true");
						//query = session.createQuery(" from InstructionsDto IDTO where IDTO.id in ("+StringUtils.join(instructionIdList, ",")+") and IDTO.active=true");
						instructionsDtoList = query.list();
						if(instructionsDtoList != null && !instructionsDtoList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION, instructionsDtoList, null, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, null, questionaireStepsList, questionnaireDto);
						}
					}

					//get the questionaire List
					if(!questionIdList.isEmpty()){
						List<QuestionsDto> questionsList;
						query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdList, ",")+") and QDTO.active=true and QDTO.status=true");
						//query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdList, ",")+") and QDTO.active=true");
						questionsList = query.list();
						if( questionsList != null && !questionsList.isEmpty()){
							stepsSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getStepsInfoForQuestionnaires(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION, null, questionsList, null, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionaireStepsList, questionnaireDto);
						}
					}

					//get the forms list
					if(!formIdList.isEmpty()){
						for(Integer formId : formIdList){
							List<FormMappingDto> formList;
							query = session.createQuery(" from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId from FormDto FDTO where FDTO.formId="+formId+" and FDTO.active=true) ORDER BY FMDTO.sequenceNo ");
							//query = session.createQuery(" from FormMappingDto FMDTO where FMDTO.formId in (select FDTO.formId from FormDto FDTO where FDTO.formId="+formId+" and FDTO.active=true) ORDER BY FMDTO.sequenceNo ");
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
			case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
				//runDetailsBean = getActiveTaskFrequencyDetailsForOneTime(activeTask, runDetailsBean);
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
							activeTaskEndTime = "23:59:59";
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
			case StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME: 
				//runDetailsBean = getQuestionnaireFrequencyDetailsForOneTime(questionaire, runDetailsBean);
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
					if(dailyFrequencyList != null && !dailyFrequencyList.isEmpty()){
						for(int i=0; i<dailyFrequencyList.size(); i++){
							ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
							String activeTaskStartTime;
							String activeTaskEndTime;
							activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();
							if(i == (dailyFrequencyList.size()-1)){
								activeTaskEndTime = "23:59:59";
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
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getInstructionDetailsForQuestionnaire(instructionsDtoList, sequenceNoMap, stepsSequenceTreeMap, questionnaireStepDetailsMap);
				break;
			case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getQuestionDetailsForQuestionnaire(questionsDtoList, sequenceNoMap, stepsSequenceTreeMap, session, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList, questionaireStepsList, questionnaireDto);
				break;
			case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
				stepsOrderSequenceTreeMap = (TreeMap<Integer, QuestionnaireActivityStepsBean>) getFormDetailsForQuestionnaire(formsList, sequenceNoMap, session, stepsSequenceTreeMap, questionnaireStepDetailsMap, questionResponseTypeMasterInfoList);
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
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getInstructionDetailsForQuestionnaire(List<InstructionsDto> instructionsDtoList, Map<String, Integer> sequenceNoMap, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap) throws DAOException{
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
							if(masterInfo.getId().intValue() == questionsDto.getResponseType().intValue()){
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
					query = session.createQuery("from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionsDto.getId()+" and QRSTDTO.destinationStepId is not null");
					destinationConditionList = query.list();
					if(destinationConditionList != null && !destinationConditionList.isEmpty() && questionnaireDto.getBranching()){
						for(QuestionResponseSubTypeDto destinationDto : destinationConditionList){
							DestinationBean destination = new DestinationBean();
							destination.setCondition(StringUtils.isEmpty(destinationDto.getValue())?"":destinationDto.getValue());
							destination = getDestinationStepTypeForResponseSubType(destination, destinationDto, questionaireStepsList);
							destinationsList.add(destination);
						}
					}else{
						DestinationBean destinationBean = new DestinationBean();
						destinationBean.setCondition("");
						destinationBean.setDestination((questionStepDetails.getDestinationStepType()==null || questionStepDetails.getDestinationStepType().isEmpty())?"":questionStepDetails.getDestinationStepType());
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
	public SortedMap<Integer, QuestionnaireActivityStepsBean> getFormDetailsForQuestionnaire(List<FormMappingDto> formsList, Map<String, Integer> sequenceNoMap, Session session, SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap, Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap, List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList) throws DAOException{
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
					formBean.setResultType("grouped");
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
									if(masterInfo.getId().intValue() == formQuestionDto.getResponseType().intValue()){
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
		Map<String, Object> activeTaskFormat = new LinkedHashMap<>();
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
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
					questionFormat.put("placeholder", (reponseType == null || reponseType.getPlaceholder() == null)?"":reponseType.getPlaceholder());
					break;
				case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
					questionFormat.put("default", (reponseType == null || reponseType.getDefaultValue() == null)?0:reponseType.getDefaultValue());
					questionFormat.put("step", (reponseType == null || reponseType.getStep() == null)?1:reponseType.getStep()); //In minutes 1-30
					break;
				case StudyMetaDataConstants.QUESTION_HEIGHT:
					questionFormat.put("measurementSystem", (reponseType == null || reponseType.getMeasurementSystem() == null)?"":reponseType.getMeasurementSystem()); //Local/Metric/US
					questionFormat.put("placeholder", (reponseType == null || reponseType.getPlaceholder() == null)?"":reponseType.getPlaceholder());
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
			questionFormat.put("maxValue", (reponseType==null || reponseType.getMaxValue()==null)?10000:reponseType.getMaxValue());
			questionFormat.put("minValue", (reponseType==null || reponseType.getMinValue()==null)?-10000:reponseType.getMinValue());
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
			questionFormat.put("step", (reponseType==null || reponseType.getStep()==null)?1:getScaleStepCount(reponseType.getStep(), (Integer) questionFormat.get("maxValue"), (Integer) questionFormat.get("minValue")));
			questionFormat.put("vertical", (reponseType==null || reponseType.getVertical()==null || !reponseType.getVertical())?false:true);
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("maxValue", (reponseType==null || reponseType.getMaxValue()==null)?1:reponseType.getMaxValue());
			questionFormat.put("minValue", (reponseType==null || reponseType.getMinValue()==null)?-1:reponseType.getMinValue());
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
			questionFormat.put("maxFractionDigits", (reponseType==null || reponseType.getMaxFractionDigits()==null)?4:getContinuousScaleMaxFractionDigits((Integer) questionFormat.get("maxValue"), (Integer) questionFormat.get("minValue")));
			questionFormat.put("vertical", (reponseType==null || reponseType.getVertical()==null || !reponseType.getVertical())?false:true);
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> textChoicesList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> textScaleMap = new LinkedHashMap<>();
					textScaleMap.put("text", subType.getText()==null?"":subType.getText());
					textScaleMap.put("value", subType.getValue()==null?"":subType.getValue());
					textScaleMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					textScaleMap.put("exclusive", (subType.getExclusive() == null || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))?true:false);
					textChoicesList.add(textScaleMap);
				}
			}
			questionFormat.put("textChoices", textChoicesList);
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultValue()==null)?0:reponseType.getDefaultValue());
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
					valuePickerMap.put("text", subType.getText()==null?"":subType.getText());
					valuePickerMap.put("value", subType.getValue()==null?"":subType.getValue());
					valuePickerMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					valuePickerMap.put("exclusive", (subType.getExclusive() == null || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))?true:false);
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		List<QuestionResponseSubTypeDto> responseSubTypeList = null;
		List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
		try{
			query = session.createQuery(" from QuestionResponseSubTypeDto QRSTDTO where QRSTDTO.responseTypeId="+questionDto.getId());
			responseSubTypeList = query.list();
			if(responseSubTypeList != null && !responseSubTypeList.isEmpty()){
				for(QuestionResponseSubTypeDto subType : responseSubTypeList){
					LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
					textChoiceMap.put("text", subType.getText()==null?"":subType.getText());
					textChoiceMap.put("value", subType.getValue()==null?"":subType.getValue());
					textChoiceMap.put("detail", subType.getDetail()==null?"":subType.getDetail());
					textChoiceMap.put("exclusive", (subType.getExclusive() == null || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.NO))?false:true);
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		try{
			questionFormat.put("style", (reponseType==null || reponseType.getStyle()==null)?StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:reponseType.getStyle());
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
		Map<String, Object> questionFormat = new LinkedHashMap<>();
		String dateFormat = "";
		try{
			questionFormat.put("style", (reponseType==null || reponseType.getStyle()==null)?"":reponseType.getStyle());
			if(reponseType != null && StringUtils.isNotEmpty(reponseType.getStyle()) && reponseType.getStyle().equalsIgnoreCase(StudyMetaDataConstants.QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATE)){
				dateFormat = "yyyy-MM-dd";
			}else{
				dateFormat = "yyyy-MM-dd HH:mm:ss";
			}
			questionFormat.put("minDate", (reponseType==null || reponseType.getMinDate()==null)?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getMinDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			questionFormat.put("maxDate", (reponseType==null || reponseType.getMaxDate()==null)?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getMaxDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			questionFormat.put("default", (reponseType==null || reponseType.getDefaultDate()==null)?"":StudyMetaDataUtil.getFormattedDateTimeZone(reponseType.getDefaultDate(), dateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ")); //Date
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
			questionFormat.put("validationRegex", (reponseType==null || reponseType.getValidationRegex()==null)?"":reponseType.getValidationRegex());
			questionFormat.put("invalidMessage", (reponseType==null || reponseType.getInvalidMessage()==null)?"":reponseType.getInvalidMessage());
			questionFormat.put("multipleLines", (reponseType==null || reponseType.getMultipleLines()==null || !reponseType.getMultipleLines())?false:true);
			questionFormat.put("placeholder", (reponseType==null || reponseType.getPlaceholder()==null)?"":reponseType.getPlaceholder());
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
		String time = "00:00:00";
		try{
			startDateTime = activeTaskDto.getActiveTaskLifetimeStart()+" "+time;
			endDateTime = activeTaskDto.getActiveTaskLifetimeEnd()+" "+time;
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
						endDateTime = activeTaskDto.getActiveTaskLifetimeEnd()+" "+"23:59:59";
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
		String time = "00:00:00";
		try{
			startDateTime = questionaire.getStudyLifetimeStart()+" "+time;
			endDateTime = questionaire.getStudyLifetimeEnd()+" "+time;
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
						endDateTime = questionaire.getStudyLifetimeEnd()+" "+"23:59:59";
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
					if(questionnaireStepsDto.getDestinationStep().intValue() == stepsDto.getStepId().intValue()){
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
				if(destinationDto.getDestinationStepId().intValue() == stepsDto.getStepId().intValue()){
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
		try{
			File file = new File(imagePath);
			byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
			base64Image = new String(encoded, StandardCharsets.UTF_8);
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
	 * This method is used to get the maximum fraction digits based on the maxValue and minValue
	 * 
	 * @author Mohan
	 * @param maxValue
	 * @param minValue
	 * @return Integer
	 * @throws DAOException
	 */
	public Integer getContinuousScaleMaxFractionDigits(Integer maxValue, Integer minValue) throws DAOException{
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Starts");
		Integer maxFracDigits = 4;
		try{
			Integer diff = maxValue - minValue;
			if(diff > 2000 || diff == 20000){
				maxFracDigits = 0;
			}else if (diff > 200 || diff == 2000){
				maxFracDigits = 1;
			}else if (diff > 20 || diff == 200){
				maxFracDigits = 2;
			}else if (diff > 2 || diff == 20){
				maxFracDigits = 3;
			}else{
				maxFracDigits = 4;
			}
		}catch(Exception e){
			LOGGER.error("ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: ERROR", e);
		}
		LOGGER.info("INFO: ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: Ends");
		return maxFracDigits;
	}
	
}
