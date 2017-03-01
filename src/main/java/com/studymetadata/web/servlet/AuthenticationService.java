package com.studymetadata.web.servlet;


import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.util.StudyMetaDataConstants;
import com.sun.jersey.core.util.Base64;

public class AuthenticationService {
	
	public static final Logger logger = Logger.getLogger(AuthenticationService.class);
	
	public boolean authenticate(String authCredentials) {
		logger.info("INFO: AuthenticationService - authenticate() - Starts");
		boolean authenticationStatus = false;
		String bundleIdAndAppToken = null;
		try{
			if(StringUtils.isNotEmpty(authCredentials)){
				// header value format will be "Basic encodedstring" for Basic
				// authentication. Example "Basic YWRtaW46YWRtaW4="
				if(authCredentials.contains("Basic")){
					final String encodedUserPassword = authCredentials.replaceFirst("Basic"+ " ", "");
					byte[] decodedBytes = Base64.decode(encodedUserPassword);
					bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
					if(bundleIdAndAppToken.contains(":")){
						final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
						final String bundleId = tokenizer.nextToken();
						final String appToken = tokenizer.nextToken();
						if((bundleId.equals(StudyMetaDataConstants.ANDROID_BUNDLE_ID) && appToken.equals(StudyMetaDataConstants.ANDROID_APP_TOKEN)) || (bundleId.equals(StudyMetaDataConstants.IOS_BUNDLE_ID) && appToken.equals(StudyMetaDataConstants.IOS_APP_TOKEN))){
							authenticationStatus = true;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("AuthenticationService - authenticate() :: ERROR", e);
			return authenticationStatus;
		}
		logger.info("INFO: AuthenticationService - authenticate() - Ends");
		return authenticationStatus;
	}
}
