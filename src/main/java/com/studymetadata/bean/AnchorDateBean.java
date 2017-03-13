package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 *
 */
public class AnchorDateBean {
	private String type = "";
	private QuestionInfoBean questionInfo = new QuestionInfoBean();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public QuestionInfoBean getQuestionInfo() {
		return questionInfo;
	}
	public void setQuestionInfo(QuestionInfoBean questionInfo) {
		this.questionInfo = questionInfo;
	}
	
}
