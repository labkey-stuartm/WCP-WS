package com.studymetadata.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.exception.ErrorCodes;
import com.studymetadata.util.StudyMetaDataConstants;

public class RestAuthenticationFilter implements Filter {

	public static final Logger logger = Logger.getLogger(RestAuthenticationFilter.class);
	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
		logger.info("INFO: RestAuthenticationFilter - doFilter() - Starts");
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
			String pingPath = "/ping";
			if(StringUtils.isNotEmpty(authCredentials)){
				AuthenticationService authenticationService = new AuthenticationService();
				boolean authenticationStatus = authenticationService.authenticate(authCredentials);
				if (authenticationStatus) {
					filter.doFilter(request, response);
				} else {
					if (response instanceof HttpServletResponse) {
						HttpServletResponse httpServletResponse = (HttpServletResponse) response;
						httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						httpServletResponse.setHeader("status", ErrorCodes.STATUS_103);
						httpServletResponse.setHeader("title", ErrorCodes.INVALID_AUTHORIZATION);
						httpServletResponse.setHeader("StatusMessage", StudyMetaDataConstants.INVALID_AUTHORIZATION);
					}
				}
			}else{
				//to ping the Web Services
				if(pingPath.equalsIgnoreCase(httpServletRequest.getPathInfo())){
					filter.doFilter(request, response);
				}else{
					HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					httpServletResponse.setHeader("status", ErrorCodes.STATUS_102);
					httpServletResponse.setHeader("title", ErrorCodes.INVALID_INPUT);
					httpServletResponse.setHeader("StatusMessage", StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG);
				}
			}
		}
		logger.info("INFO: RestAuthenticationFilter - doFilter() - Ends");
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
