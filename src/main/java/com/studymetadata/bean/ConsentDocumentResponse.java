package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 *
 */
public class ConsentDocumentResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private ConsentDocumentBean consent = new ConsentDocumentBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ConsentDocumentBean getConsent() {
		return consent;
	}
	public void setConsent(ConsentDocumentBean consent) {
		this.consent = consent;
	}
	
}
