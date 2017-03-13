package com.studymetadata.bean;

public class ActivitiesBean {
	private String title = "";
	private String type = ""; //active task/questionnaire
	private String frequency = "";
	private ConfigurationBean configuration = new ConfigurationBean();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public ConfigurationBean getConfiguration() {
		return configuration;
	}
	public void setConfiguration(ConfigurationBean configuration) {
		this.configuration = configuration;
	}
}
