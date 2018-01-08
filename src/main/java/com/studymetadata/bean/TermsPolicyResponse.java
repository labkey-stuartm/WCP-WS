package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides application privacy, terms of use details in response.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:16:10 PM
 *
 */
public class TermsPolicyResponse {

	private String message = StudyMetaDataConstants.FAILURE;
	private String terms = "";
	private String privacy = "";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

}
