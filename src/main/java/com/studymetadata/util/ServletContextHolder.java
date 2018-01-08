package com.studymetadata.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Implements {@link ServletContextListener} interface to provide
 * {@link ServletContext} configuration for context initialization and to
 * destroy.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:39:03 PM
 *
 */
public class ServletContextHolder implements ServletContextListener {

	private static ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		setServletContext(sce.getServletContext());
		HibernateUtil.getSessionFactory();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		HibernateUtil.getSessionFactory().close();
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext) {
		ServletContextHolder.servletContext = servletContext;
	}

}
