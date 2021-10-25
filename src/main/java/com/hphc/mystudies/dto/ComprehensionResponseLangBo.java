package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comprehension_test_response_lang")
public class ComprehensionResponseLangBo implements Serializable {

  @EmbeddedId private ComprehensionResponseLangPK comprehensionResponseLangPK;

  @Column(name = "question_id")
  private Integer questionId;

  @Column(name = "response_option")
  private String responseOption;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "question_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "lang_code",
        referencedColumnName = "lang_code",
        insertable = false,
        updatable = false)
  })
  private ComprehensionQuestionLangBO comprehensionQuestionLangBO;

  public ComprehensionQuestionLangBO getComprehensionQuestionLangBO() {
    return comprehensionQuestionLangBO;
  }

  public void setComprehensionQuestionLangBO(
      ComprehensionQuestionLangBO comprehensionQuestionLangBO) {
    this.comprehensionQuestionLangBO = comprehensionQuestionLangBO;
  }

  public ComprehensionResponseLangPK getComprehensionResponseLangPK() {
    return comprehensionResponseLangPK;
  }

  public void setComprehensionResponseLangPK(
      ComprehensionResponseLangPK comprehensionResponseLangPK) {
    this.comprehensionResponseLangPK = comprehensionResponseLangPK;
  }

  public Integer getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Integer questionId) {
    this.questionId = questionId;
  }

  public String getResponseOption() {
    return responseOption;
  }

  public void setResponseOption(String responseOption) {
    this.responseOption = responseOption;
  }
}
