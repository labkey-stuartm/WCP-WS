package com.studymetadata.web.servlet;


import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.util.StudyMetaDataConstants;
import com.studymetadata.util.StudyMetaDataUtil;
import com.sun.jersey.core.util.Base64;

public class AuthenticationService {
	
	public static final Logger logger = Logger.getLogger(AuthenticationService.class);
	
	@SuppressWarnings("unchecked")
	HashMap<String, String> authPropMap = StudyMetaDataUtil.authConfigMap;
	
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
						if((bundleId.equals(authPropMap.get("android.bundle.id.pregnent.women")) && appToken.equals(authPropMap.get("android.app.token.pregnent.women"))) || (bundleId.equals(authPropMap.get("ios.bundle.id.pregnent.women")) && appToken.equals(authPropMap.get("ios.app.token.pregnent.women")))){
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
