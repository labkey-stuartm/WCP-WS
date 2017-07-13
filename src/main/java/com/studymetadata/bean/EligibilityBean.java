package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EligibilityBean {
	/*/eligibilityConsent*/
	private String type = "";
	private String tokenTitle = "";
	private List<QuestionnaireActivityStepsBean> test = new ArrayList<>(); //<refer Appendix 3 for question structures>
	private List<HashMap<String,Object>> correctAnswers = new ArrayList<>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTokenTitle() {
		return tokenTitle;
	}
	public void setTokenTitle(String tokenTitle) {
		this.tokenTitle = tokenTitle;
	}
	public List<QuestionnaireActivityStepsBean> getTest() {
		return test;
	}
	public void setTest(List<QuestionnaireActivityStepsBean> test) {
		this.test = test;
	}
	public List<HashMap<String, Object>> getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(List<HashMap<String, Object>> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	
}
