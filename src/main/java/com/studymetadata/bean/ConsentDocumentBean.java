package com.studymetadata.bean;

/**
 * 
 * @author Mohan
 *
 */
public class ConsentDocumentBean {
	private String version = "";
	private String type = ""; //text/html
	private String content = "";
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
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
	
}
