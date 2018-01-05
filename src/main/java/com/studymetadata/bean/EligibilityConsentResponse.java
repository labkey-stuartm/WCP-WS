package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides eligibility {@link EligibilityBean} and consent
 * {@link ConsentDetailsBean} metadata details in the response.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:11:04 PM
 *
 */
public class EligibilityConsentResponse {

	private String message = StudyMetaDataConstants.FAILURE;
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
