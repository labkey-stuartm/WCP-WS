package com.studymetadata.web.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class StudyMetaDataController extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(StudyMetaDataController.class);
	private static final long serialVersionUID = 1L;
	private String port = "";
	private String forwardURL = "";

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		LOGGER.info("INFO: StudyMetaDataController - init() :: Starts");
		super.init(servletConfig);
		LOGGER.info("INFO: StudyMetaDataController - init() :: Ends");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("INFO: StudyMetaDataController - doGet() :: Starts");
		doPost(req, resp);
		LOGGER.info("INFO: StudyMetaDataController - doGet() :: Ends");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("INFO: StudyMetaDataController - doPost() :: Starts");
		String jsonp_callback = null;
		String localForwardURL = forwardURL;
		URL URL = new URL(localForwardURL);
		HttpURLConnection urlConnection = (HttpURLConnection) URL.openConnection();
		BufferedInputStream buffer = new BufferedInputStream(urlConnection.getInputStream());
		StringBuilder builder = new StringBuilder();
		int byteRead;
		try {
			jsonp_callback = (String) req.getSession().getServletContext().getAttribute("jsonp.callback");
		} catch (Exception e) {
			LOGGER.error("StudyMetaDataController - doPost() :: ERROR ==> jsonp_callback key is missing... ");
			e.printStackTrace();
		}
		
		try {
			localForwardURL += ":"+new Integer(port).toString() + req.getContextPath() + req.getPathInfo() + "?" + req.getQueryString();
		} catch (NumberFormatException e) {
			localForwardURL += req.getContextPath() + req.getPathInfo() + "?" + req.getQueryString();
		}
		
		while ((byteRead = buffer.read()) != -1){
			builder.append((char) byteRead);
		}
		buffer.close();
		LOGGER.info("INFO: StudyMetaDataController - doPost() :: HttpURLConnection.response = "+builder);
		if (req.getParameter(jsonp_callback) != null) {
			if (req.getPathInfo().indexOf("/json") != -1) {
				resp.getWriter().write( req.getParameter(jsonp_callback) + "(" + builder.toString() + ")");
			} else {
				resp.getWriter().write(builder.toString());
			}
		} else {
			resp.getWriter().write(builder.toString());
		}
		LOGGER.info("INFO: StudyMetaDataController - doPost() :: Ends ");
	}
}
