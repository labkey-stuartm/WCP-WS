package com.studymetadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.studymetadata.bean.NotificationsResponse;
import com.studymetadata.bean.TermsPolicyResponse;
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
		try{
			if(StringUtils.isNotEmpty(skip)){
				notificationsResponse.setMessage(StudyMetaDataConstants.SUCCESS);
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - notifications() :: ERROR", e);
		}
		LOGGER.info("INFO: AppMetaDataDao - notifications() :: Ends");
		return notificationsResponse;
	}
	
	/**
	 * @author Mohan
	 * @return superAdminsList
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UserDto> superAdminsList() throws DAOException{
		LOGGER.info("INFO: AppMetaDataDao - superAdminsList() :: Starts");
		List<UserDto> superAdminsList = new ArrayList<>();
		List<Object[]> adminList = null;
		try{
			session = sessionFactory.openSession();
			query = session.createSQLQuery("select u.email, u.first_name, u.last_name, u.user_id from users u where u.role_id=1 and u.status=true");
			adminList = query.list();
			if(adminList != null && !adminList.isEmpty()){
				for(Object[] obj : adminList){
					UserDto admin = new UserDto();
					admin.setUserEmail((String) obj[0]);
					admin.setFirstName((String) obj[1]);
					admin.setLastName((String) obj[2]);
					admin.setUserId((Integer) obj[3]);
					superAdminsList.add(admin);
				}
			}
		}catch(Exception e){
			LOGGER.error("AppMetaDataDao - superAdminsList() :: ERROR", e);
		}finally{
			if(session != null){
				session.close();
			}
		}
		LOGGER.info("INFO: AppMetaDataDao - superAdminsList() :: Ends");
		return superAdminsList;
	}
}
