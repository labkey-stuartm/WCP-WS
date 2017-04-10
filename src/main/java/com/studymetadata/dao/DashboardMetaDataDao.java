package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.bean.ChartsBean;
import com.studymetadata.bean.DashboardBean;
import com.studymetadata.bean.StatisticsBean;
import com.studymetadata.bean.StudyDashboardResponse;
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
	public StudyDashboardResponse studyDashboardInfo(String studyId) throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - studyDashboardInfo() :: Starts");
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
			LOGGER.error("DashboardMetaDataDao - studyDashboardInfo() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: DashboardMetaDataDao - studyDashboardInfo() :: Ends");
		return studyDashboardResponse;
	}
	
	
	/*-----------------------------Manipulate chart data methods starts----------------------------------*/
	/**
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 * This method is used to fetch the chart configuration details for single line chart
	 */
	public Map<String, Object> singleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - singleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
		try{
			configuration.put("subType", "single");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions

			Map<String, Object> axisColor = new HashMap<>();
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
			Map<String, Object> settings = new HashMap<>();
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
	 * @author Mohan
	 * @return Map<String, Object>
	 * @throws Exception
	 * 
	 * This method is used to fetch the chart configuration details for multiple line chart
	 */
	public Map<String, Object> multipleLineChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - multipleLineChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
		try{
			configuration.put("subType", "multiple");
			configuration.put("gridlines", false);
			configuration.put("animated", false);
			configuration.put("scaling", 0); //x-axis divisions

			Map<String, Object> axisColor = new HashMap<>();
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
			Map<String, Object> settings = new HashMap<>();
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
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for unique pie chart
	 */
	public Map<String, Object> uniquePieChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - uniquePieChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
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
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for range pie chart
	 */
	public Map<String, Object> rangePieChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - rangePieChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
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
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for single bar chart
	 */
	public Map<String, Object> singleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - singleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
		try{
			configuration.put("subType", "single");

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);

			//single setting only
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new HashMap<>();
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
	 * @author Mohan
	 * @return Object
	 * @throws DAOException
	 * 
	 * This method is used to fetch the chart configuration details for multiple bar chart
	 */
	public Map<String, Object> multipleBarChartDetails() throws DAOException{
		LOGGER.info("INFO: DashboardMetaDataDao - multipleBarChartDetails() :: Starts");
		Map<String, Object> configuration = new HashMap<>();
		try{
			configuration.put("subType", "multiple");

			List<String> titles = new ArrayList<>();
			configuration.put("titles", titles);

			//more than one setting
			List<Map<String, Object>> settingsList = new ArrayList<>();
			Map<String, Object> settings = new HashMap<>();
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
