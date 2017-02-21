package com.studymetadata.bean;

public class StatisticsBean {
	private String title = "";
	private String displayName = "";
	private String activityType = "";
	private String unit = "";
	private String calculation = "";
	private ConfigurationBean dataSource = new ConfigurationBean();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getCalculation() {
		return calculation;
	}
	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}
	public ConfigurationBean getDataSource() {
		return dataSource;
	}
	public void setDataSource(ConfigurationBean dataSource) {
		this.dataSource = dataSource;
	}
	
	
}
