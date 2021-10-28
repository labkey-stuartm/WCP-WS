package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "form_lang")
public class FormLangBO implements Serializable {

  @EmbeddedId private FormLangPK formLangPK;

  @Column(name = "questionnaire_id")
  private Integer questionnaireId;

  @Column(name = "repeatable_text")
  private String repeatableText;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  public FormLangPK getFormLangPK() {
    return formLangPK;
  }

  public void setFormLangPK(FormLangPK formLangPK) {
    this.formLangPK = formLangPK;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
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

  public String getRepeatableText() {
    return repeatableText;
  }

  public void setRepeatableText(String repeatableText) {
    this.repeatableText = repeatableText;
  }

  public Integer getQuestionnaireId() {
    return questionnaireId;
  }

  public void setQuestionnaireId(Integer questionnaireId) {
    this.questionnaireId = questionnaireId;
  }
}
