package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "resources_lang")
public class ResourcesLangBO implements Serializable {

  @EmbeddedId
  private ResourcesLangPK resourcesLangPK;

  @Column(name = "sequence_no")
  private Integer sequenceNo = 0;

  @Column(name = "study_id")
  private Integer studyId;

  @Column(name = "rich_text")
  private String richText;

  @Column(name = "resource_text")
  private String resourceText;

  @Column(name = "pdf_url")
  private String pdfUrl;

  @Column(name = "pdf_name")
  private String pdfName;

  @Column(name = "title")
  private String title;

  @Column(name = "text_or_pdf", length = 1)
  private Boolean textOrPdf;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  public ResourcesLangPK getResourcesLangPK() {
    return resourcesLangPK;
  }

  public void setResourcesLangPK(ResourcesLangPK resourcesLangPK) {
    this.resourcesLangPK = resourcesLangPK;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public String getRichText() {
    return richText;
  }

  public void setRichText(String richText) {
    this.richText = richText;
  }

  public String getResourceText() {
    return resourceText;
  }

  public void setResourceText(String resourceText) {
    this.resourceText = resourceText;
  }

  public String getPdfUrl() {
    return pdfUrl;
  }

  public void setPdfUrl(String pdfUrl) {
    this.pdfUrl = pdfUrl;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public String getPdfName() {
    return pdfName;
  }

  public void setPdfName(String pdfName) {
    this.pdfName = pdfName;
  }

  public Boolean getTextOrPdf() {
    return textOrPdf;
  }

  public void setTextOrPdf(Boolean textOrPdf) {
    this.textOrPdf = textOrPdf;
  }
}
