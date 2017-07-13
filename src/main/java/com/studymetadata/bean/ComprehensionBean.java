package com.studymetadata.bean;

public class ComprehensionBean {
	private QuestionnaireActivityStepsBean questionStepStructureBean = new QuestionnaireActivityStepsBean(); //<refer Appendix 3 for choice question format>

	public QuestionnaireActivityStepsBean getQuestionStepStructureBean() {
		return questionStepStructureBean;
	}

	public void setQuestionStepStructureBean(QuestionnaireActivityStepsBean questionStepStructureBean) {
		this.questionStepStructureBean = questionStepStructureBean;
	}
	
}
