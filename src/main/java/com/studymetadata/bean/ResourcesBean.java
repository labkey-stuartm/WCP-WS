package com.studymetadata.bean;

public class ResourcesBean {
	/*/gatewayInfo*/
	private String title = "";
	private String type = ""; //html/pdf
	private String content = ""; //text/pdf link

	
	private String audience = "";
	private String studyId = "";
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
}
