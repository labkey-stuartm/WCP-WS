package com.studymetadata.bean;

public class ConsentBean {
	private String type = "";
	private String title = "";
	private String text = "";
	private String description = "";
	private String html = "";
	private String url = "";
	private boolean visualStep = false;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isVisualStep() {
		return visualStep;
	}
	public void setVisualStep(boolean visualStep) {
		this.visualStep = visualStep;
	}
	
	
}
