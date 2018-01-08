package com.studymetadata.bean;

/**
 * Provides details about the correct answers for comprehension test
 * 
 * @author BTC
 * @since Jan 4, 2018 3:10:20 PM
 *
 */
public class CorrectAnswersBean {

	private String key = "";
	private String[] answer = {};
	private String evaluation = "";

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
