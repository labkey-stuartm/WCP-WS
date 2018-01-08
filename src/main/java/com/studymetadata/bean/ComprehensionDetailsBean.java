package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides information about pass score, comprehension questions
 * {@link QuestionnaireActivityStepsBean} and correct answers details
 * {@link CorrectAnswersBean}
 * 
 * @author BTC
 * @since Jan 4, 2018 3:09:33 PM
 *
 */
public class ComprehensionDetailsBean {

	private Integer passScore = 0;
	private List<QuestionnaireActivityStepsBean> questions = new ArrayList<>();
	private List<CorrectAnswersBean> correctAnswers = new ArrayList<>();

	public Integer getPassScore() {
		return passScore;
	}

	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}

	public List<QuestionnaireActivityStepsBean> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionnaireActivityStepsBean> questions) {
		this.questions = questions;
	}

	public List<CorrectAnswersBean> getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(List<CorrectAnswersBean> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

}
