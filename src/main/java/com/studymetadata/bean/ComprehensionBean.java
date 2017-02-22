package com.studymetadata.bean;

import com.studymetadata.bean.appendix.QuestionStepStructureBean;

public class ComprehensionBean {
	private QuestionStepStructureBean questionStepStructureBean = new QuestionStepStructureBean(); //<refer Appendix 3 for choice question format>

	public QuestionStepStructureBean getQuestionStepStructureBean() {
		return questionStepStructureBean;
	}

	public void setQuestionStepStructureBean(
			QuestionStepStructureBean questionStepStructureBean) {
		this.questionStepStructureBean = questionStepStructureBean;
	}
	
}
