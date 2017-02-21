package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 *
 */
public class QuestionStepStructureBean {
	private String title="";
	private String key="";
	private String type="";
	private String resultType="";
	private String text="";
	private boolean skippable=false;
	private String groupName="";
	private String phi="";
	private String destinations=""; //Dictionary; nil if branching is false
	private String format=""; //Dictionary; specific to each question type, nil in case of no formatting or	validation required
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isSkippable() {
		return skippable;
	}
	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getPhi() {
		return phi;
	}
	public void setPhi(String phi) {
		this.phi = phi;
	}
	public String getDestinations() {
		return destinations;
	}
	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	
}
