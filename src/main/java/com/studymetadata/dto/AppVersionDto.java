package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="app_versions")
public class AppVersionDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="av_id")
	private Integer avId;
	
	@Column(name="force_update")
	private Integer forceUpdate=1;
	
	@Column(name="os_type")
	private String osType; //ios (or) android
	
	@Column(name="app_version")
	private Float appVersion=1f;
	
	@Column(name="created_on")
	private String createdOn;
	
	@Column(name="bundle_id")
	private String bundleId;
	
	@Column(name="custom_study_id")
	private String customStudyId;
	
	@Column(name="message")
	private String message;

	public Integer getAvId() {
		return avId;
	}

	public void setAvId(Integer avId) {
		this.avId = avId;
	}

	public Integer getForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(Integer forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public Float getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Float appVersion) {
		this.appVersion = appVersion;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
