package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 *
 */
public class ReviewBean {
	private String reasonForConsent = "";
	private String signatureTitle = "";
	private String signatureContent = "";
	
	public String getReasonForConsent() {
		return reasonForConsent;
	}
	public void setReasonForConsent(String reasonForConsent) {
		this.reasonForConsent = reasonForConsent;
	}
	public String getSignatureTitle() {
		return signatureTitle;
	}
	public void setSignatureTitle(String signatureTitle) {
		this.signatureTitle = signatureTitle;
	}
	public String getSignatureContent() {
		return signatureContent;
	}
	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}
	
	
}
