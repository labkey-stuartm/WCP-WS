package com.studymetadata.bean;

/**
 * Provides comprehension details for the questionnaire type of activity
 * {@link QuestionnaireActivityStepsBean}
 * 
 * @author BTC
 * @since Jan 4, 2018 3:09:28 PM
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
