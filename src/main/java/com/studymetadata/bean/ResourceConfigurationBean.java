package com.studymetadata.bean;

public class ResourceConfigurationBean {
	private String availableDate = ""; //availability start date
	private String expiryDate = ""; //availability end date
	private Integer startDays = 0; //elapsed days from anchor date (positive value) or days before anchor date (negative value)
	private Integer endDays = 0; //elapsed days from anchor date (positive value) or days before anchor date (negative value)
	
	public String getAvailableDate() {
		return availableDate;
	}
	public void setAvailableDate(String availableDate) {
		this.availableDate = availableDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Integer getStartDays() {
		return startDays;
	}
	public void setStartDays(Integer startDays) {
		this.startDays = startDays;
	}
	public Integer getEndDays() {
		return endDays;
	}
	public void setEndDays(Integer endDays) {
		this.endDays = endDays;
	}
	
}
