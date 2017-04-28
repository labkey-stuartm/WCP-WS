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

import com.studymetadata.bean.AppUpdatesResponse;
import com.studymetadata.bean.NotificationsBean;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.StudyUpdatesBean;
import com.studymetadata.bean.StudyUpdatesResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.dto.NotificationDto;
import com.studymetadata.dto.StudyVersionDto;
import com.studymetadata.dto.UserDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.util.HibernateUtil;
import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;

public class AppMetaDataDao {
	private static final Logger LOGGER = Logger.getLogger(AppMetaDataDao.class);

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
	 * @return TermsPolicyResponse
	 * @throws DAOException
	 */
	public TermsPolicyResponse termsPolicy() throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - termsPolicy() :: Starts");
		TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
		try{
			termsPolicyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			termsPolicyResponse.setPrivacy(propMap.get("fda.smd.pricaypolicy")==null?"":propMap.get("fda.smd.pricaypolicy"));
			termsPolicyResponse.setTerms(propMap.get("fda.smd.terms")==null?"":propMap.get("fda.smd.terms"));
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - termsPolicy() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataDao - termsPolicy() :: Ends");
		return termsPolicyResponse;
	}

	/**
	 * @author Mohan
	 * @param skip
	 * @return NotificationsResponse
	 * @throws DAOException
	 */
	public NotificationsResponse notifications(String skip) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		List<NotificationDto> notificationList = null;
		try{
			/*session = sessionFactory.openSession();
			query = session.createQuery(" from NotificationDto NDTO where NDTO.scheduleDate >="+StudyMetaDataUtil.getCurrentDate()+" LIMIT "+skip);
			notificationList = query.list();
			if(notificationList != null && !notificationList.isEmpty()){
				List<NotificationsBean> notifyList = new ArrayList<>();
				for(NotificationDto notificationDto : notificationList){
					NotificationsBean botifyBean = new NotificationsBean();
					botifyBean.setNotificationId(notificationDto.getNotificationId().toString());
					if(notificationDto.getNotificationType().equalsIgnoreCase(StudyMetaDataConstants.NOTIFICATION_TYPE_GT)){
						botifyBean.setType(StudyMetaDataConstants.NOTIFICATION_GATEWAY);
					}else{
						botifyBean.setType(StudyMetaDataConstants.NOTIFICATION_STANDALONE);
					}
					notifyList.add(botifyBean);
				}
				notificationsResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}*/
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - notifications() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: AppMetaDataDao - notifications() :: Ends");
		return notificationsResponse;
	}
	
	/**
	 * @author Mohan
	 * @param appVersion
	 * @param os
	 * @return AppUpdatesResponse
	 * @throws DAOException
	 */
	public AppUpdatesResponse appUpdates(String appVersion, String os) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - appUpdates() :: Starts");
		AppUpdatesResponse appUpdates = new AppUpdatesResponse();
		try{
			appUpdates.setMessage(StudyMetaDataConstants.SUCCESS);
			appUpdates.setForceUpdate(false);
			appUpdates.setCurrentVersion("1.0");
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - appUpdates() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataDao - appUpdates() :: Ends");
		return appUpdates;
	}
	
	/**
	 * @author Mohan
	 * @param studyId
	 * @param studyVersion
	 * @return StudyUpdatesResponse
	 * @throws DAOException
	 */
	public StudyUpdatesResponse studyUpdates(String studyId, String studyVersion) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - studyUpdates() :: Starts");
		StudyUpdatesResponse studyUpdates = new StudyUpdatesResponse();
		StudyUpdatesBean updates = new StudyUpdatesBean();
		List<StudyVersionDto> studyVersionList = null;
		StudyVersionDto currentVersion = null;
		StudyVersionDto latestVersion = null;
		try{
			session = sessionFactory.openSession();
			query = session.getNamedQuery("getStudyVersionsByCustomStudyId").setString("customStudyId", studyId);
			studyVersionList = query.list();
			if(studyVersionList != null && !studyVersionList.isEmpty()){
				currentVersion = studyVersionList.get(0);
				latestVersion = studyVersionList.get(studyVersionList.size()-1);
				updates.setConsent(currentVersion.getConsentVersion()==latestVersion.getConsentVersion()?false:true);
				updates.setActivities(currentVersion.getActivityVersion()==latestVersion.getActivityVersion()?false:true);
				updates.setResources(false);
				updates.setInfo(currentVersion.getStudyVersion()==latestVersion.getStudyVersion()?false:true);
				studyUpdates.setUpdates(updates);
				studyUpdates.setCurrentVersion(latestVersion.getStudyVersion().toString());
			}else{
				studyUpdates.setCurrentVersion(studyVersion);
			}
			studyUpdates.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - studyUpdates() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: AppMetaDataDao - studyUpdates() :: Ends");
		return studyUpdates;
	}
}
