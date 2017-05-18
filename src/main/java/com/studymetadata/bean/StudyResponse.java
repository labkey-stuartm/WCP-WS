package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class StudyResponse {
	private String message = "FAILURE";
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
