package com.studymetadata.bean.appendix;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.bean.ActivityMetadataBean;
import com.studymetadata.bean.QuestionnaireActivityStepsBean;

/**
 * 
 * @author Mohan
 *
 */
public class QuestionnaireActivityStructureBean {
	private String type = "";
	private ActivityMetadataBean metadata = new ActivityMetadataBean();
	private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ActivityMetadataBean getMetadata() {
		return metadata;
	}
	public void setMetadata(ActivityMetadataBean metadata) {
		this.metadata = metadata;
	}
	public List<QuestionnaireActivityStepsBean> getSteps() {
		return steps;
	}
	public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
		this.steps = steps;
	}
}
