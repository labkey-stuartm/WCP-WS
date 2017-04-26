package com.studymetadata.bean;

import java.util.HashMap;
import java.util.Map;

public class ActiveTaskActivityStepsBean {
	private String type = ""; //instruction (OR) question (OR) form (OR) task
	private String resultType = "";
	private String key = "";
	private String text = "";
	private String[] options = new String[0];
	private Map<String, Object> format = new HashMap<>(); //QuestionFormat (OR) ActiveTaskFormat
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public Map<String, Object> getFormat() {
		return format;
	}
	public void setFormat(Map<String, Object> format) {
		this.format = format;
	}
	
}
