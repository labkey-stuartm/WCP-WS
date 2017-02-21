package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class EligibilityBean {
	/*/eligibilityConsent*/
	private String type = "";
	private String tokenTitle = "";
	private List<QuestionStepStructureBean> test = new ArrayList<QuestionStepStructureBean>(); //<refer Appendix 3 for question structures>
	
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
	public List<QuestionStepStructureBean> getTest() {
		return test;
	}
	public void setTest(List<QuestionStepStructureBean> test) {
		this.test = test;
	}
	
}
