package com.studymetadata.bean;

public class ResourcesBean {
	/*/gatewayInfo*/
	private String title = "";
	private String type = ""; //html/pdf
	private String content = ""; //text/pdf link

	
	private String audience = "";
	private String studyId = "";
	private String key = "";
	private ConfigurationBean configuration = new ConfigurationBean();
	
	private String resourceId = "";
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public String getStudyId() {
		return studyId;
	}
	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}
	public ConfigurationBean getConfiguration() {
		return configuration;
	}
	public void setConfiguration(ConfigurationBean configuration) {
		this.configuration = configuration;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
