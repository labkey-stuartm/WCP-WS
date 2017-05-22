package com.studymetadata.util;

import java.io.FileInputStream;
import java.util.Properties;

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
					sessionFactory = null;
				}
				
				//get the DB Config details from external property file
				Properties properties = new Properties();
				properties.load(new FileInputStream(ServletContextHolder.getServletContext().getInitParameter("property_file_location_path")));
				sessionFactory = new AnnotationConfiguration().addProperties(properties).configure("hibernate.cfg.xml").buildSessionFactory();
			}
		} catch (Throwable e) {
			logger.error("HibernateUtil - getSessionFactory() :: ERROR ", e);
		}
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Ends");
		return sessionFactory;
	}

	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory = sessionFactory;
	}
}
