package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StudyLanguagePK implements Serializable {

  @Column(name = "study_id")
  private Integer study_id;

  @Column(name = "lang_code")
  private String langCode;

  public StudyLanguagePK() {}

  public StudyLanguagePK(Integer study_id, String langCode) {
    this.study_id = study_id;
    this.langCode = langCode;
  }

  public Integer getStudy_id() {
    return study_id;
  }

  public void setStudy_id(Integer study_id) {
    this.study_id = study_id;
  }

  public String getLangCode() {
    return langCode;
  }

  public void setLangCode(String langCode) {
    this.langCode = langCode;
  }
}
