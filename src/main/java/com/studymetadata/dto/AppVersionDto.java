package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:25:53 PM
 *
 */
@Entity
@Table(name = "app_versions")
@NamedQueries({

		@NamedQuery(name = "AppVersionDto.findByBundleIdOsType", query = "FROM AppVersionDto AVDTO"
				+ " WHERE AVDTO.bundleId= :bundleId AND AVDTO.osType= :osType"
				+ " ORDER BY AVDTO.avId DESC"),

		@NamedQuery(name = "AppVersionDto.findByBundleIdOsTypeAppVersion", query = "FROM AppVersionDto AVDTO"
				+ " WHERE AVDTO.bundleId= :bundleId AND AVDTO.osType= :osType"
				+ " ORDER BY AVDTO.appVersion DESC"), })
public class AppVersionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2555323540993364916L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "av_id")
	private Integer avId;

	@Column(name = "force_update")
	private Integer forceUpdate = 1;

	@Column(name = "os_type")
	private String osType;

	@Column(name = "app_version")
	private Float appVersion = 1f;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "bundle_id")
	private String bundleId;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "message")
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
