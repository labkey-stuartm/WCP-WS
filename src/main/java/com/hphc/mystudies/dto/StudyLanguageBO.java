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

  @Column(name = "research_sponsor")
  private String researchSponsor;

  @Column(name = "category")
  private String category;

  @Column(name = "allow_rejoin_text")
  private String allowRejoinText;

  @Column(name = "media_link")
  private String mediaLink;

  @Column(name = "instructional_text")
  private String instructionalText;

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

  // e-consent page columns starts
  @Column(name = "e_consent_title")
  private String eConsentTitle;

  @Column(name = "tagline_description")
  private String taglineDescription;

  @Column(name = "short_description")
  private String shortDescription;

  @Column(name = "long_description")
  private String longDescription;

  @Column(name = "learn_more_text")
  private String learnMoreText;

  @Column(name = "signature_one")
  private String signatureOne;

  @Column(name = "signature_two")
  private String signatureTwo;

  @Column(name = "signature_three")
  private String signatureThree;
  // e-consent page columns ends

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

  public String getInstructionalText() {
    return instructionalText;
  }

  public void setInstructionalText(String instructionalText) {
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

  public String getResearchSponsor() {
    return researchSponsor;
  }

  public void setResearchSponsor(String researchSponsor) {
    this.researchSponsor = researchSponsor;
  }

  public String geteConsentTitle() {
    return eConsentTitle;
  }

  public void seteConsentTitle(String eConsentTitle) {
    this.eConsentTitle = eConsentTitle;
  }

  public String getTaglineDescription() {
    return taglineDescription;
  }

  public void setTaglineDescription(String taglineDescription) {
    this.taglineDescription = taglineDescription;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public String getLearnMoreText() {
    return learnMoreText;
  }

  public void setLearnMoreText(String learnMoreText) {
    this.learnMoreText = learnMoreText;
  }

  public String getSignatureOne() {
    return signatureOne;
  }

  public void setSignatureOne(String signatureOne) {
    this.signatureOne = signatureOne;
  }

  public String getSignatureTwo() {
    return signatureTwo;
  }

  public void setSignatureTwo(String signatureTwo) {
    this.signatureTwo = signatureTwo;
  }

  public String getSignatureThree() {
    return signatureThree;
  }

  public void setSignatureThree(String signatureThree) {
    this.signatureThree = signatureThree;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
