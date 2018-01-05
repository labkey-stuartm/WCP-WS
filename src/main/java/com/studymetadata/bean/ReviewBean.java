package com.studymetadata.bean;

/**
 * Provides consent review details.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:14:01 PM
 *
 */
public class ReviewBean {

	private String reasonForConsent = "";
	private String reviewHTML = "";

	public String getReasonForConsent() {
		return reasonForConsent;
	}

	public void setReasonForConsent(String reasonForConsent) {
		this.reasonForConsent = reasonForConsent;
	}

	public String getReviewHTML() {
		return reviewHTML;
	}

	public void setReviewHTML(String reviewHTML) {
		this.reviewHTML = reviewHTML;
	}

}
