package com.studymetadata.web.servlet;


import java.util.HashMap;
import java.util.Map;
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
						if(authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)){
							authenticationStatus = true;
						}
						/*if((bundleId.equals(authPropMap.get("android.bundleid")) && appToken.equals(authPropMap.get("android.apptoken"))) || (bundleId.equals(authPropMap.get("ios.bundleid")) && appToken.equals(authPropMap.get("ios.apptoken"))) || (bundleId.equals(authPropMap.get("labkey.bundleid")) && appToken.equals(authPropMap.get("labkey.apptoken")))){
							authenticationStatus = true;
						}*/
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
