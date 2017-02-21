package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class EligibilityConsentResponse {
	private String message = "FAILURE";
	private EligibilityBean eligibility = new EligibilityBean();
	private List<ConsentBean> consent = new ArrayList<ConsentBean>();
	private List<ComprehensionBean> comprehension = new ArrayList<ComprehensionBean>();
	private SharingBean sharing = new SharingBean();
	private ReviewBean review = new ReviewBean();
	
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
	public List<ConsentBean> getConsent() {
		return consent;
	}
	public void setConsent(List<ConsentBean> consent) {
		this.consent = consent;
	}
	public List<ComprehensionBean> getComprehension() {
		return comprehension;
	}
	public void setComprehension(List<ComprehensionBean> comprehension) {
		this.comprehension = comprehension;
	}
	public SharingBean getSharing() {
		return sharing;
	}
	public void setSharing(SharingBean sharing) {
		this.sharing = sharing;
	}
	public ReviewBean getReview() {
		return review;
	}
	public void setReview(ReviewBean review) {
		this.review = review;
	}
	
}
