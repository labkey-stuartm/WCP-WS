package com.studymetadata.util;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;


public class Mail  {
	private static Logger logger = Logger.getLogger(Mail.class.getName());
	
	@SuppressWarnings("unchecked")
	static HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	public static boolean sendemail(String email, String subject, String messageBody) throws Exception{
		logger.debug("sendemail()====start");
		boolean sentMail = false;
		try {
			Properties props = new Properties();
			/*props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "465");*/
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.host", propMap.get("smtp.hostname"));
			props.put("mail.smtp.port", propMap.get("smtp.portvalue"));
			/*props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");*/
			Session session = Session.getInstance(props/*,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("apps@boston-technology.com", "password789");
				}
			}*/);
			
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);
			message.setContent(messageBody, "text/html");
			Transport.send(message);
			sentMail = true;
		} catch (MessagingException e) {
			logger.error("ERROR:  sendemail() - "+e+" : ");
			sentMail = false;
		} catch (Exception e) {
			logger.error("ERROR:  sendemail() - "+e+" : ");
		}
		logger.info("Mail.sendemail() :: Ends");
		return sentMail;
	}
}