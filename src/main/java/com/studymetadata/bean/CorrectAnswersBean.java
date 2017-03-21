package com.studymetadata.bean;

public class CorrectAnswersBean{

	private String key = "";
	private String[] answer = {};
	private String evaluation = ""; //	ANY/ALL
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String[] getAnswer() {
		return answer;
	}
	public void setAnswer(String[] answer) {
		this.answer = answer;
	}
	public String getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}
	
}
