package com.studymetadata.bean;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:15:05 PM
 *
 */
public class StudyBean {

	private String studyId = "";
	private String studyVersion = "";
	private String title = "";
	private String category = "";
	private String sponsorName = "";
	private String tagline = "";
	private String status = "";
	private String logo = "";
	private SettingsBean settings = new SettingsBean();

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(String studyVersion) {
		this.studyVersion = studyVersion;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSponsorName() {
		return sponsorName;
	}

	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public SettingsBean getSettings() {
		return settings;
	}

	public void setSettings(SettingsBean settings) {
		this.settings = settings;
	}

}
