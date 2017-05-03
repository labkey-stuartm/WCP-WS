package com.studymetadata.bean;

public class ResourcesBean {
	/*/gatewayInfo*/
	private String title = "";
	private String type = ""; //html/pdf
	private String resourcesId = "";
	private String content = ""; //text/pdf link
	private String audience = "";
	private ResourceConfigurationBean availability = new ResourceConfigurationBean();
	
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
	public String getResourcesId() {
		return resourcesId;
	}
	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
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
	public ResourceConfigurationBean getAvailability() {
		return availability;
	}
	public void setAvailability(ResourceConfigurationBean availability) {
		this.availability = availability;
	}
	
}
