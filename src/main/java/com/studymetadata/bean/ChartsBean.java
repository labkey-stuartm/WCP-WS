package com.studymetadata.bean;

import java.util.HashMap;
import java.util.Map;

public class ChartsBean {
	private String title = "";
	private String displayName = "";
	private String type = "";
	private Map<String, Object> configuration = new HashMap<>();
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}
	public ConfigurationBean getDataSource() {
		return dataSource;
	}
	public void setDataSource(ConfigurationBean dataSource) {
		this.dataSource = dataSource;
	}
	
}
