package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "active_task_lang")
public class ActiveTaskLangBO implements Serializable {

  @EmbeddedId private ActiveTaskLangPK activeTaskLangPK;

  @Column(name = "study_id")
  private Integer studyId;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "instruction")
  private String instruction;

  @Column(name = "stat_name")
  private String statName;

  @Column(name = "stat_name2")
  private String statName2;

  @Column(name = "stat_name3")
  private String statName3;

  @Column(name = "chart_title")
  private String chartTitle;

  @Column(name = "chart_title2")
  private String chartTitle2;

  @Column(name = "chart_title3")
  private String chartTitle3;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "display_units_stat")
  private String displayUnitStat;

  @Column(name = "display_units_stat2")
  private String displayUnitStat2;

  @Column(name = "display_units_stat3")
  private String displayUnitStat3;

  public ActiveTaskLangPK getActiveTaskLangPK() {
    return activeTaskLangPK;
  }

  public void setActiveTaskLangPK(ActiveTaskLangPK activeTaskLangPK) {
    this.activeTaskLangPK = activeTaskLangPK;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getChartTitle2() {
    return chartTitle2;
  }

  public void setChartTitle2(String chartTitle2) {
    this.chartTitle2 = chartTitle2;
  }

  public String getChartTitle3() {
    return chartTitle3;
  }

  public void setChartTitle3(String chartTitle3) {
    this.chartTitle3 = chartTitle3;
  }

  public String getInstruction() {
    return instruction;
  }

  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public String getStatName() {
    return statName;
  }

  public void setStatName(String statName) {
    this.statName = statName;
  }

  public String getStatName2() {
    return statName2;
  }

  public void setStatName2(String statName2) {
    this.statName2 = statName2;
  }

  public String getStatName3() {
    return statName3;
  }

  public void setStatName3(String statName3) {
    this.statName3 = statName3;
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

  public String getChartTitle() {
    return chartTitle;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public String getDisplayUnitStat() {
    return displayUnitStat;
  }

  public void setDisplayUnitStat(String displayUnitStat) {
    this.displayUnitStat = displayUnitStat;
  }

  public String getDisplayUnitStat2() {
    return displayUnitStat2;
  }

  public void setDisplayUnitStat2(String displayUnitStat2) {
    this.displayUnitStat2 = displayUnitStat2;
  }

  public String getDisplayUnitStat3() {
    return displayUnitStat3;
  }

  public void setDisplayUnitStat3(String displayUnitStat3) {
    this.displayUnitStat3 = displayUnitStat3;
  }
}
