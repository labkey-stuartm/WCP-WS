package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:11:38 PM
 *
 */
public class GatewayInfoResourceBean {
	
	private String resourcesId = "";
	private String title = "";
	private String type = "";
	private String content = "";
	
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
	public String getResourcesId() {
		return resourcesId;
	}
	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}
	
}
