package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "studies_lang")
public class StudyLanguageBO implements Serializable {

  @EmbeddedId private StudyLanguagePK studyLanguagePK;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "name")
  private String name;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "study_tagline")
  private String studyTagline;

  @Column(name = "description")
  private String description;

  @Column(name = "allow_rejoin_text")
  private String allowRejoinText;

  @Column(name = "media_link")
  private String mediaLink;

  @Column(name = "instructional_text")
  private Integer instructionalText;

  @Column(name = "question_text")
  private String questionText;

  @Column(name = "correct_answer")
  private Boolean correctAnswer;

  @Column(name = "consent_doc_content")
  private String consentDocContent;

  @Column(name = "agreement_of_consent")
  private String agreementOfConsent;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String full_name) {
    this.fullName = full_name;
  }

  public String getStudyTagline() {
    return studyTagline;
  }

  public void setStudyTagline(String study_tagline) {
    this.studyTagline = study_tagline;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAllowRejoinText() {
    return allowRejoinText;
  }

  public void setAllowRejoinText(String allow_rejoin_text) {
    this.allowRejoinText = allow_rejoin_text;
  }

  public String getMediaLink() {
    return mediaLink;
  }

  public void setMediaLink(String media_link) {
    this.mediaLink = media_link;
  }

  public Integer getInstructionalText() {
    return instructionalText;
  }

  public void setInstructionalText(Integer instructionalText) {
    this.instructionalText = instructionalText;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public Boolean getCorrectAnswer() {
    return correctAnswer;
  }

  public void setCorrectAnswer(Boolean correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public String getConsentDocContent() {
    return consentDocContent;
  }

  public void setConsentDocContent(String consent_doc_content) {
    this.consentDocContent = consent_doc_content;
  }

  public String getAgreementOfConsent() {
    return agreementOfConsent;
  }

  public void setAgreementOfConsent(String agreementOfConsent) {
    this.agreementOfConsent = agreementOfConsent;
  }

  public StudyLanguagePK getStudyLanguagePK() {
    return studyLanguagePK;
  }

  public void setStudyLanguagePK(StudyLanguagePK studyLanguagePK) {
    this.studyLanguagePK = studyLanguagePK;
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
