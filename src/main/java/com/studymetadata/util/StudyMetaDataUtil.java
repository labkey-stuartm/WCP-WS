package com.studymetadata.util;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.studymetadata.bean.FailureResponse;
import com.sun.jersey.core.util.Base64;

public class StudyMetaDataUtil {
	private static Logger logger = Logger.getLogger(StudyMetaDataUtil.class.getName());

	/* Read Properties file */
	@SuppressWarnings("rawtypes")
	protected static final  HashMap configMap = StudyMetaDataUtil.getAppProperties();
	
//	@SuppressWarnings("unchecked")	
//	private static final  HashMap<String, String> propMap = StudyMetaDataUtil.configMap;

	//Authorization properties file
	@SuppressWarnings("rawtypes")
	protected static final HashMap authConfigMap = StudyMetaDataUtil.getAuthorizationProperties();
		
	@SuppressWarnings("unchecked")
	private static final HashMap<String, String> authPropMap = StudyMetaDataUtil.authConfigMap;


	/**
	 * @return HashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap getAppProperties(){
		HashMap hm = new HashMap<String, String>();
		logger.warn("StudyMetaDataUtil - getAppProperties() :: Properties Initialization");
		Enumeration<String> keys = null;
		Enumeration<Object> objectKeys = null;
		try {
			ResourceBundle rb = ResourceBundle.getBundle("messageResource");
			keys = rb.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = rb.getString(key);
				hm.put(key, value);
			}
			ServletContext context = ServletContextHolder.getServletContext();
			Properties prop = new Properties();
			prop.load(new FileInputStream(context.getInitParameter("property_file_location_path")));
			objectKeys = prop.keys();
			while (objectKeys.hasMoreElements()) {
				String key = (String) objectKeys.nextElement();
				String value = prop.getProperty(key);
				hm.put(key, value);
			}
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getAppProperties() - ERROR " , e);
		} 
		return hm;
	}

	public static FailureResponse getFailureResponse(String status, String title, String detail){
		logger.info("INFO: StudyMetaDataUtil - getFailureResponse() :: Starts");
		FailureResponse failureResponse = new FailureResponse();
		try {
			failureResponse.setResultType(StudyMetaDataConstants.FAILURE);
			failureResponse.getErrors().setStatus(status);
			failureResponse.getErrors().setTitle(title);
			failureResponse.getErrors().setDetail(detail);
		} catch (Exception e) {
			logger.error("ERROR: StudyMetaDataUtil - getFailureResponse() ",e);
		}
		logger.info("INFO: StudyMetaDataUtil - getFailureResponse() :: Ends");
		return failureResponse;
	}

	public static void getFailureResponse(String status, String title, String detail, HttpServletResponse response){
		logger.info("INFO: StudyMetaDataUtil - getFailureResponse() :: Starts");
		try {
			response.setHeader("status", status); 
			response.setHeader("title", title);
			response.setHeader("StatusMessage", detail);
		} catch (Exception e) {
			logger.error("ERROR: StudyMetaDataUtil - getFailureResponse() ",e);
		}
		logger.info("INFO: StudyMetaDataUtil - getFailureResponse() :: Ends");
	}

	public static int noOfDaysForMonthYear(int month, int year) {
		logger.info("INFO: StudyMetaDataUtil - noOfDaysForMonthYear() :: Starts");
		int numDays = 30;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month-1);
			numDays = calendar.getActualMaximum(Calendar.DATE);
		} catch (Exception e) {
			logger.error("ERROR: StudyMetaDataUtil - noOfDaysForMonthYear() " + e);
		}
		logger.info("INFO: StudyMetaDataUtil - noOfDaysForMonthYear() :: Ends");
		return numDays;
	}

	public static int noOfDaysBetweenTwoDates(String startDate, String endDate) {
		logger.info("INFO: StudyMetaDataUtil - noOfDaysBetweenTwoDates() :: Starts");
		int daysdiff=0;
		try {
			long diff = StudyMetaDataConstants.SDF_DATE.parse(endDate).getTime() - StudyMetaDataConstants.SDF_DATE.parse(startDate).getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000)+1;
			daysdiff = (int) diffDays;
		} catch (Exception e) {
			logger.error("ERROR: StudyMetaDataUtil - noOfDaysBetweenTwoDates() " + e);
		}
		logger.info("INFO: StudyMetaDataUtil - noOfDaysBetweenTwoDates() :: Ends");
		return daysdiff;
	}

	public static String getCurrentDate() {
		logger.info("INFO: StudyMetaDataUtil - getCurrentDate() :: Starts");
		String dateNow = "";
		try {
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			dateNow = formatter.format(currentDate.getTime());
		} catch (Exception e) {
			logger.error("ERROR: StudyMetaDataUtil - getCurrentDate() " + e);
		}
		logger.info("INFO: StudyMetaDataUtil - getCurrentDate() :: Ends");
		return dateNow;
	}

	public static String getCurrentDateTime() {
		logger.info("StudyMetaDataUtil: getCurrentDateTime() - Starts ");
		String getToday = "";
		try {
			Date today = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			getToday = formatter.format(today.getTime());
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getCurrentDateTime() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getCurrentDateTime() - Ends ");
		return getToday;
	}

	public static String getFormattedDate1(String inputDate, String inputFormat, String outputFormat) {
		logger.info("StudyMetaDataUtil: getFormattedDate1() - Starts ");
		String finalDate = "";
		java.sql.Date formattedDate = null; 
		if (inputDate != null && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)){
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
				formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());

				formatter = new SimpleDateFormat(outputFormat);
				finalDate = formatter.format(formattedDate);
			} catch (Exception e){
				logger.error("StudyMetaDataUtil: getFormattedDate1() - ERROR",e);
			}
		}
		logger.info("StudyMetaDataUtil: getFormattedDate1() - Ends ");
		return finalDate;
	}

	public static String getTimeDiffInDaysHoursMins(Date dateOne, Date dateTwo) {
		logger.info("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Starts ");
		String diff = "";
		try {
			long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
			diff = String.format("%d Day(s) %d hour(s) %d min(s)", TimeUnit.MILLISECONDS.toDays(timeDiff), TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeDiff)), 
					TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getTimeDiffInDaysHoursMins() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Ends ");
		return diff;
	}

	public static String getTimeDiffInDaysHoursMins(String dateOne1, String dateTwo2) {
		logger.info("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Starts ");
		String diff = "";
		try{
			Date dateOne = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateOne1);
			Date dateTwo = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateTwo2);
			long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
			diff = String.format("%d",TimeUnit.MILLISECONDS.toMinutes(timeDiff));
		}catch(ParseException e){
			logger.error("StudyMetaDataUtil - getTimeDiffInDaysHoursMins() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Ends ");
		return diff;
	}

	public static String getEncodedStringByBase64(String plainText) {
		logger.info("StudyMetaDataUtil: getEncodedStringByBase64() - Starts ");
		if(StringUtils.isEmpty(plainText)){return "";}
		try {
			// encrypt data on your side using BASE64
			byte[]   bytesEncoded = Base64.encode(plainText.getBytes());
			return new String(bytesEncoded);
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getEncodedStringByBase64() - ERROR " , e);

		}
		logger.info("StudyMetaDataUtil: getEncodedStringByBase64() - Ends ");
		return "";
	}
	public static String getDecodedStringByBase64(String encodedText) {
		logger.info("StudyMetaDataUtil: getDecodedStringByBase64() - Starts ");
		if(StringUtils.isEmpty(encodedText)){return "";}
		try {
			// Decrypt data on other side, by processing encoded data
			byte[] valueDecoded= Base64.decode(encodedText );
			return  new String(valueDecoded);

		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getDecodedStringByBase64() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getDecodedStringByBase64() - Ends ");
		return "";

	}

	public static String getEncryptedString(String input) {
		logger.info("INFO: getEncryptedString :: Starts");
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotEmpty(input)){
			/** Add the password salt to input parameter */
			input = input + StudyMetaDataConstants.PASS_SALT;
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
				messageDigest.update(input.getBytes("UTF-8"));
				byte[] digestBytes = messageDigest.digest();
				String hex = null;
				for (int i = 0; i < 8; i++) {
					hex = Integer.toHexString(0xFF & digestBytes[i]);
					if (hex.length() < 2)
						sb.append("0");
					sb.append(hex);
				}
			}
			catch (Exception e) {
				logger.error("ERROR: getEncryptedString ",e);
			}
		}
		logger.info("INFO: getEncryptedString :: Ends");
		return sb.toString();
	}


	public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
		logger.info("StudyMetaDataUtil: getFormattedDate() - Starts ");
		String finalDate = "";
		java.sql.Date formattedDate = null; 
		if (inputDate != null && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)){
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
				formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());

				formatter = new SimpleDateFormat(outputFormat);
				finalDate = formatter.format(formattedDate);
			} catch (Exception e){
				logger.error("ERROR: getFormattedDate ",e);
			}
		}
		logger.info("StudyMetaDataUtil: getFormattedDate() - Ends ");
		return finalDate;
	}

	public static String getTimeFromSec(String secs){
		logger.info("StudyMetaDataUtil: getTimeFromSec() - Starts ");
		String time ="";
		int hours = Integer.parseInt(secs) / 3600,
				remainder = Integer.parseInt(secs) % 3600,
				minutes = remainder / 60,
				seconds = remainder % 60;

		String disHour = (hours < 10 ? "0" : "") + hours,
				disMinu = (minutes < 10 ? "0" : "") + minutes ,
				disSec = (seconds < 10 ? "0" : "") + seconds ; 
		System.out.println(disHour +":"+ disMinu+":"+disSec); 
		time = disHour +":"+ disMinu+":"+disSec;
		logger.info("StudyMetaDataUtil: getTimeFromSec() - End ");
		return time;
	}

	public static String addMinutes(String dtStr, int minutes) {
		logger.info("StudyMetaDataUtil: addMinutes() - Starts ");
		String newdateStr = "";
		try {
			SimpleDateFormat date = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
			Date dt = date.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MINUTE, minutes);
			Date newDate = cal.getTime();
			newdateStr = date.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addMinutes() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addMinutes() - Ends ");
		return newdateStr; 
	}

	public static String addDays(String dtStr, int days) {
		logger.info("StudyMetaDataUtil: addDays() - Starts ");
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE_TIME.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DATE, days);
			Date newDate = cal.getTime();
			newdateStr = StudyMetaDataConstants.SDF_DATE_TIME.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addDays() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addDays() - Ends ");
		return newdateStr; 
	}

	public static String addMonth(String dtStr, int months) {
		logger.info("StudyMetaDataUtil: addMonth() - Starts ");
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MONTH, months);
			Date newDate = cal.getTime();
			newdateStr =StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addMonth() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addMonth() - Ends ");
		return newdateStr;

	}

	public static String addYear(String dtStr, int years) {
		logger.info("StudyMetaDataUtil: addYear() - Starts ");
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.YEAR, years);
			Date newDate = cal.getTime();
			newdateStr = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addYear() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addYear() - Ends ");
		return newdateStr ;
	}

	public static Long getDateToSeconds(String getCurrentDate) {
		logger.info("StudyMetaDataUtil: getDateToSeconds() - Starts ");
		Long getInSeconds = null;
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(getCurrentDate);
			getInSeconds=dt.getTime();
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - getDateToSeconds() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getDateToSeconds() - Ends ");
		return getInSeconds;
	}


	public static String getSecondsToDate(String value) {
		logger.info("StudyMetaDataUtil: getSecondsToDate() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue);
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");// yyyy-MM-dd
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDate() - Ends ");
		return dateText;
	}

	public static String getToDate(String value) {
		logger.info("StudyMetaDataUtil: getToDate() - Starts ");
		String dateText;
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");// yyyy-MM-dd
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(value);
		logger.info("StudyMetaDataUtil: getToDate() - Ends ");
		return dateText;
	}


	public static Long getDateAndTimeToSeconds(String getCurrentDateTime) {
		logger.info("StudyMetaDataUtil: getDateAndTimeToSeconds() - Starts ");
		Long getInSeconds = null;
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE_TIME.parse(getCurrentDateTime);
			getInSeconds=dt.getTime();
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - getDateAndTimeToSeconds() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getDateAndTimeToSeconds() - Ends ");
		return getInSeconds;
	}


	public static String getSecondsToDateandTime(String value) {
		logger.info("StudyMetaDataUtil: getSecondsToDateandTime() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue);
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// yyyy-MM-dd HH:mm:ss
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDateandTime() - Ends ");
		return dateText;
	}

	public static String getSecondsToDateTimeMin(String value) {
		logger.info("StudyMetaDataUtil: getSecondsToDateTimeMin() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue* 1000);
		SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");// yyyy-MM-dd HH:mm:ss
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDateTimeMin() - Ends ");
		return dateText;
	}

	public static String getSecondsToDateForm(String value) {
		logger.info("StudyMetaDataUtil: getSecondsToDateForm() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue* 1000);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");// yyyy-MM-dd HH:mm:ss
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDateForm() - Ends ");
		return dateText;
	}

	public static String getCurrentDateTimeInUTC() {
		logger.info("StudyMetaDataUtil: getCurrentDateTimeInUTC() - Starts ");
		String dateNow = null;
		final SimpleDateFormat sdf = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
		String timeZone = "UTC";
		try {
			String strDate = new Date() + "";
			if(strDate.indexOf("IST") != -1){
				timeZone = "IST";
			}
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			dateNow = sdf.format(new Date());
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil: getCurrentDateTimeInUTC(): ERROR " + e);
		}
		logger.info("StudyMetaDataUtil: getCurrentDateTimeInUTC() - Ends ");
		return dateNow;
	}

	/*---------------------------------------------FDA util methods------------------------------------------*/
	/*-----------------------------------------FDA WCP WS related methods starts-----------------------------------------*/
	public static String platformType(String authCredentials, String type) {
		logger.info("INFO: StudyMetaDataUtil - platformType() - Starts");
		String bundleIdAndAppToken = null;
		String platform = "";
		try{
			if(StringUtils.isNotEmpty(authCredentials)){
				if(authCredentials.contains("Basic")){
					final String encodedUserPassword = authCredentials.replaceFirst("Basic"+ " ", "");
					byte[] decodedBytes = Base64.decode(encodedUserPassword);
					bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
					if(bundleIdAndAppToken.contains(":")){
						final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
						final String bundleId = tokenizer.nextToken();
						final String appToken = tokenizer.nextToken();
						if(authPropMap.containsKey(bundleId) && authPropMap.containsKey(appToken)){
							String appBundleId = "";
							String appTokenId = "";
							for(Map.Entry<String, String> map : authPropMap.entrySet()){
								if(map.getKey().equals(appToken)){
									appTokenId = map.getValue();
								}
								
								if(map.getKey().equals(bundleId)){
									appBundleId = map.getValue();
								}
							}
							
							if(StringUtils.isNotEmpty(appBundleId) && StringUtils.isNotEmpty(appTokenId)){
								final StringTokenizer authTokenizer = new StringTokenizer(appTokenId, ".");
								final String platformType = authTokenizer.nextToken();
								/*final String key = authTokenizer.nextToken();*/
								if(platformType.equals(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)){
									switch (type) {
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM: platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS: platform = StudyMetaDataConstants.STUDY_PLATFORM_ANDROID;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID: platform = bundleId;
											break;
									}
								}else{
									switch (type) {
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM: platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS: platform = StudyMetaDataConstants.STUDY_PLATFORM_IOS;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID: platform = bundleId;
											break;
									}
								}
							}
							
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("StudyMetaDataUtil - platformType() :: ERROR", e);
		}
		logger.info("INFO: StudyMetaDataUtil - platformType() - Ends");
		return platform;
	}

	/**
	 * @author Mohan
	 * @return HashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap getAuthorizationProperties(){
		logger.info("INFO: StudyMetaDataUtil - getAuthorizationProperties() :: Starts");
		HashMap hashMap = new HashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle("authorizationResource");
		Enumeration<String> keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = rb.getString(key);
			hashMap.put(key, value);
		}
		logger.info("INFO: StudyMetaDataUtil - getAuthorizationProperties() :: Ends");
		return hashMap;
	}

	public static String getDayByDate(String input){
		logger.info("StudyMetaDataUtil: getDayByDate() - Starts ");
		String actualDay = "";
		try {
			if(StringUtils.isNotEmpty(input)){
				SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date MyDate = newDateFormat.parse(input);
				newDateFormat.applyPattern(StudyMetaDataConstants.SDF_DAY);
				actualDay = newDateFormat.format(MyDate);
			}
		} catch (Exception e) {
			logger.error("StudyMetaDataUtil - getDayByDate() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: getDayByDate() - Ends ");
		return actualDay;
	}

	public static String addDaysToDate(String input, int days){
		logger.info("StudyMetaDataUtil: addDaysToDate() - Starts ");
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DATE, days);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addDaysToDate() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addDaysToDate() - Ends ");
		return output;
	}

	public static String addWeeksToDate(String input, int weeks){
		logger.info("StudyMetaDataUtil: addWeeksToDate() - Starts ");
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.WEEK_OF_MONTH, weeks);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addWeeksToDate() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addWeeksToDate() - Ends ");
		return output;
	}

	public static String addMonthsToDate(String input, int months){
		logger.info("StudyMetaDataUtil: addMonthsToDate() - Starts ");
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MONTH, months);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			logger.error("StudyMetaDataUtil - addMonthsToDate() - ERROR " , e);
		}
		logger.info("StudyMetaDataUtil: addMonthsToDate() - Ends ");
		return output;
	}

	public static String getFormattedDateTimeZone(String input, String inputFormat, String outputFormat){
		logger.info("StudyMetaDataUtil: getFormattedDateTimeZone() - Starts ");
		String output = "";
		try{
			if(StringUtils.isNotEmpty(input)){
				SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
				Date inputDate = inputSDF.parse(input);
				SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat); //yyyy-MM-dd'T'hh:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.SSSZ
				//outputSDF.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				//outputSDF.setTimeZone(TimeZone.getDefault()); //TimeZone.getTimeZone("CST")
				output = outputSDF.format(inputDate);
			}
		}catch(Exception e){
			logger.error("AuthenticationService - getFormattedDateTimeZone() :: ERROR", e);
		}
		logger.info("StudyMetaDataUtil: getFormattedDateTimeZone() - Ends ");
		return output;
	}
	
	public static String addSeconds(String dtStr, int seconds) {
		logger.info("StudyMetaDataUtil: addSeconds() - Starts ");
		String newdateStr = "";
		try {
			SimpleDateFormat date = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
			Date dt = date.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.SECOND, seconds);
			Date newDate = cal.getTime();
			newdateStr = date.format(newDate);
		} catch (ParseException e) {
			logger.error("AuthenticationService - addSeconds() :: ERROR", e);
		}
		logger.info("StudyMetaDataUtil: addSeconds() - Ends ");
		return newdateStr;
	}
	
	public static String getMilliSecondsForImagePath() {
		logger.info("StudyMetaDataUtil: getMilliSecondsForImagePath() - Starts ");
		String milliSeconds;
		Calendar cal = Calendar.getInstance();
		milliSeconds = "?v="+cal.getTimeInMillis();
		logger.info("StudyMetaDataUtil: getMilliSecondsForImagePath() - Ends ");
		return milliSeconds;
	}
	
	public static String getBundleIdFromAuthorization(String authCredentials) {
		logger.info("INFO: StudyMetaDataUtil - getBundleIdFromAuthorization() - Starts");
		String bundleIdAndAppToken = null;
		String appBundleId = "";
		try{
			if(StringUtils.isNotEmpty(authCredentials)){
				if(authCredentials.contains("Basic")){
					final String encodedUserPassword = authCredentials.replaceFirst("Basic"+ " ", "");
					byte[] decodedBytes = Base64.decode(encodedUserPassword);
					bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
					if(bundleIdAndAppToken.contains(":")){
						final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
						final String bundleId = tokenizer.nextToken();
						final String appToken = tokenizer.nextToken();
						if(authPropMap.containsKey(bundleId) && authPropMap.containsKey(appToken)){
							appBundleId = bundleId;
						}
						
					}
				}
			}
		}catch(Exception e){
			logger.error("StudyMetaDataUtil - getBundleIdFromAuthorization() :: ERROR", e);
		}
		logger.info("INFO: StudyMetaDataUtil - getBundleIdFromAuthorization() - Ends");
		return appBundleId;
	}
	
	/**
	 * This method is used to replace all the single quotes with the escape characters
	 * 
	 * @author Mohan
	 * @param activityId
	 * @return
	 */
	public static String replaceSingleQuotes(String activityId){
		logger.info("INFO: StudyMetaDataUtil - replaceSingleQuotes() - Starts");
		if(activityId.indexOf("'") > -1){
			activityId = activityId.replaceAll("'", "''");
		}
		logger.info("INFO: StudyMetaDataUtil - replaceSingleQuotes() - Ends");
		return activityId;
	} 
	/*-----------------------------------------FDA WCP WS related methods ends-----------------------------------------*/
}