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
 * @createdOn Jan 4, 2018 3:27:08 PM
 *
 */
@Entity
@Table(name = "consent_info")
@NamedQueries({

		@NamedQuery(name = "consentInfoDtoByStudyId", query = "from ConsentInfoDto CIDTO"
				+ " where CIDTO.studyId =:studyId ORDER BY CIDTO.sequenceNo"),

		@NamedQuery(name = "consentInfoDetailsByCustomStudyIdAndVersion", query = "from ConsentInfoDto CIDTO"
				+ " where CIDTO.customStudyId =:customStudyId and ROUND(CIDTO.version, 1)=:version"
				+ " ORDER BY CIDTO.sequenceNo"), })
public class ConsentInfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7398906510250618397L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "consent_item_type")
	private String consentItemType;

	@Column(name = "title")
	private String title;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "brief_summary")
	private String briefSummary;

	@Column(name = "elaborated")
	private String elaborated;

	@Column(name = "html_content")
	private String htmlContent;

	@Column(name = "url")
	private String url;

	@Column(name = "visual_step")
	private String visualStep;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "display_title")
	private String displayTitle;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "consent_item_title_id")
	private Integer consentItemTitleId;

	@Column(name = "version")
	private Float version = 0f;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "is_live")
	private Integer live = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getConsentItemType() {
		return consentItemType;
	}

	public void setConsentItemType(String consentItemType) {
		this.consentItemType = consentItemType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBriefSummary() {
		return briefSummary;
	}

	public void setBriefSummary(String briefSummary) {
		this.briefSummary = briefSummary;
	}

	public String getElaborated() {
		return elaborated;
	}

	public void setElaborated(String elaborated) {
		this.elaborated = elaborated;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVisualStep() {
		return visualStep;
	}

	public void setVisualStep(String visualStep) {
		this.visualStep = visualStep;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getConsentItemTitleId() {
		return consentItemTitleId;
	}

	public void setConsentItemTitleId(Integer consentItemTitleId) {
		this.consentItemTitleId = consentItemTitleId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Float getVersion() {
		return version;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public Integer getLive() {
		return live;
	}

	public void setLive(Integer live) {
		this.live = live;
	}

}
