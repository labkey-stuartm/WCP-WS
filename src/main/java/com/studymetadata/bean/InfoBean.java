package com.studymetadata.bean;

/**
 * Provides study details.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:12:05 PM
 *
 */
public class InfoBean {

	private String type = "";
	private String image = "";
	private String title = "";
	private String text = "";
	private String videoLink = "";

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

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

}
