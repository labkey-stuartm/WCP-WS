package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "consent_info_lang")
public class ConsentInfoLangBO implements Serializable {

  @EmbeddedId private ConsentInfoLangPK consentInfoLangPK;

  @Column(name = "study_id")
  private Integer studyId;

  // Todo to be removed @Anoop Sharma
  @Column(name = "consent_item_type")
  private String consentItemType;

  @Column(name = "brief_summary")
  private String briefSummary;

  @Column(name = "elaborated")
  private String elaborated;

  // Todo to be removed @Anoop Sharma
  @Column(name = "visual_step")
  private String visualStep;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "display_title")
  private String displayTitle;

  @Column(name = "status")
  private Boolean status;

  // Todo to be removed @Anoop Sharma
  @Column(name = "consent_item_title_id")
  private Integer consentItemTitleId;

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "version")
  private Float version = 0f;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

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

  public Integer getLive() {
    return live;
  }

  public void setLive(Integer live) {
    this.live = live;
  }

  public Float getVersion() {
    return version;
  }

  public void setVersion(Float version) {
    this.version = version;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public Integer getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Integer modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public ConsentInfoLangPK getConsentInfoLangPK() {
    return consentInfoLangPK;
  }

  public void setConsentInfoLangPK(ConsentInfoLangPK consentInfoLangPK) {
    this.consentInfoLangPK = consentInfoLangPK;
  }
}
