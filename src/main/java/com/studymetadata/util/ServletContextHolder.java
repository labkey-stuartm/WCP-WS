package com.studymetadata.util;

import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextHolder implements ServletContextListener {
	private static ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		setServletContext(sce.getServletContext());
		//TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext) {
		ServletContextHolder.servletContext = servletContext;
	}
	
}
