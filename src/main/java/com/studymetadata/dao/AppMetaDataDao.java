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
import org.hibernate.annotations.NamedQuery;

import com.studymetadata.bean.AppUpdatesResponse;
import com.studymetadata.bean.NotificationsBean;
import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.StudyUpdatesBean;
import com.studymetadata.bean.StudyUpdatesResponse;
import com.studymetadata.bean.TermsPolicyResponse;
import com.studymetadata.dto.AppVersionDto;
import com.studymetadata.dto.NotificationDto;
import com.studymetadata.dto.ResourcesDto;
import com.studymetadata.dto.StudyDto;
import com.studymetadata.dto.StudyVersionDto;
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
	public NotificationsResponse notifications(String skip, String authorization) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - notifications() :: Starts");
		NotificationsResponse notificationsResponse = new NotificationsResponse();
		List<NotificationDto> notificationList = null;
		String bundleIdType = "";
		String platformType = "";
		List<NotificationsBean> notifyList = new ArrayList<>();
		AppVersionDto appVersion = null;
		String notificationStudyTypeQuery = "";
		String deviceType = "";
		String scheduledDate = "";
		String scheduledTime = "";
		try{
			
			bundleIdType = StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID);
			deviceType = StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
			if(StringUtils.isNotEmpty(bundleIdType) && StringUtils.isNotEmpty(deviceType)){
				platformType = deviceType.substring(0, 1).toUpperCase();
				session = sessionFactory.openSession();
				query = session.createQuery("from AppVersionDto AVDTO where AVDTO.bundleId='"+bundleIdType+"' and AVDTO.osType='"+deviceType+"' ORDER BY AVDTO.avId DESC");
				query.setMaxResults(1);
				appVersion = (AppVersionDto) query.uniqueResult();
				if(appVersion != null){
					if(StringUtils.isNotEmpty(appVersion.getCustomStudyId())){
						notificationStudyTypeQuery = " and NDTO.customStudyId in (select SDTO.customStudyId from StudyDto SDTO where SDTO.type='"+StudyMetaDataConstants.STUDY_TYPE_SD+"' and SDTO.platform like '%"+platformType+"%' and SDTO.customStudyId='"+appVersion.getCustomStudyId()+"') and NDTO.notificationType='"+StudyMetaDataConstants.NOTIFICATION_TYPE_ST+"'";
					}
					query = session.createQuery("from NotificationDto NDTO where NDTO.notificationSubType in ('"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_GENERAL+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_STUDY+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_ACTIVITY+"','"+StudyMetaDataConstants.NOTIFICATION_SUBTYPE_RESOURCE+"') "+notificationStudyTypeQuery+" and NDTO.notificationSent=true or NDTO.anchorDate=true ");
					query.setFirstResult(Integer.parseInt(skip));
					query.setMaxResults(20);
					notificationList = query.list();
					if(notificationList != null && !notificationList.isEmpty()){
						Map<Integer, NotificationsBean> notificationTreeMap = new HashMap<>(); 
						List<Integer> notificationIdsList = new ArrayList<>();
						for(NotificationDto notificationDto : notificationList){
							NotificationsBean notifyBean = new NotificationsBean();
							notifyBean.setNotificationId(notificationDto.getNotificationId().toString());
							if(notificationDto.getNotificationType().equalsIgnoreCase(StudyMetaDataConstants.NOTIFICATION_TYPE_GT)){
								notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_GATEWAY);
								notifyBean.setAudience(StudyMetaDataConstants.NOTIFICATION_AUDIENCE_ALL);
							}else{
								notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_STANDALONE);
								notifyBean.setAudience(notificationDto.isAnchorDate()?StudyMetaDataConstants.NOTIFICATION_AUDIENCE_LIMITED:StudyMetaDataConstants.NOTIFICATION_AUDIENCE_PARTICIPANTS);
							}
							notifyBean.setSubtype(StringUtils.isEmpty(notificationDto.getNotificationSubType())?"":notificationDto.getNotificationSubType());
							notifyBean.setTitle(propMap.get("fda.smd.notification.title")==null?"":propMap.get("fda.smd.notification.title"));
							notifyBean.setMessage(StringUtils.isEmpty(notificationDto.getNotificationText())?"":notificationDto.getNotificationText());
							notifyBean.setStudyId(StringUtils.isEmpty(notificationDto.getCustomStudyId())?"":notificationDto.getCustomStudyId());
							scheduledDate = notificationDto.isAnchorDate()?StudyMetaDataUtil.getCurrentDate():notificationDto.getScheduleDate();
							scheduledTime = StringUtils.isEmpty(notificationDto.getScheduleTime())?"00:00:00":notificationDto.getScheduleTime();
							notifyBean.setDate(StudyMetaDataUtil.getFormattedDateTimeZone(scheduledDate+" "+scheduledTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
							
							notificationIdsList.add(notificationDto.getNotificationId());
							notificationTreeMap.put(notificationDto.getNotificationId(), notifyBean);
						}
						
						//reorder the list i.e. latest should come first 
						Collections.sort(notificationIdsList, Collections.reverseOrder());
						
						//get the notification bean based on the id
						for(Integer notificationId : notificationIdsList){
							notifyList.add(notificationTreeMap.get(notificationId));
						}
					}
				}
			}
			notificationsResponse.setNotifications(notifyList);
			notificationsResponse.setMessage(StudyMetaDataConstants.SUCCESS);
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
	public AppUpdatesResponse appUpdates(String appVersion, String authCredentials) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - appUpdates() :: Starts");
		AppUpdatesResponse appUpdates = new AppUpdatesResponse();
		AppVersionDto appVersionDto = null;
		String os = "";
		String bundleId = "";
		try{
			os = StudyMetaDataUtil.platformType(authCredentials, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
			bundleId = StudyMetaDataUtil.getBundleIdFromAuthorization(authCredentials);
			if(StringUtils.isNotEmpty(os)){
				session = sessionFactory.openSession();
				query = session.createQuery("from AppVersionDto AVDTO where AVDTO.osType='"+os+"' and AVDTO.bundleId='"+bundleId+"' ORDER BY AVDTO.appVersion DESC");
				query.setMaxResults(1);
				appVersionDto = (AppVersionDto) query.uniqueResult();
			}
			
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
		List<ResourcesDto> resourcesList = null;
		StudyDto studyDto = null;
		try{
			session = sessionFactory.openSession();
			query = session.getNamedQuery("getStudyUpdatesDetailsByCurrentVersion").setString("customStudyId", studyId).setFloat("studyVersion", Float.valueOf(studyVersion));
			studyVersionList = query.list();
			if(studyVersionList != null && !studyVersionList.isEmpty()){
				currentVersion = studyVersionList.get(0);
				latestVersion = studyVersionList.get(studyVersionList.size()-1);
				updates.setConsent(latestVersion.getConsentVersion().floatValue() > currentVersion.getConsentVersion().floatValue()?true:false);
				updates.setActivities(latestVersion.getActivityVersion().floatValue() > currentVersion.getActivityVersion().floatValue()?true:false);
				updates.setResources(latestVersion.getStudyVersion().floatValue() > currentVersion.getStudyVersion().floatValue()?true:false);
				//check whether resources are available for the latest version or not
				query = session.createQuery("from ResourcesDto RDTO where RDTO.studyId in (select SDTO.id from StudyDto SDTO where SDTO.customStudyId='"+studyId+"' and ROUND(SDTO.version, 1)="+latestVersion.getStudyVersion()+")");
				resourcesList = query.list();
				if(resourcesList==null||resourcesList.isEmpty()){
					updates.setResources(false);
				}
				updates.setInfo(latestVersion.getStudyVersion().floatValue() > currentVersion.getStudyVersion().floatValue()?true:false);
				studyUpdates.setCurrentVersion(latestVersion.getStudyVersion().toString());
			}else{
				studyUpdates.setCurrentVersion(studyVersion);
			}
			//get the status of the latest study
			query = session.createQuery("from StudyDto SDTO where SDTO.customStudyId='"+studyId+"' ORDER BY SDTO.id DESC");
			query.setMaxResults(1);
			studyDto = (StudyDto) query.uniqueResult();
			if(studyDto!=null){
				switch (studyDto.getStatus()) {
					case StudyMetaDataConstants.STUDY_STATUS_ACTIVE: updates.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
						break;
					case StudyMetaDataConstants.STUDY_STATUS_PAUSED: updates.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
						break;
					case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH: updates.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
						break;
					case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED: updates.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
						break;
					default:
						break;
				}
			}
			studyUpdates.setUpdates(updates);
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
	public String updateAppVersionDetails(String forceUpdate, String osType, String appVersion, String bundleId, String customStudyId) throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - updateAppVersionDetails() :: Starts");
		String updateAppVersionResponse = "OOPS! Something went wrong.";
		List<AppVersionDto> appVersionDtoList = null;
		Boolean updateFlag = false;
		AppVersionDto appVersionDto = new AppVersionDto();
		try{
			session = sessionFactory.openSession();
			query = session.createQuery("from AppVersionDto AVDTO where AVDTO.osType='"+osType+"' and AVDTO.bundleId='"+bundleId+"' ORDER BY AVDTO.appVersion DESC");
			appVersionDtoList = query.list();
			if(appVersionDtoList != null && !appVersionDtoList.isEmpty()){
				if(Float.parseFloat(appVersion) == appVersionDtoList.get(0).getAppVersion().floatValue()){
					if(Integer.parseInt(forceUpdate) == appVersionDtoList.get(0).getForceUpdate().intValue()){
						updateAppVersionResponse = "v"+appVersion+" is already available for os "+osType+"";
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
								updateAppVersionResponse = "v"+appVersion+" is already available for os "+osType+"";
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
				appVersionDto.setBundleId(bundleId);
				appVersionDto.setCustomStudyId(customStudyId);
				
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
