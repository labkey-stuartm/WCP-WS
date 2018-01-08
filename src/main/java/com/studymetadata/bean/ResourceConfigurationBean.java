package com.studymetadata.bean;

/**
 * Provides configuration details for resource.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:13:11 PM
 *
 */
public class ResourceConfigurationBean {

	private String availableDate = "";
	private String expiryDate = "";
	private Integer startDays = 0;
	private Integer endDays = 0;

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
