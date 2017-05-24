package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.bean.ChartDataSourceBean;
import com.studymetadata.bean.ChartsBean;
import com.studymetadata.bean.DashboardActivityBean;
import com.studymetadata.bean.DashboardBean;
import com.studymetadata.bean.StatisticsBean;
import com.studymetadata.bean.StatisticsDataSourceBean;
import com.studymetadata.bean.StudyDashboardResponse;
import com.studymetadata.dto.ActiveTaskAttrtibutesValuesDto;
import com.studymetadata.dto.ActiveTaskDto;
import com.studymetadata.dto.ActiveTaskFormulaDto;
import com.studymetadata.dto.FormDto;
import com.studymetadata.dto.FormMappingDto;
import com.studymetadata.dto.QuestionnairesDto;
import com.studymetadata.dto.QuestionnairesStepsDto;
import com.studymetadata.dto.QuestionsDto;
import com.studymetadata.dto.StatisticImageListDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyVersionDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class DashboardMetaDataDao {
	private static final Logger LOGGER = Logger.getLogger(DashboardMetaDataDao.class);

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
	 * @return StudyDashboardResponse
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public StudyDashboardResponse studyDashboardInfo(String studyId) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - studyDashboardInfo() :: Starts");
		StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
		DashboardBean dashboard = new DashboardBean();
		List<ChartsBean> chartsList = new ArrayList<>();
		List<StatisticsBean> statisticsList = new ArrayList<>();
		Map<String, Object> activityMap = new LinkedHashMap<>();
		Map<String, Object> questionnaireMap = new LinkedHashMap<>();
		List<Integer> activeTaskIdsList = new ArrayList<>();
		List<Integer> questionnaireIdsList = new ArrayList<>();
		List<ActiveTaskDto> activeTaskList = null;
		List<QuestionnairesDto> questionnaireList = null;
		List<ActiveTaskAttrtibutesValuesDto> activeTaskValuesList = null;
		List<QuestionnairesStepsDto> questionnaireStepsList = null;
		List<Integer> questionIdsList = new ArrayList<>();
		List<Integer> formIdsList = new ArrayList<>();
		List<ActiveTaskFormulaDto> formulaDtoList = null;
		List<StatisticImageListDto> statisticImageList = null;
		StudyDto studyDto = null;
		StudyVersionDto studyVersionDto= null;
		try{
			session = sessionFactory.openSession();
			query =  session.getNamedQuery("getLiveStudyIdByCustomStudyId").setString("customStudyId", studyId);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto != null){
				query =  session.getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyDto.getCustomStudyId()).setFloat("studyVersion", studyDto.getVersion());
				query.setMaxResults(1);
				studyVersionDto = (StudyVersionDto) query.uniqueResult();
				
				//Active Task details
				//query = session.getNamedQuery("getActiveTaskDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyVersionDto.getCustomStudyId()).setFloat("version", studyVersionDto.getActivityVersion());
				query = session.getNamedQuery("getActiveTaskDetailsByCustomStudyIdAndIsLive").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1);
				activeTaskList = query.list();
				if( activeTaskList != null && !activeTaskList.isEmpty()){
					for(ActiveTaskDto activeTask : activeTaskList){
						activityMap.put(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTask.getId(), activeTask);
						activeTaskIdsList.add(activeTask.getId());
					}
				}
				
				//Questionnaire details
				//query = session.getNamedQuery("getQuestionnaireDetailsByCustomStudyIdAndVersion").setString("customStudyId", studyVersionDto.getCustomStudyId()).setFloat("version", studyVersionDto.getActivityVersion());
				query = session.getNamedQuery("getQuestionnaireDetailsByCustomStudyIdAndIsLive").setString("customStudyId", studyVersionDto.getCustomStudyId()).setInteger("live", 1);
				questionnaireList = query.list();
				if(questionnaireList != null && !questionnaireList.isEmpty()){
					for(QuestionnairesDto questionnaireDto :questionnaireList){
						questionnaireMap.put(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId(), questionnaireDto);
						questionnaireIdsList.add(questionnaireDto.getId());
					}
					
					if(questionnaireIdsList != null && !questionnaireIdsList.isEmpty()){
						query = session.createQuery("from QuestionnairesStepsDto QSDTO where QSDTO.questionnairesId in ("+StringUtils.join(questionnaireIdsList, ',')+") and QSDTO.stepType in ('"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION+"','"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM+"') and QSDTO.active=true and QSDTO.status=true ORDER BY QSDTO.questionnairesId, QSDTO.sequenceNo");
						questionnaireStepsList = query.list();
						if(questionnaireStepsList != null && !questionnaireStepsList.isEmpty()){
							for(QuestionnairesStepsDto questionnaireSteps : questionnaireStepsList){
								//questionsList
								if(questionnaireSteps.getStepType().equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)){
									questionIdsList.add(questionnaireSteps.getInstructionFormId());
								}
								
								//formList
								if(questionnaireSteps.getStepType().equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)){
									formIdsList.add(questionnaireSteps.getInstructionFormId());
								}
								activityMap.put(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireSteps.getInstructionFormId()+"-"+questionnaireSteps.getStepType(), questionnaireSteps);
							}
						}
					}
				}
				
				query = session.createQuery(" from ActiveTaskFormulaDto ATCDTO");
				formulaDtoList = query.list();
				
				query = session.createQuery(" from StatisticImageListDto STDTO");
				statisticImageList = query.list();
				
				//get statistics and chart details for Active Task
				if(activeTaskIdsList != null && !activeTaskIdsList.isEmpty()){
					query = session.createQuery("from ActiveTaskAttrtibutesValuesDto ATAVDTO where ATAVDTO.addToLineChart=true or ATAVDTO.useForStatistic=true and ATAVDTO.activeTaskId in ("+StringUtils.join(activeTaskIdsList, ',')+")");
					activeTaskValuesList = query.list();
					if(activeTaskValuesList != null && !activeTaskValuesList.isEmpty()){
						for(ActiveTaskAttrtibutesValuesDto activeTaskAttrDto : activeTaskValuesList){
							ActiveTaskDto activeTaskDto = null;
							activeTaskDto = (ActiveTaskDto) activityMap.get(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK+"-"+activeTaskAttrDto.getActiveTaskId());
							if(activeTaskDto != null){
								activeTaskAttrDto.setActivityType(StudyMetaDataConstants.DASHBOARD_ACTIVE_TASK);
								activeTaskAttrDto.setActivityStepKey(StringUtils.isEmpty(activeTaskDto.getShortTitle())?"":activeTaskDto.getShortTitle());
								activeTaskAttrDto.setActivityVersion(activeTaskDto.getStudyVersion()==null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:activeTaskDto.getStudyVersion().toString());
								activeTaskAttrDto.setActivityId(activeTaskDto.getShortTitle());
								if(activeTaskAttrDto.isAddToLineChart()){
									chartsList = getChartDetails(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK, activeTaskAttrDto, null, chartsList, activeTaskDto.getShortTitle());
								}
								
								if(activeTaskAttrDto.isUseForStatistic()){
									statisticsList = getStatisticsDetails(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK, activeTaskAttrDto, null, statisticsList,  formulaDtoList, statisticImageList);
								}
							}
						}
					}
				}
				
				//get statistics and chart details for Questionnaire
				if(questionIdsList != null && !questionIdsList.isEmpty()){
					List<QuestionsDto> questionsList;
					query = session.createQuery(" from QuestionsDto QDTO where QDTO.id in ("+StringUtils.join(questionIdsList, ",")+") and QDTO.active=true and QDTO.status=true and QDTO.addLineChart='"+StudyMetaDataConstants.YES+"' or QDTO.useStasticData='"+StudyMetaDataConstants.YES+"' ");
					questionsList = query.list();
					for(QuestionsDto questionDto : questionsList){
						QuestionnairesStepsDto questionnaireSteps = (QuestionnairesStepsDto) activityMap.get(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionDto.getId()+"-"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
						if(questionnaireSteps != null){
							QuestionnairesDto questionnaireDto = (QuestionnairesDto) questionnaireMap.get(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireSteps.getQuestionnairesId());
							if(questionnaireDto != null){
								questionDto.setActivityType(StudyMetaDataConstants.DASHBOARD_QUESTIONNAIRE);
								questionDto.setActivityStepKey(StringUtils.isEmpty(questionnaireSteps.getStepShortTitle())?"":questionnaireSteps.getStepShortTitle());
								questionDto.setActivityVersion(questionnaireDto.getVersion()==null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:questionnaireDto.getVersion().toString());
								questionDto.setActivityId(questionnaireDto.getShortTitle());
								if(questionDto.getAddLineChart().equalsIgnoreCase(StudyMetaDataConstants.YES)){
									chartsList = getChartDetails(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE, null, questionDto, chartsList, questionnaireSteps.getStepShortTitle());
								}
								
								if(questionDto.getUseStasticData().equalsIgnoreCase(StudyMetaDataConstants.YES)){
									statisticsList = getStatisticsDetails(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE, null, questionDto, statisticsList, formulaDtoList, statisticImageList);
								}
							}
						}
					}
				}
				
				if(formIdsList != null && !formIdsList.isEmpty()){
					List<FormDto> formDtoList = null;
					query = session.createQuery("from FormDto FDTO where FDTO.formId in ("+StringUtils.join(formIdsList, ',')+") and FDTO.active=true");
					formDtoList = query.list();
					if(formDtoList != null && !formDtoList.isEmpty()){
						for(FormDto form : formDtoList){
							List<Integer> formQuestionIdsList = new ArrayList<>();
							List<FormMappingDto> formMappingDtoList;
							query = session.createQuery("from FormMappingDto FMDTO where FMDTO.formId="+form.getFormId()+" order by FMDTO.sequenceNo");
							formMappingDtoList = query.list();
							if(formMappingDtoList != null && !formMappingDtoList.isEmpty()){
								for(FormMappingDto formMappingDto : formMappingDtoList){
									formQuestionIdsList.add(formMappingDto.getQuestionId());
								}
							}
							
							if(!formQuestionIdsList.isEmpty()){
								List<QuestionsDto> formQuestionDtoList = null;
								query = session.createQuery("from QuestionsDto FQDTO where FQDTO.id in ("+StringUtils.join(formQuestionIdsList, ',')+") and FQDTO.active=true and FQDTO.status=true and FQDTO.addLineChart='"+StudyMetaDataConstants.YES+"' or FQDTO.useStasticData='"+StudyMetaDataConstants.YES+"' ");
								formQuestionDtoList = query.list();
								if(formQuestionDtoList != null && !formQuestionDtoList.isEmpty()){
									for(QuestionsDto questionDto : formQuestionDtoList){
										if(formQuestionIdsList.contains(questionDto.getId())){
											QuestionnairesStepsDto questionnaireSteps = (QuestionnairesStepsDto) activityMap.get(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+form.getFormId()+"-"+StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM);
											if(questionnaireSteps != null){
												QuestionnairesDto questionnaireDto = (QuestionnairesDto) questionnaireMap.get(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireSteps.getQuestionnairesId());
												if(questionnaireDto != null){
													questionDto.setActivityType(StudyMetaDataConstants.DASHBOARD_QUESTIONNAIRE);
													questionDto.setActivityStepKey(StringUtils.isEmpty(questionDto.getShortTitle())?"":questionDto.getShortTitle());
													questionDto.setActivityVersion(questionnaireDto.getVersion()==null?StudyMetaDataConstants.STUDY_DEFAULT_VERSION:questionnaireDto.getVersion().toString());
													questionDto.setActivityId(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE+"-"+questionnaireDto.getId());
													if(questionDto.getAddLineChart().equalsIgnoreCase(StudyMetaDataConstants.YES)){
														chartsList = getChartDetails(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE, null, questionDto, chartsList, questionDto.getShortTitle());
													}
													
													if(questionDto.getUseStasticData().equalsIgnoreCase(StudyMetaDataConstants.YES)){
														statisticsList = getStatisticsDetails(StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE, null, questionDto, statisticsList, formulaDtoList, statisticImageList);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				dashboard.setCharts(chartsList);
				dashboard.setStatistics(statisticsList);
				studyDashboardResponse.setDashboard(dashboard);
				studyDashboardResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - studyDashboardInfo() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: DashboardMetaDataDao - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
	
	/**
	 * This method is used to get the chart details based on the activity type
	 * 
	 * @author Mohan
	 * @param activityType
	 * @param activeTask
	 * @param questionnaire
	 * @param activityMap
	 * @param questionnaireMap
	 * @param chartsList
	 * @return List<ChartsBean>
	 * @throws DAOException
	 */
	public List<ChartsBean> getChartDetails(String activityType, ActiveTaskAttrtibutesValuesDto activeTask, QuestionsDto question, List<ChartsBean> chartsList, String chartTitle) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - getChartDetails() :: Starts");
		ChartsBean chart = new ChartsBean();
		ChartDataSourceBean dataSource = new ChartDataSourceBean();
		DashboardActivityBean activity = new DashboardActivityBean();
		try{
			if(activityType.equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)){
				chart.setTitle(chartTitle);
				chart.setDisplayName(StringUtils.isEmpty(activeTask.getTitleChat())?"":activeTask.getTitleChat());
				chart.setType(StudyMetaDataConstants.CHART_TYPE_LINE);
				chart.setConfiguration(singleBarChartDetails());
				
				dataSource.setType(activeTask.getActivityType());
				dataSource.setKey(activeTask.getActivityStepKey());
				
				activity.setActivityId(activeTask.getActivityId());
				activity.setVersion(activeTask.getActivityVersion());
				dataSource.setActivity(activity);
				
				dataSource.setTimeRangeType(StringUtils.isEmpty(activeTask.getTimeRangeChart())?"":getTimeRangeType(activeTask.getTimeRangeChart()));
				dataSource.setStartTime("");
				dataSource.setEndTime("");
				
				chart.setDataSource(dataSource);
			}else{
				chart.setTitle(chartTitle);
				chart.setDisplayName(StringUtils.isEmpty(question.getChartTitle())?"":question.getChartTitle());
				chart.setType(StudyMetaDataConstants.CHART_TYPE_LINE);
				chart.setConfiguration(singleBarChartDetails());
				
				dataSource.setType(question.getActivityType());
				dataSource.setKey(question.getActivityStepKey());
				
				activity.setActivityId(question.getActivityId());
				activity.setVersion(question.getActivityVersion());
				dataSource.setActivity(activity);
				
				dataSource.setTimeRangeType(StringUtils.isEmpty(question.getLineChartTimeRange())?"":getTimeRangeType(question.getLineChartTimeRange()));
				dataSource.setStartTime("");
				dataSource.setEndTime("");
				
				chart.setDataSource(dataSource);
			}
			chartsList.add(chart);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - getChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - getChartDetails() :: Ends");
		return chartsList;
	}
	
	/**
	 * This method is used to get the statistics details based on the activity type
	 * 
	 * @author Mohan
	 * @param activityType
	 * @param activeTask
	 * @param questionnaire
	 * @param activityMap
	 * @param questionnaireMap
	 * @param chartsList
	 * @return List<ChartsBean>
	 * @throws DAOException
	 */
	public List<StatisticsBean> getStatisticsDetails(String activityType, ActiveTaskAttrtibutesValuesDto activeTask, QuestionsDto question, List<StatisticsBean> statisticsList, List<ActiveTaskFormulaDto> formulaDtoList, List<StatisticImageListDto> statisticImageList) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - getStatisticsDetails() :: Starts");
		StatisticsBean statistics = new StatisticsBean();
		StatisticsDataSourceBean dataSource = new StatisticsDataSourceBean();
		DashboardActivityBean activity = new DashboardActivityBean();
		try{
			if(activityType.equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)){
				statistics.setTitle(StringUtils.isEmpty(activeTask.getIdentifierNameStat())?"":activeTask.getIdentifierNameStat());
				statistics.setDisplayName(StringUtils.isEmpty(activeTask.getDisplayNameStat())?"":activeTask.getDisplayNameStat());
				statistics.setStatType(StringUtils.isEmpty(activeTask.getUploadTypeStat())?"":getStatisticsType(Integer.parseInt(activeTask.getUploadTypeStat()), statisticImageList));
				statistics.setUnit(StringUtils.isEmpty(activeTask.getDisplayUnitStat())?"":activeTask.getDisplayUnitStat());
				statistics.setCalculation(StringUtils.isEmpty(activeTask.getFormulaAppliedStat())?"":getFormulaType(Integer.parseInt(activeTask.getFormulaAppliedStat()), formulaDtoList));
				
				activity.setActivityId(activeTask.getActivityId());
				activity.setVersion(activeTask.getActivityVersion());
				dataSource.setActivity(activity);
				dataSource.setKey(activeTask.getActivityStepKey());
				dataSource.setType(activeTask.getActivityType());
				statistics.setDataSource(dataSource);
			}else{
				statistics.setTitle(StringUtils.isEmpty(question.getStatShortName())?"":question.getStatShortName());
				statistics.setDisplayName(StringUtils.isEmpty(question.getStatDisplayName())?"":question.getStatDisplayName());
				statistics.setStatType(question.getStatType()==null?"":getStatisticsType(question.getStatType(), statisticImageList));
				statistics.setUnit(StringUtils.isEmpty(question.getStatDisplayUnits())?"":question.getStatDisplayUnits());
				statistics.setCalculation(question.getStatFormula()==null?"":getFormulaType(question.getStatFormula(), formulaDtoList));
				
				dataSource.setType(question.getActivityType());
				dataSource.setKey(question.getActivityStepKey());
				activity.setActivityId(question.getActivityId());
				activity.setVersion(question.getActivityVersion());
				dataSource.setActivity(activity);
				
				statistics.setDataSource(dataSource);
			}
			statisticsList.add(statistics);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - getStatisticsDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - getStatisticsDetails() :: Ends");
		return statisticsList;
	}
	
	/**
	 * @author Mohan
	 * @param timeRange
	 * @return String
	 * @throws DAOException
	 */
	public String getTimeRangeType(String timeRange) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - getTimeRangeType() :: Starts");
		String type = timeRange;
		try{
			if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.DAYS_OF_THE_CURRENT_WEEK)){
				type=StudyMetaDataConstants.CHART_DAY_OF_WEEK;
			}else if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.DAYS_OF_THE_CURRENT_MONTH)){
				type=StudyMetaDataConstants.CHART_DAYS_OF_MONTH;
			}else if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.WEEKS_OF_THE_CURRENT_MONTH)){
				type=StudyMetaDataConstants.CHART_WEEK_OF_MONTH;
			}else if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.MONTHS_OF_THE_CURRENT_YEAR)){
				type=StudyMetaDataConstants.CHART_MONTHS_OF_YEAR;
			}else if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.RUN_BASED)){
				type=StudyMetaDataConstants.CHART_RUNS;
			}else if(timeRange.equalsIgnoreCase(StudyMetaDataConstants.MULTIPLE_TIMES_A_DAY)){
				type=StudyMetaDataConstants.CHART_HOURS_A_DAY;
			}
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - getTimeRangeType() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - getTimeRangeType() :: Ends");
		return type;
	}
	
	/**
	 * This method is used to get the statistics value by statId
	 * 
	 * @author Mohan
	 * @param statisticTypeId
	 * @param statisticImageList
	 * @return String
	 * @throws DAOException
	 */
	public String getStatisticsType(Integer statisticTypeId,List<StatisticImageListDto> statisticImageList) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - getStatisticsType() :: Starts");
		String statisticType = "";
		try{
			if(statisticImageList != null && !statisticImageList.isEmpty()){
				for(StatisticImageListDto statistic : statisticImageList){
					if(statisticTypeId.intValue() == statistic.getStatisticImageId().intValue()){
						statisticType = statistic.getValue();
						break;
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - getStatisticsType() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - getStatisticsType() :: Ends");
		return statisticType;
	}
	
	/**
	 * This method is used to get the fomula value by formulaId
	 * 
	 * @author Mohan
	 * @param formulaTypeId
	 * @param formulaDtoList
	 * @return String
	 * @throws DAOException
	 */
	public String getFormulaType(Integer formulaTypeId,List<ActiveTaskFormulaDto> formulaDtoList) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - getFormulaType() :: Starts");
		String formulaType = "";
		try{
			if(formulaDtoList != null && !formulaDtoList.isEmpty()){
				for(ActiveTaskFormulaDto formulaDto : formulaDtoList){
					if(formulaTypeId.intValue() == formulaDto.getActivetaskFormulaId().intValue()){
						formulaType = formulaDto.getFormula();
						break;
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - getFormulaType() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - getFormulaType() :: Ends");
		return formulaType;
	}
	
	
	/*-----------------------------Manipulate chart data methods starts----------------------------------*/
	/**
	 * This method is used to fetch the chart configuration details for single line chart
	 * 
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 */
	public Map<String, Object> singleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - singleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "single");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions

			Map<String, Object> axisColor = new LinkedHashMap<>();
			axisColor.put("x-axis", "#fff"); //hexcolor
			axisColor.put("y-axis", "#000"); //hexcolor
			configuration.put("axisColor", axisColor);

			configuration.put("max", 0.0d);
			configuration.put("min", 0.0d);

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);
			configuration.put("defaultText", "");

			//single setting only
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new LinkedHashMap<>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<>();
			settings.put("pointValues", pointValues);
			settings.put("lineColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - singleLineChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - singleLineChartDetails() :: Ends");
		return configuration;
	}

	/**
	 * This method is used to fetch the chart configuration details for multiple line chart
	 * 
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 */
	public Map<String, Object> multipleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - multipleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "multiple");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions

			Map<String, Object> axisColor = new LinkedHashMap<>();
			axisColor.put("x-axis", "#fff"); //hexcolor
			axisColor.put("y-axis", "#000"); //hexcolor
			configuration.put("axisColor", axisColor);

			configuration.put("max", 0.0d);
			configuration.put("min", 0.0d);

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);
			configuration.put("defaultText", "");

			// more than one setting
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new LinkedHashMap<>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<>();
			settings.put("pointValues", pointValues);
			settings.put("lineColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - multipleLineChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - multipleLineChartDetails() :: Ends");
		return configuration;
	}

	/**
	 * This method is used to fetch the chart configuration details for unique pie chart
	 * 
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 */
	public Map<String, Object> uniquePieChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - uniquePieChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "unique-responses");
			configuration.put("numberOfSegments", 0); // =0, calculated at run time <number of unique responses>

			List<Double> values = new ArrayList<>();
			configuration.put("values", values); //calculated <count of each unique response / total number of responses>

			List<String> colors = new ArrayList<>();
			configuration.put("colors", colors); //<default colors>

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles); //<unique response>
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - uniquePieChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - uniquePieChartDetails() :: Ends");
		return configuration;
	}

	/**
	 * This method is used to fetch the chart configuration details for range pie chart
	 * 
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 */
	public Map<String, Object> rangePieChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - rangePieChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "range-responses");
			configuration.put("numberOfSegments", 5); //Number of ranges

			List<Double> values = new ArrayList<>();
			configuration.put("values", values); //calculated <count of responses in each range / total number of responses>

			List<String> colors = new ArrayList<>();
			configuration.put("colors", colors);

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles); //<unique response>
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - rangePieChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - rangePieChartDetails() :: Ends");
		return configuration;
	}

	/**
	 * This method is used to fetch the chart configuration details for single bar chart
	 * 
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 */
	public Map<String, Object> singleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - singleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "single");

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);

			//single setting only
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new LinkedHashMap<>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<>();
			settings.put("pointValues", pointValues);
			settings.put("barColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - singleBarChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - singleBarChartDetails() :: Ends");
		return configuration;
	}

	/**
	 * This method is used to fetch the chart configuration details for multiple bar chart
	 * 
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 */
	public Map<String, Object> multipleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - multipleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new LinkedHashMap<>();
		try{
			configuration.put("subType", "multiple");

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);

			//more than one setting
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new LinkedHashMap<>();
			settings.put("numberOfPoints", 1);
			List<Double> pointValues = new ArrayList<>();
			settings.put("pointValues", pointValues);
			settings.put("barColor", "#d3d3d3");
			settingsList.add(settings);
			configuration.put("settings", settingsList);
		}catch(Exception e){
			LOGGER.error("DashboardMetaDataDao - multipleBarChartDetails() :: ERROR", e);
		}
		LOGGER.info("INFO: DashboardMetaDataDao - multipleBarChartDetails() :: Ends");
		return configuration;
	}
	/*-----------------------------Manipulate chart data methods ends----------------------------------*/
}
