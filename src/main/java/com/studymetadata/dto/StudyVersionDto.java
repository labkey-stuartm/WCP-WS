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

@Entity
@Table(name="study_version")
@NamedQueries({ 
	@NamedQuery(name = "getStudyVersionDetailsByCustomStudyId", query = "from StudyVersionDto SVDTO where SVDTO.customStudyId =:customStudyId ORDER BY SVDTO.versionId DESC LIMIT 1"),
	@NamedQuery(name = "getStudyVersionsByCustomStudyId", query = "from StudyVersionDto SVDTO where SVDTO.customStudyId =:customStudyId"),
})
public class StudyVersionDto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="version_id")
	private Integer versionId;
	
	@Column(name = "custom_study_id")
	private String customStudyId;
	
	@Column(name = "study_version")
	private Float studyVersion = 0f;
	
	@Column(name = "activity_version")
	private Float activityVersion = 0f;
	
	@Column(name = "consent_version")
	private Float consentVersion = 0f;

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public Float getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Float studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Float getActivityVersion() {
		return activityVersion;
	}

	public void setActivityVersion(Float activityVersion) {
		this.activityVersion = activityVersion;
	}

	public Float getConsentVersion() {
		return consentVersion;
	}

	public void setConsentVersion(Float consentVersion) {
		this.consentVersion = consentVersion;
	}
}
