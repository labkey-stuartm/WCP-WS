package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides studies metadata details {@link StudyBean} in response.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:15:27 PM
 *
 */
public class StudyResponse {

	private String message = StudyMetaDataConstants.FAILURE;
	private List<StudyBean> studies = new ArrayList<>();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<StudyBean> getStudies() {
		return studies;
	}

	public void setStudies(List<StudyBean> studies) {
		this.studies = studies;
	}
}
