package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class ComprehensionDetailsBean {
	private Integer passScore = 0;
	private List<ComprehensionBean> questions = new ArrayList<>();
	private List<CorrectAnswersBean> correctAnswers = new ArrayList<>();
	
	public Integer getPassScore() {
		return passScore;
	}
	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}
	public List<ComprehensionBean> getQuestions() {
		return questions;
	}
	public void setQuestions(List<ComprehensionBean> questions) {
		this.questions = questions;
	}
	public List<CorrectAnswersBean> getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(List<CorrectAnswersBean> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	
}
