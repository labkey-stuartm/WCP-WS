package com.studymetadata.bean;

public class EligibilityConsentResponse {
	private String message = "FAILURE";
	private EligibilityBean eligibility = new EligibilityBean();
	private ConsentDetailsBean consent = new ConsentDetailsBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public EligibilityBean getEligibility() {
		return eligibility;
	}
	public void setEligibility(EligibilityBean eligibility) {
		this.eligibility = eligibility;
	}
	public ConsentDetailsBean getConsent() {
		return consent;
	}
	public void setConsent(ConsentDetailsBean consent) {
		this.consent = consent;
	}
}
