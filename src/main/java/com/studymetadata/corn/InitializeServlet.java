package com.studymetadata.corn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

public class InitializeServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(InitializeServlet.class);
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	public void init() throws ServletException {
		LOGGER.info("INFO: InitializeServlet - init() :: Starts");
		try {
			System.out.println("Initializing NewsLetter PlugIn");
			CronScheluder objPlugin = new CronScheluder();
		}catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("InitializeServlet - init() - ERROR :: ", e);
		}
		LOGGER.info("INFO: InitializeServlet - init() :: Ends");
	}
}
