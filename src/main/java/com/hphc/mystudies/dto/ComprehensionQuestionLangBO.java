package com.hphc.mystudies.dto;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "comprehension_test_question_lang")
public class ComprehensionQuestionLangBO implements Serializable {

  @EmbeddedId private ComprehensionQuestionLangPK comprehensionQuestionLangPK;

  @Column(name = "study_id")
  private Integer studyId;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "structure_of_correct_ans")
  private Boolean structureOfCorrectAns = true;

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "question_text")
  private String questionText;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      mappedBy = "comprehensionQuestionLangBO")
  private List<ComprehensionResponseLangBo> comprehensionResponseLangBoList;

  public List<ComprehensionResponseLangBo> getComprehensionResponseLangBoList() {
    return comprehensionResponseLangBoList;
  }

  public void setComprehensionResponseLangBoList(
      List<ComprehensionResponseLangBo> comprehensionResponseLangBoList) {
    this.comprehensionResponseLangBoList = comprehensionResponseLangBoList;
  }

  public ComprehensionQuestionLangPK getComprehensionQuestionLangPK() {
    return comprehensionQuestionLangPK;
  }

  public void setComprehensionQuestionLangPK(
      ComprehensionQuestionLangPK comprehensionQuestionLangPK) {
    this.comprehensionQuestionLangPK = comprehensionQuestionLangPK;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public Boolean getStructureOfCorrectAns() {
    return structureOfCorrectAns;
  }

  public void setStructureOfCorrectAns(Boolean structureOfCorrectAns) {
    this.structureOfCorrectAns = structureOfCorrectAns;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
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
}
