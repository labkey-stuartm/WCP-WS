package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class QuestionnaireActivityStepsBean {
	private String type = ""; //instruction (OR) question (OR) form (OR) task
	private String resultType = "";
	private String key = "";
	private String title = "";
	private String text = "";
	private Boolean skippable = false;
	private String groupName = "";
	private Boolean repeatable = false;
	private String repeatableText = "";
	private List<DestinationBean> destinations = new ArrayList<>();
	private String healthDataKey = "";
	private Map<String, Object> format = new HashMap<>(); //QuestionFormat (OR) ActiveTaskFormat
	private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>(); //Question
	
	private String[] options = new String[0];
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getSkippable() {
		return skippable;
	}
	public void setSkippable(Boolean skippable) {
		this.skippable = skippable;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Boolean getRepeatable() {
		return repeatable;
	}
	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}
	public String getRepeatableText() {
		return repeatableText;
	}
	public void setRepeatableText(String repeatableText) {
		this.repeatableText = repeatableText;
	}
	public List<DestinationBean> getDestinations() {
		return destinations;
	}
	public void setDestinations(List<DestinationBean> destinations) {
		this.destinations = destinations;
	}
	public String getHealthDataKey() {
		return healthDataKey;
	}
	public void setHealthDataKey(String healthDataKey) {
		this.healthDataKey = healthDataKey;
	}
	public Map<String, Object> getFormat() {
		return format;
	}
	public void setFormat(Map<String, Object> format) {
		this.format = format;
	}
	public List<QuestionnaireActivityStepsBean> getSteps() {
		return steps;
	}
	public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
		this.steps = steps;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	
	
}
