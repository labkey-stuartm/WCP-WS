package com.studymetadata.util;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	
	private static Logger logger = Logger.getLogger(HibernateUtil.class);
	
	private static Object obj = new Object();
	private static SessionFactory sessionFactory = null;

	public static SessionFactory getSessionFactory() {
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Starts");
		try {
			synchronized (obj) {
				if(sessionFactory != null && !sessionFactory.isClosed()){
					sessionFactory.close();
				}
				/*if(null == sessionFactory){*/
					sessionFactory = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
				/*}*/
			}
		} catch (Throwable e) {
			logger.error("HibernateUtil - getSessionFactory() :: ERROR ", e);
			e.printStackTrace();
		}
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Ends");
		return sessionFactory;
	}

	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory = sessionFactory;
	}
}
