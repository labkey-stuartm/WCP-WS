package com.studymetadata.bean;

import com.studymetadata.bean.appendix.ActivityStructureBean;
import com.studymetadata.util.StudyMetaDataConstants;

public class ActivityMetaDataResponse {
	private String message = StudyMetaDataConstants.FAILURE;
	private ActivityStructureBean activity = new ActivityStructureBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ActivityStructureBean getActivity() {
		return activity;
	}
	public void setActivity(ActivityStructureBean activity) {
		this.activity = activity;
	}
	
}
