package com.studymetadata.bean;

public class BrandingBean {
	private String titleFont = "";
	private String bgColor = ""; //hex code
	private String tintColor = ""; //hex code
	private String logo = ""; //image link
	
	public String getTitleFont() {
		return titleFont;
	}
	public void setTitleFont(String titleFont) {
		this.titleFont = titleFont;
	}
	public String getBgColor() {
		return bgColor;
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	public String getTintColor() {
		return tintColor;
	}
	public void setTintColor(String tintColor) {
		this.tintColor = tintColor;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	
}
