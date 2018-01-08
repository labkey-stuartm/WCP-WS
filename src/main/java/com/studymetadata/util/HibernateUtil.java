package com.studymetadata.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Provides hibernate configuration details to get session factory.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:37:36 PM
 *
 */
public class HibernateUtil {

	private static Logger logger = Logger.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory = null;

	/**
	 * 
	 */
	private HibernateUtil() {
		super();
	}

	/**
	 * 
	 * @return
	 */
	public static SessionFactory getSessionFactory() {
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Starts");
		try {
			if (sessionFactory == null) {
				Properties properties = new Properties();
				properties.load(new FileInputStream(ServletContextHolder
						.getServletContext().getInitParameter(
								"property_file_location_path")));
				sessionFactory = new AnnotationConfiguration()
						.addProperties(properties)
						.configure("hibernate.cfg.xml").buildSessionFactory();
			}
		} catch (Exception e) {
			logger.error("HibernateUtil - getSessionFactory() :: ERROR ", e);
		}
		logger.info("INFO: HibernateUtil - getSessionFactory() :: Ends");
		return sessionFactory;
	}

	/**
	 * 
	 * @param sessionFactory
	 */
	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory = sessionFactory;
	}
}
