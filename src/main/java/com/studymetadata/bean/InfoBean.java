package com.studymetadata.bean;

public class InfoBean {
	private String type = ""; //text/video
	private String image = ""; //link
	private String title = "";
	private String text = "";
	private String link = ""; //used for media link
	private String fdaLink = "";
	private String website = "";
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getFdaLink() {
		return fdaLink;
	}
	public void setFdaLink(String fdaLink) {
		this.fdaLink = fdaLink;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
}
