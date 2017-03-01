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
 * @author Mohan
 *
 */
@Entity
@Table(name="consent_info")
@NamedQueries({
	/*@NamedQuery(name="consentInfoDtoByConsentId", query="from ConsentInfoDto CIDTO where CIDTO.consentId =:consentId"),*/
	@NamedQuery(name="consentInfoDtoByStudyId", query=" from ConsentInfoDto CIDTO where CIDTO.studyId =:studyId "),
})
public class ConsentInfoDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
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
	
}

