package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class ComprehensionDetailsBean {
	private Integer passScore = 0;
	private List<ComprehensionBean> questions = new ArrayList<ComprehensionBean>();
	
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
	
}
