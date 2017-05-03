package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.studymetadata.dto.AppVersionDto;
import com.studymetadata.dto.NotificationDto;
import com.studymetadata.dto.StudyVersionDto;
import com.studymetadata.exception.DAOException;
import com.studymetadata.exception.OrchestrationException;
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
			session = sessionFactory.openSession();
			query = session.createQuery("from NotificationDto NDTO where NDTO.notificationSent=true and NDTO.notificationSubType in ('"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_GENERAL+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_STUDY+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_ACTIVITY+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_RESOURCE+"')");
			query.setFirstResult(Integer.parseInt(skip));
			notificationList = query.list();
			if(notificationList != null && !notificationList.isEmpty()){
				Map<Integer, NotificationsBean> notificationTreeMap = new HashMap<>(); 
				List<NotificationsBean> notifyList = new ArrayList<>();
				List<Integer> notificationIdsList = new ArrayList<>();
				for(NotificationDto notificationDto : notificationList){
					NotificationsBean notifyBean = new NotificationsBean();
					notifyBean.setNotificationId(notificationDto.getNotificationId().toString());
					if(notificationDto.getNotificationType().equalsIgnoreCase(StudyMetaDataConstants.NOTIFICATION_TYPE_GT)){
						notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_GATEWAY);
					}else{
						notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_STANDALONE);
					}
					notifyBean.setAudience(StudyMetaDataConstants.NOTIFICATION_AUDIENCE_ALL);
					notifyBean.setSubtype(StringUtils.isEmpty(notificationDto.getNotificationSubType())?"":notificationDto.getNotificationSubType());
					notifyBean.setTitle(propMap.get("fda.smd.notification.title")==null?"":propMap.get("fda.smd.notification.title"));
					notifyBean.setMessage(StringUtils.isEmpty(notificationDto.getNotificationText())?"":notificationDto.getNotificationText());
					notifyBean.setStudyId(StringUtils.isEmpty(notificationDto.getCustomStudyId())?"":notificationDto.getCustomStudyId());
					
					notificationIdsList.add(notificationDto.getNotificationId());
					notificationTreeMap.put(notificationDto.getNotificationId(), notifyBean);
				}
				
				//reorder the list i.e. latest should come first 
				Collections.sort(notificationIdsList, Collections.reverseOrder());
				
				//get the notification bean based on the id
				for(Integer notificationId : notificationIdsList){
					notifyList.add(notificationTreeMap.get(notificationId));
				}
				notificationsResponse.setNotifications(notifyList);
				notificationsResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
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
		AppVersionDto appVersionDto = null;
		try{
			session = sessionFactory.openSession();
			query = session.createQuery("from AppVersionDto AVDTO where AVDTO.osType='"+os+"' ORDER BY AVDTO.appVersion DESC");
			query.setMaxResults(1);
			appVersionDto = (AppVersionDto) query.uniqueResult();
			if(appVersionDto != null){
				appUpdates.setCurrentVersion(String.valueOf(appVersionDto.getAppVersion()));
				appUpdates.setForceUpdate(appVersionDto.getForceUpdate().intValue()==0?false:true);
			}else{
				appUpdates.setForceUpdate(false);
				appUpdates.setCurrentVersion(appVersion);
			}
			appUpdates.setMessage(StudyMetaDataConstants.SUCCESS);
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - appUpdates() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
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
				updates.setResources(currentVersion.getStudyVersion()==latestVersion.getStudyVersion()?false:true);
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
	
	/**
	 * @author Mohan
	 * @param forceUpdate
	 * @param osType
	 * @param appVersion
	 * @return String
	 * @throws DAOException
	 */
	public String updateAppVersionDetails(String forceUpdate, String osType, String appVersion) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - updateAppVersionDetails() :: Starts");
		String updateAppVersionResponse = "OOPS! Something went wrong.";
		List<AppVersionDto> appVersionDtoList = null;
		Boolean updateFlag = false;
		AppVersionDto appVersionDto = new AppVersionDto();
		try{
			session = sessionFactory.openSession();
			query = session.createQuery("from AppVersionDto AVDTO where AVDTO.osType='"+osType+"' ORDER BY AVDTO.appVersion DESC");
			appVersionDtoList = query.list();
			if(appVersionDtoList != null && !appVersionDtoList.isEmpty()){
				if(Float.parseFloat(appVersion) == appVersionDtoList.get(0).getAppVersion().floatValue()){
					if(Integer.parseInt(forceUpdate) == appVersionDtoList.get(0).getForceUpdate().intValue()){
						updateAppVersionResponse = ""+appVersion+" is already available for os "+osType+"";
					}else{
						updateFlag = true;
						appVersionDto = appVersionDtoList.get(0);
					}
				}else{
					for(AppVersionDto avDto : appVersionDtoList){
						if(Float.parseFloat(appVersion) > avDto.getAppVersion().floatValue()){
							updateFlag = true;
							break;
						}
						
						if(Float.parseFloat(appVersion) == avDto.getAppVersion().floatValue()){
							if(Integer.parseInt(forceUpdate) == avDto.getForceUpdate().intValue()){
								updateAppVersionResponse = ""+appVersion+" is already available for os "+osType+"";
								break;
							}else{
								updateFlag = true;
								appVersionDto = avDto;
							}
						}
					}
				}
			}else{
				updateFlag = true;
			}
			
			//update flag
			if(updateFlag){
				transaction = session.beginTransaction();
				appVersionDto.setAppVersion(Float.parseFloat(appVersion));
				appVersionDto.setForceUpdate(Integer.parseInt(forceUpdate));
				appVersionDto.setOsType(osType);
				appVersionDto.setCreatedOn(StudyMetaDataUtil.getCurrentDateTime());
				session.saveOrUpdate(appVersionDto);
				
				transaction.commit();
				updateAppVersionResponse = "App Version was successfully updated to v"+appVersion+" for "+osType+" os.";
			}
			
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - updateAppVersionDetails() :: ERROR", e);
			if(transaction.isActive()){
				transaction.rollback();
			}
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: AppMetaDataDao - updateAppVersionDetails() :: Ends");
		return updateAppVersionResponse;
	}
}
