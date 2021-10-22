package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "eligibility_test_lang")
@NamedQueries({
    @NamedQuery(
        name = "EligibilityTestLangBo.findById",
        query =
            "SELECT ETB FROM EligibilityTestLangBo ETB WHERE ETB.active = true AND ETB.eligibilityTestLangPK.id=:eligibilityTestId ORDER BY ETB.sequenceNo"),
    @NamedQuery(
        name = "EligibilityTestLangBo.deleteById",
        query = "UPDATE EligibilityTestLangBo SET active = false WHERE eligibilityTestLangPK.id=:eligibilityTestId")
})
public class EligibilityTestLangBo implements Serializable {

  @EmbeddedId private EligibilityTestLangPK eligibilityTestLangPK;

  @Column(name = "eligibility_id")
  private Integer eligibilityId;

  @Column(name = "study_id")
  private Integer studyId;

  @Column(name = "question")
  private String question;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "status")
  private Boolean status = false;

  @Column(name = "active")
  private Boolean active = true;

  public EligibilityTestLangPK getEligibilityTestLangPK() {
    return eligibilityTestLangPK;
  }

  public void setEligibilityTestLangPK(EligibilityTestLangPK eligibilityTestLangPK) {
    this.eligibilityTestLangPK = eligibilityTestLangPK;
  }

  public Integer getEligibilityId() {
    return eligibilityId;
  }

  public void setEligibilityId(Integer eligibilityId) {
    this.eligibilityId = eligibilityId;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }
}
