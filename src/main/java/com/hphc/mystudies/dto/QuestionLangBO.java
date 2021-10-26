package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "question_lang")
public class QuestionLangBO implements Serializable {

  @EmbeddedId private QuestionLangPK questionLangPK;

  @Column(name = "questionnaire_id")
  private Integer questionnaireId;

  @Column(name = "form_id")
  private Integer formId;

  @Column(name = "question")
  private String question;

  @Column(name = "description")
  private String description;

  @Column(name = "stat_display_name")
  private String statDisplayName;

  @Column(name = "stat_diaplay_units")
  private String statDisplayUnits;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "status")
  private Boolean status= false;

  @Column(name = "min_desc")
  private String minDescription;

  @Column(name = "max_desc")
  private String maxDescription;

  @Column(name = "display_text")
  private String displayText;

  @Column(name = "placeholder_text")
  private String placeholderText;

  @Column(name = "unit")
  private String unit;

  @Column(name = "invalid_message")
  private String invalidMessage;

  @Column(name = "except_text")
  private String exceptText;

  @Column(name = "response_type_id")
  private Integer responseTypeId;

  @Column(name = "chart_title")
  private String chartTitle;

  @Column(name = "other_text")
  private String otherText;

  @Column(name = "other_placeholder_text")
  private String otherPlaceholderText;

  @Column(name = "text_choice_description")
  private String textChoiceDescription;

  @Column(name = "other_description")
  private String otherDescription;

  public String getOtherText() {
    return otherText;
  }

  public void setOtherText(String otherText) {
    this.otherText = otherText;
  }

  public QuestionLangPK getQuestionLangPK() {
    return questionLangPK;
  }

  public void setQuestionLangPK(QuestionLangPK questionLangPK) {
    this.questionLangPK = questionLangPK;
  }

  public Integer getQuestionnaireId() {
    return questionnaireId;
  }

  public void setQuestionnaireId(Integer questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatDisplayName() {
    return statDisplayName;
  }

  public void setStatDisplayName(String statDisplayName) {
    this.statDisplayName = statDisplayName;
  }

  public String getStatDisplayUnits() {
    return statDisplayUnits;
  }

  public void setStatDisplayUnits(String statDisplayUnits) {
    this.statDisplayUnits = statDisplayUnits;
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

  public String getMinDescription() {
    return minDescription;
  }

  public void setMinDescription(String minDescription) {
    this.minDescription = minDescription;
  }

  public String getMaxDescription() {
    return maxDescription;
  }

  public void setMaxDescription(String maxDescription) {
    this.maxDescription = maxDescription;
  }

  public String getDisplayText() {
    return displayText;
  }

  public void setDisplayText(String displayText) {
    this.displayText = displayText;
  }

  public String getPlaceholderText() {
    return placeholderText;
  }

  public void setPlaceholderText(String placeholderText) {
    this.placeholderText = placeholderText;
  }

  public String getInvalidMessage() {
    return invalidMessage;
  }

  public void setInvalidMessage(String invalidMessage) {
    this.invalidMessage = invalidMessage;
  }

  public String getExceptText() {
    return exceptText;
  }

  public void setExceptText(String exceptText) {
    this.exceptText = exceptText;
  }

  public String getChartTitle() {
    return chartTitle;
  }

  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  public Integer getResponseTypeId() {
    return responseTypeId;
  }

  public void setResponseTypeId(Integer responseTypeId) {
    this.responseTypeId = responseTypeId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getOtherPlaceholderText() {
    return otherPlaceholderText;
  }

  public void setOtherPlaceholderText(String otherPlaceholderText) {
    this.otherPlaceholderText = otherPlaceholderText;
  }

  public String getOtherDescription() {
    return otherDescription;
  }

  public void setOtherDescription(String otherDescription) {
    this.otherDescription = otherDescription;
  }

  public String getTextChoiceDescription() {
    return textChoiceDescription;
  }

  public void setTextChoiceDescription(String textChoiceDescription) {
    this.textChoiceDescription = textChoiceDescription;
  }

  public Integer getFormId() {
    return formId;
  }

  public void setFormId(Integer formId) {
    this.formId = formId;
  }
}
