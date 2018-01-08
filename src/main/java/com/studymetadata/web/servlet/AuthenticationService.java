package com.studymetadata.web.servlet;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.util.StudyMetaDataUtil;
import com.sun.jersey.core.util.Base64;

/**
 * Provides authentication to check requesting user is authorized to access
 * data.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:49:26 PM
 *
 */
public class AuthenticationService {

	public static final Logger LOGGER = Logger
			.getLogger(AuthenticationService.class);

	@SuppressWarnings("unchecked")
	HashMap<String, String> authPropMap = StudyMetaDataUtil
			.getAuthorizationProperties();

	/**
	 * Authenticate the provided authorization credentials
	 * 
	 * @author BTC
	 * @param authCredentials
	 *            the Basic Authorization
	 * @return {@link Boolean}
	 */
	public boolean authenticate(String authCredentials) {
		LOGGER.info("INFO: AuthenticationService - authenticate() - Starts");
		boolean authenticationStatus = false;
		String bundleIdAndAppToken = null;
		try {
			if (StringUtils.isNotEmpty(authCredentials)
					&& authCredentials.contains("Basic")) {
				final String encodedUserPassword = authCredentials
						.replaceFirst("Basic" + " ", "");
				byte[] decodedBytes = Base64.decode(encodedUserPassword);
				bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
				if (bundleIdAndAppToken.contains(":")) {
					final StringTokenizer tokenizer = new StringTokenizer(
							bundleIdAndAppToken, ":");
					final String bundleId = tokenizer.nextToken();
					final String appToken = tokenizer.nextToken();
					if (authPropMap.containsKey(bundleId)
							&& authPropMap.containsKey(appToken)) {
						authenticationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("AuthenticationService - authenticate() :: ERROR", e);
			return authenticationStatus;
		}
		LOGGER.info("INFO: AuthenticationService - authenticate() - Ends");
		return authenticationStatus;
	}
}
