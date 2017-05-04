package com.studymetadata.util;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
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
	public static HashMap configMap = StudyMetaDataUtil.getAppProperties();
	@SuppressWarnings("unchecked")	
	private static HashMap<String, String> propMap = StudyMetaDataUtil.configMap;

	//Authorization properties file
	@SuppressWarnings("rawtypes")
	public static HashMap authConfigMap = StudyMetaDataUtil.getAuthorizationProperties();
	@SuppressWarnings("unchecked")	
	private static HashMap<String, String> authPropMap = StudyMetaDataUtil.authConfigMap;


	/**
	 * @return HashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap getAppProperties(){
		HashMap hm = new HashMap<String, String>();
		logger.warn("fdahpStudyDesignerUtil - getAppProperties() :: Properties Initialization");
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
			logger.error("fdahpStudyDesignerUtil - getAppProperties() - ERROR " , e);
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

	public static boolean isEmpty(String str) {
		logger.info("INFO: StudyMetaDataUtil - isEmpty() :: Starts");
		boolean flag = false;
		if(null == str || (null != str && "".equals(str.trim()))){
			flag = true;
		}
		logger.info("INFO: StudyMetaDataUtil - isEmpty() :: Ends");
		return flag;
	}

	public static boolean isNotEmpty(String str) {
		logger.info("INFO: StudyMetaDataUtil - isNotEmpty() :: Starts");
		boolean flag = false;
		if(null != str && !"".equals(str.trim())){
			flag = true;
		}
		logger.info("INFO: StudyMetaDataUtil - isNotEmpty() :: Ends");
		return flag;
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



	/* Get Current Date */
	/*public static String getCurrentDate() {
		Calendar currentDate = Calendar.getInstance();
		String dateNow = commonMavenWSConstants.SDF_DATE.format(currentDate.getTime());
		return dateNow;
	}*/

	public static String getCurrentDate() {
		Calendar currentDate = Calendar.getInstance();
		/*String dateNow = AcuityLinkConstants.DB_SDF_DATE.format(currentDate.getTime());*/
		//SimpleDateFormat formatter = AcuityLinkConstants.DB_SDF_DATE;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	public static String getCurrentDateTime() {
		String getToday = "";
		try {
			Date today = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			getToday = formatter.format(today.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getToday;
	}

	public static String getFormattedDate1(String inputDate, String inputFormat, String outputFormat) {
		String finalDate = "";
		java.sql.Date formattedDate = null; 
		if (inputDate != null && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)){
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
				formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());

				formatter = new SimpleDateFormat(outputFormat);
				finalDate = formatter.format(formattedDate);
			} catch (Exception e){
				logger.error("ERROR: getFormattedDate() ",e);
			}
		}
		return finalDate;
	}

	public static String getDateDiffernce(String date1,String date2,boolean liveStreamCreated){
		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		String statusFlag = "0";
		try {
			d1 = format.parse(date1);
			d2 = format.parse(date2);

			//in milliseconds
			long diff =0;
			if(d1.after(d2)){
				System.out.println("Date1 is after Date2");
				diff = d1.getTime() - d2.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);

				if(diffDays == 0){
					if(diffHours ==0){
						if(liveStreamCreated == false){
							if(diffMinutes <=30 && diffMinutes>=3){
								statusFlag = "3";//no
							}else 
								if(diffMinutes <=2 && diffMinutes>=0){
									statusFlag = "1";//yes
								}
						}else{
							if(diffMinutes <=2 && diffMinutes>=0){
								statusFlag = "1";//yes
							}
						}

					}

				}
			}
			// before() will return true if and only if date1 is before date2
			if(d1.before(d2)){
				System.out.println("Date1 is before Date2");
				diff = d2.getTime() - d1.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);

				if(diffDays == 0){
					if(diffHours ==0){
						if(diffMinutes <=30 && diffMinutes>=0){
							statusFlag = "2";// yes
						}
					}

				}
			}

			//equals() returns true if both the dates are equal
			if(d1.equals(d2)){
				System.out.println("Date1 is equal Date2");

				diff = d1.getTime() - d2.getTime();
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);

				if(diffDays == 0){
					if(diffHours ==0){
						if(diffMinutes <= 2 && diffMinutes>=0){
							statusFlag = "1";
						}else{


						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusFlag;
	}

	public static String getScheduler(String date1,String date2){
		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		String statusFlag = "NO";
		try {
			d1 = format.parse(date1);
			d2 = format.parse(date2);

			//in milliseconds
			long diff =0;
			if(d1.after(d2)){
				/**/
			}
			// before() will return true if and only if date1 is before date2
			if(d1.before(d2)){
				System.out.println("Date1 is before Date2");
				diff = d2.getTime() - d1.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);

				if(diffDays == 0){
					if(diffHours ==0){
						if(diffMinutes <=30 && diffMinutes>=0){

						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusFlag;
	}


	public static String getTimeDiffInDaysHoursMins(Date dateOne, Date dateTwo) {
		String diff = "";
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		diff = String.format("%d Day(s) %d hour(s) %d min(s)", TimeUnit.MILLISECONDS.toDays(timeDiff), TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeDiff)), 
				TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		return diff;
	}

	public static String getTimeDiffInDaysHoursMins(String dateOne1, String dateTwo2) {
		String diff = "";
		try{
			Date dateOne = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateOne1);
			Date dateTwo = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateTwo2);
			long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
			diff = String.format("%d",TimeUnit.MILLISECONDS.toMinutes(timeDiff));
			/*diff = String.format("%d", TimeUnit.MILLISECONDS.toDays(timeDiff), TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeDiff)), 
					TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));*/
		}catch(ParseException e){
			e.printStackTrace();
		}
		return diff;
	}

	public static String getEncodedStringByBase64(String plainText) {
		if(StringUtils.isEmpty(plainText)){return "";}
		try {
			// encrypt data on your side using BASE64
			byte[]   bytesEncoded = Base64.encode(plainText.getBytes());
			return new String(bytesEncoded);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return "";
	}
	public static String getDecodedStringByBase64(String encodedText) {
		if(StringUtils.isEmpty(encodedText)){return "";}
		try {
			// Decrypt data on other side, by processing encoded data
			byte[] valueDecoded= Base64.decode(encodedText );
			return  new String(valueDecoded);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String getEncryptedString(String input) {
		StringBuffer sb = new StringBuffer();
		logger.info("INFO: getEncodedString :: Starts");
		if(StringUtils.isNotEmpty(input)){
			/** Add the password salt to input parameter */
			input = input + StudyMetaDataConstants.PASSWORD_SALT;
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
				logger.error("ERROR: getEncodedString ",e);
			}
		}
		logger.info("INFO: getEncodedString :: Ends");
		return sb.toString();
	}


	public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
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
		return finalDate;
	}

	public static String getTimeFromSec(String secs){
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
		return time;
	}

	public static String addMinutes(String dtStr, int minutes) {
		String newdateStr = "";
		try {
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = date.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MINUTE, minutes);
			Date newDate = cal.getTime();
			newdateStr = date.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newdateStr; 
	}



	/*public static String addDays(String dtStr, int minutes) {
		String newdateStr = "";
		try {
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = date.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MINUTE, minutes);
			Date newDate = cal.getTime();
			newdateStr = date.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return newdateStr; 
	}*/

	public static String addDays(String dtStr, int days) {
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE_TIME.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DATE, days);
			Date newDate = cal.getTime();
			newdateStr = StudyMetaDataConstants.SDF_DATE_TIME.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newdateStr; 
	}

	public static String addMonth(String dtStr, int months) {
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MONTH, months);
			Date newDate = cal.getTime();
			newdateStr =StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newdateStr;

	}

	public static String addYear(String dtStr, int years) {
		String newdateStr = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.YEAR, years);
			Date newDate = cal.getTime();
			newdateStr = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newdateStr ;
	}

	public static String convertStringBase64ToImage(String imageInString, String uploadImagePath) throws Exception {
		logger.info("StudyMetaDataUtil.convertStringBase64ToImage() :: Starts");
		String filename = "";
		String destFile = "";
		try{
			destFile = uploadImagePath;
			filename = new Date().getTime()+".png";
			destFile = destFile+filename;
			byte[] imageInByte;
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			imageInByte = decoder.decodeBuffer(imageInString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
			BufferedImage image = ImageIO.read(bis);
			bis.close();
			ImageIO.write(image, "png", new File(destFile));
		}catch (Exception e) {
			logger.error("StudyMetaDataUtil.convertStringBase64ToImage() :: ERROR " + e);
		}
		logger.info("StudyMetaDataUtil.convertStringBase64ToImage() :: Ends");
		return filename;
	}

	public static Long getDateToSeconds(String getCurrentDate) {
		Long getInSeconds = null;
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(getCurrentDate);
			getInSeconds=dt.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
		/* long getLongValue = Long.parseLong(value);*/
		/* Date date=new Date(value);*/
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");// yyyy-MM-dd
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(value);
		logger.info("StudyMetaDataUtil: getToDate() - Ends ");
		return dateText;
	}


	public static Long getDateAndTimeToSeconds(String getCurrentDateTime) {
		Long getInSeconds = null;
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE_TIME.parse(getCurrentDateTime);
			getInSeconds=dt.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getInSeconds;
	}


	public static String getSecondsToDateandTime(String value) {
		logger.info("StudyMetaDataUtil: getSecondsToDate() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue);
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// yyyy-MM-dd HH:mm:ss
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDate() - Ends ");
		return dateText;

		/*Calendar currentDate = Calendar.getInstance();
			String dateNow = AcuityLinkConstants.DB_SDF_DATE.format(currentDate.getTime());
			//SimpleDateFormat formatter = AcuityLinkConstants.DB_SDF_DATE;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			String dateNow = formatter.format(currentDate.getTime());
			return dateNow;*/
	}

	/* public static String addMinutesForLonDate(String dtStr, int minutes) {
			String newdateStr = "";
			try {
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dt = date.parse(dtStr);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				cal.add(Calendar.MINUTE, minutes);
				Date newDate = cal.getTime();
				newdateStr = date.format(newDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        return newdateStr; 
		}*/


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
		logger.info("StudyMetaDataUtil: getSecondsToDateTimeMin() - Starts ");
		String dateText;
		long getLongValue = Long.parseLong(value);
		Date date=new Date(getLongValue* 1000);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");// yyyy-MM-dd HH:mm:ss
		df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateText = df2.format(date);
		logger.info("StudyMetaDataUtil: getSecondsToDateTimeMin() - Ends ");
		return dateText;
	}

	public static void main(String[] args) {
		/*try {
				String imagePath = convertStringBase64ToImage("iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NjVBRkU3MTYyNjBFMTFFNTlCRDY4MzZBNTJERTRFQzUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NjVBRkU3MTcyNjBFMTFFNTlCRDY4MzZBNTJERTRFQzUiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo2NUFGRTcxNDI2MEUxMUU1OUJENjgzNkE1MkRFNEVDNSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo2NUFGRTcxNTI2MEUxMUU1OUJENjgzNkE1MkRFNEVDNSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PiYpslsAAARkSURBVHja7J1dTuMwEMeHigNY4gLhBGtO0PLKy5YTUE7Q8I6UVuIdOEG7J2ifeG04wfYIuQBSOAFrS442qpLi0NiecWYkCzVUTeKf//PhOMnZ19cXsOGxMwbCQNgYCANhYyAMhI2BMBA2BsJAev/Rj6e3EOeSqFb08UMXjzfBgIwiGVgr1f6qJqmfSAxAUtVmqgkDRjCQcKZBPNc+a4XsGEgYkwcw6ttXDMSvCaMEcUQ5KQPxZzuLWKHVM2EgfjIq22xqY9JhBuIwiM86urYNpcyLEpCfBuu24M9ATgzimxOVlTKQ/qyPWPBMoZKnAGTRY7aEPp5gB6JBZD3+XoK9aMQMRDjqvCnmeIIZyMphDYE2nmAFkpqR7Bq4YCB2fj7zsB/paT/kgfjMhFJANt+FDcgigG9H5bowAQnlQhJA5LowAQlZH6BxXViAhHBVKF0XBiDaZcwHlN2hB4IpqKahlRoayBTwXWZdDRWIAJwTffLj6S0dIpAU8E6FZwqKGBIQVLl/i3qzIQGhcI07VSpJhgBkAu5ncskOnBBAMqBjU6WSScxAJkBvNWEWM5AM6NnEp0pGrA5cA2nE6rBWiYwJiCSsjsrmMQGZA32b+ahLfADRVe8M4rBZDEBigaHtLgYgdxEBSZTbmlIGIiGCe8cP7DdlIFOIz0grpG005artiQIRLit3l0BEg7sqVbtS7dr8vWe35Q9I0yhaHyhjTVQpkiKQpoP+bNhWEgRC0mWNLb9HMpa4mttyCcR2muGTak0SA5BfDdsKokDIKcQWUg5szoHII9tFg0IKRuEWiOiYoWwZhVsgecei6pVRhIkh2qYtbotjSSAgApon6B6I9V1ODcixA85aCsQXQkBKakCKb9LftEUlJCr3i8ebPTUg79/8P2vJxm4B//yWs3g3CnjQbTfsaGVdI4fyThFIYeF+2p7MszdQsBaMW4pAtP2x+E7b41w1lCuERWPhKn74ALK2/J5+volsyWR0TLlH5MKcFrGugZSWUKonVcsjYC9VWwZ2Y2WHQYa2MFx2KBj1KydmRzpjYcDcmo7xDedVuSunSj334XMNFNvV7zrzGpuapDwSVLc1kNLUNonZNob+L7MWPgpXX2/YqUZ/0rEDHk4I6ivodxnrrVKH8wTD11xWCd2X/CQm2O9+ONqXPR7/1gcMn0CqQvEnnTQxUKr4YntDf1+XWAvwuH4sxEvBNnD6cszcVMu5qVfKFnXJHpR97bLuwADkuxT3lJFc1FQF1GA4A2IBzBWUPmOedxjBgNSgPAO+G3qCwQgKpAZmAXju0N2b9DbYbEDwJ8qpk9dAMMzs6gzwKvRxoHgIpoKSm85YBth9te8Fhr5A93Jic+txBs2rU1zURXnDABm2Qg46oyrELqH/a+zVbG1101CO7fzPAa/pznsxLTGKGcP/icQuStibQhL9CslzoGFFDU5VXMraFIk4yJRKILpmmF9wz0DYGAgDYWMgDISNgTAQNgbCQBgIJvsnwAAQ6kbUuyqCkQAAAABJRU5ErkJggg==", "D://temp/");
				System.out.println("imagePath ==> " + imagePath);
				BufferedImage image = ImageIO.read(new File("D://temp/" + imagePath));
				 int imageWidth = image.getWidth();
				 int imageHeight = image.getHeight();
				 System.out.println("imageWidth ==> " + imageWidth);
				 System.out.println("imageHeight ==> " + imageHeight);
			} catch (Exception e) {
				e.printStackTrace();
			}*/

		/*String getCurrentDate=CommonUtil.getCurrentDate();
			try {
				Date dt = CommonContants.SDF_DATE.parse(getCurrentDate);
				 System.out.println("check:---"+dt.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		/*String getCurrentDate=StudyMetaDataUtil.getCurrentDate();
			Long long1=StudyMetaDataUtil.getDateToSeconds(getCurrentDate);
			System.out.println("check:---"+long1);
			Integer i = (int) (long) long1;
			System.out.println("ch In:---"+i);*/

		String	uploadImagePath = (String)propMap.get("uploadImagePath");
		/*StudyMetaDataUtil.getCurrentDateTime();*/
		/*Long long2=StudyMetaDataUtil.getDateToSeconds(StudyMetaDataUtil.getCurrentDate());
			System.out.println("vv:---"+StudyMetaDataUtil.getCurrentDate());
			System.out.println("date in seconds:---"+long2);*/ //1483784940000 

		/*Long long1=StudyMetaDataUtil.getDateAndTimeToSeconds(StudyMetaDataUtil.getCurrentDateTime())/1000;
			System.out.println("dateTime in seconds:---"+long1);
			Date d = new Date(long1 * 1000);
			SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");// yyyy-MM-dd HH:mm:ss
		    String dateText = df2.format(d);
			System.out.println("dateText:----"+ dateText);*/

		/*String currentDateTime=StudyMetaDataUtil.getCurrentDateTime();
			Long long1=StudyMetaDataUtil.getDateAndTimeToSeconds(currentDateTime)/1000;
			System.out.println("current time in sec::---"+long1);
			String subHoursDate=StudyMetaDataUtil.addMinutes(currentDateTime, -60);
			System.out.println("subHoursDate:---"+subHoursDate);
			Long long2=StudyMetaDataUtil.getDateAndTimeToSeconds(subHoursDate)/1000;
		    System.out.println("before 1 hour in sec:---"+long2);*/

		/*String ch=StudyMetaDataUtil.getSecondsToDateandTime(long1.toString() * 1000);
			System.out.println("ch:---"+ch);*/

		/* File f = null;
			   boolean bool = false;
	         f = new File("test.txt");
	         try {
				bool = f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

	         // prints
	         System.out.println("File created: "+bool);*/
		FileWriter fWriter = null;
		BufferedWriter writer = null;
		try {
			fWriter = new FileWriter("alertCh.html");
			writer = new BufferedWriter(fWriter);
			writer.write(""
					+ " <head>"
					+ " <script type=\"text/javascript\" src=\"http://player.wowza.com/player/latest/wowzaplayer.min.js\"></script>"
					+ " </head>"
					+ "<body>"
					+ "<div id=\"playerElement\" style=\"width:100%; height:0; padding:0 0 56.25% 0\"></div>"
					+ "</body>"
					+ ""
					+ "<script type=\"text/javascript\">"
					+ " WowzaPlayer.create('playerElement',"
					+ " {"
					+ "\"license\":\"PLAY1-kmQfa-pm8xW-JC7bG-W7Pkv-nkzaT\","
					+ "\"title\":\"\","
					+ "\"description\":\"\","
					+ "\"sourceURL\":\"http://wowzaprod121-i.akamaihd.net/hls/live/266523/6d02f644/playlist.m3u8\","
					+ "\"autoPlay\":false,"
					+ "\"volume\":\"75\","
					+ "\"mute\":false,"
					+ "\"loop\":false,"
					+ "\"uiShowQuickRewind\":false"
					+ " }"
					+ " );"
					+ "</script>");
			/* writer.newLine();*/ //this is not actually needed for html files - can make your code more readable though 
			writer.close(); //make sure you close the writer object 
		} catch (Exception e) {
			//catch any exceptions here
		}
	}

	public static String getYoutubeURLFile(String url,String liveStreamId){
		String result =StudyMetaDataConstants.SUCCESS;
		FileWriter fWriter = null;
		BufferedWriter writer = null;
		String localiveStreamFiles = (String)propMap.get("localiveStreamFiles");
		try {
			fWriter = new FileWriter(localiveStreamFiles+"/"+liveStreamId+".html");
			writer = new BufferedWriter(fWriter);
			writer.write(""
					+ " <head>"
					+ " <script type=\"text/javascript\" src=\"http://player.wowza.com/player/latest/wowzaplayer.min.js\"></script>"
					+ " </head>"
					+ "<body>"
					+ "<div id=\"playerElement\" style=\"width:100%; height:0; padding:0 0 56.25% 0\"></div>"
					+ "</body>"
					+ ""
					+ "<script type=\"text/javascript\">"
					+ " WowzaPlayer.create('playerElement',"
					+ " {"
					+ "\"license\":\"PLAY1-kmQfa-pm8xW-JC7bG-W7Pkv-nkzaT\","
					+ "\"title\":\"\","
					+ "\"description\":\"\","
					+ "\"sourceURL\":\""
					+ url+"\","
					+ "\"autoPlay\":false,"
					+ "\"volume\":\"75\","
					+ "\"mute\":false,"
					+ "\"loop\":false,"
					+ "\"uiShowQuickRewind\":false"
					+ " }"
					+ " );"
					+ "</script>");
			/* writer.newLine();*/ //this is not actually needed for html files - can make your code more readable though 
			writer.close(); //make sure you close the writer object 
		} catch (Exception e) {
			//catch any exceptions here
		}
		return result;
	}

	public static String getCurrentDateTimeInUTC() {
		logger.info("StudyMetaDataUtil: getCurrentDateTimeInUTC() - Starts ");
		String dateNow = null;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
						if(authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)){
							String appBundleId = "";
							String appTokenId = "";
							for(Map.Entry<String, String> map : authPropMap.entrySet()){
								if(map.getValue().equals(appToken)){
									appTokenId = map.getKey();
								}
								
								if(map.getValue().equals(bundleId)){
									appBundleId = map.getKey();
								}
							}
							
							if(StringUtils.isNotEmpty(appBundleId) && StringUtils.isNotEmpty(appTokenId)){
								final StringTokenizer authTokenizer = new StringTokenizer(appTokenId, ".");
								final String id = authTokenizer.nextToken();
								final String platformType = authTokenizer.nextToken();
								final String key = authTokenizer.nextToken();
								if(platformType.equals(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)){
									switch (type) {
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM: platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS: platform = StudyMetaDataConstants.STUDY_PLATFORM_ANDROID;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID: platform = authPropMap.get("android.bundleid");
											break;
									}
								}else{
									switch (type) {
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM: platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS: platform = StudyMetaDataConstants.STUDY_PLATFORM_IOS;
											break;
										case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID: platform = authPropMap.get("ios.bundleid");
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
	private static HashMap getAuthorizationProperties(){
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
		String actualDay = "";
		try {
			if(StringUtils.isNotEmpty(input)){
				SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date MyDate = newDateFormat.parse(input);
				newDateFormat.applyPattern(StudyMetaDataConstants.SDF_DAY);
				actualDay = newDateFormat.format(MyDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actualDay;
	}

	public static String addDaysToDate(String input, int days){
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DATE, days);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static String addWeeksToDate(String input, int weeks){
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.WEEK_OF_MONTH, weeks);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static String addMonthsToDate(String input, int months){
		String output = "";
		try {
			Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MONTH, months);
			Date newDate = cal.getTime();
			output = StudyMetaDataConstants.SDF_DATE.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static String getFormattedDateTimeZone(String input, String inputFormat, String outputFormat){
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
			e.printStackTrace();
		}
		return output;
	}
	
	public static String addSeconds(String dtStr, int seconds) {
		String newdateStr = "";
		try {
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = date.parse(dtStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.SECOND, seconds);
			Date newDate = cal.getTime();
			newdateStr = date.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
		logger.info("INFO: AuthenticationService - getBundleIdFromAuthorization() - Starts");
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
						if(authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)){
							appBundleId = bundleId;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("AuthenticationService - getBundleIdFromAuthorization() :: ERROR", e);
		}
		logger.info("INFO: AuthenticationService - getBundleIdFromAuthorization() - Ends");
		return appBundleId;
	}
	/*-----------------------------------------FDA WCP WS related methods ends-----------------------------------------*/
}