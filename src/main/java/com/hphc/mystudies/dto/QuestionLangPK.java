package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class QuestionLangPK implements Serializable {

  @Column(name = "id")
  private Integer id;

  @Column(name = "lang_code")
  private String langCode;

  public QuestionLangPK(Integer id, String langCode) {
    this.id = id;
    this.langCode = langCode;
  }

  public QuestionLangPK() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLangCode() {
    return langCode;
  }

  public void setLangCode(String langCode) {
    this.langCode = langCode;
  }
}
