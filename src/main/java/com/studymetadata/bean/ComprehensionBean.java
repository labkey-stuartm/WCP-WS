package com.studymetadata.bean;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:09:28 PM
 *
 */
public class ComprehensionBean {

	private QuestionnaireActivityStepsBean questionStepStructureBean = new QuestionnaireActivityStepsBean();

	public QuestionnaireActivityStepsBean getQuestionStepStructureBean() {
		return questionStepStructureBean;
	}

	public void setQuestionStepStructureBean(
			QuestionnaireActivityStepsBean questionStepStructureBean) {
		this.questionStepStructureBean = questionStepStructureBean;
	}

}
