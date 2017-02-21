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
	HashMap<String, String> propMap = StudyMetaDataUtil.configMap;
	
	public boolean sendEmail(String email, String subject, String messageBody) throws Exception{
		logger.info("INFO: Mail - sendEmail() :: Starts");
		boolean sentMail = false;
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", propMap.get("SMTPHost"));
			props.put("mail.smtp.port", propMap.get("SMTPPort"));
			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(propMap.get("fromEmail"), propMap.get("password"));
						}
					});
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);
			message.setContent(messageBody, "text/html");
			Transport.send(message);
			sentMail = true;
		} catch (MessagingException me) {
	        logger.error("Mail - sendEmail() :: ERROR ", me);
	        me.printStackTrace();
	        sentMail = false;
		} catch (Exception e) {
			logger.error("Mail - sendEmail() :: ERROR ", e);
			e.printStackTrace();
		}
		logger.info("INFO: Mail - sendEmail() :: Ends");
		return sentMail;
	}
}
