package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

public class ActiveTaskActivityMetaDataResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private ActiveTaskActivityStructureBean activity = new ActiveTaskActivityStructureBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ActiveTaskActivityStructureBean getActivity() {
		return activity;
	}
	public void setActivity(ActiveTaskActivityStructureBean activity) {
		this.activity = activity;
	}
	
}
