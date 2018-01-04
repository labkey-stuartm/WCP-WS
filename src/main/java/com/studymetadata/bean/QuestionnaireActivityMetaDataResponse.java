package com.studymetadata.bean;

import com.studymetadata.bean.appendix.QuestionnaireActivityStructureBean;
import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:12:39 PM
 *
 */
public class QuestionnaireActivityMetaDataResponse {
	
	private String message = StudyMetaDataConstants.FAILURE;
	private QuestionnaireActivityStructureBean activity = new QuestionnaireActivityStructureBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public QuestionnaireActivityStructureBean getActivity() {
		return activity;
	}
	public void setActivity(QuestionnaireActivityStructureBean activity) {
		this.activity = activity;
	}
	
}
