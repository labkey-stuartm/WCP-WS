package com.studymetadata.bean;

public class ChartsBean {
	private String title = "";
	private String displayName = "";
	private String type = "";
	private String configuration = "";

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

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public ConfigurationBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(ConfigurationBean dataSource) {
		this.dataSource = dataSource;
	}
	
}
