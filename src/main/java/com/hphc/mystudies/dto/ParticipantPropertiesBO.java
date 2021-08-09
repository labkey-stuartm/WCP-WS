package com.hphc.mystudies.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "participant_properties")
public class ParticipantPropertiesBO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "short_title")
  private String shortTitle;

  @Column(name = "property_name", length = 300)
  private String propertyName;

  @Column(name = "property_type")
  private String propertyType;

  @Column(name = "data_type")
  private String dataType;

  @Column(name = "use_as_anchor_date")
  private Boolean useAsAnchorDate;

  @Column(name = "data_source")
  private String dataSource;

  @Column(name = "populated_at_enrollment")
  private Boolean populatedAtEnrollment;

  @Column(name = "refreshed_value")
  private Boolean refreshedValue;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_date")
  private String createdDate;

  @Column(name = "modified_by")
  private Integer modifiedBy;

  @Column(name = "modified_date")
  private String modifiedDate;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "study_id")
  private Integer studyId;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "anchor_date_id")
  private Integer anchorDateId;

  @Column(name = "completed")
  private Boolean completed = false;

  @Column(name = "version")
  private Float version = 0f;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "is_change")
  private Boolean isChange = false;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "org_id")
  private String orgId = "OrgName";

  @Column(name = "is_used_in_questionnaire")
  private Boolean isUsedInQuestionnaire = false;

  @Column(name = "is_used_in_active_task")
  private Boolean isUsedInActiveTask = false;

  @Column(name = "is_used_in_resource")
  private Boolean isUsedInResource = false;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Boolean getUseAsAnchorDate() {
    return useAsAnchorDate;
  }

  public void setUseAsAnchorDate(Boolean useAsAnchorDate) {
    this.useAsAnchorDate = useAsAnchorDate;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public Boolean getPopulatedAtEnrollment() {
    return populatedAtEnrollment;
  }

  public void setPopulatedAtEnrollment(Boolean populatedAtEnrollment) {
    this.populatedAtEnrollment = populatedAtEnrollment;
  }

  public Boolean getRefreshedValue() {
    return refreshedValue;
  }

  public void setRefreshedValue(Boolean refreshedValue) {
    this.refreshedValue = refreshedValue;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public Integer getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Integer modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
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

  public Integer getAnchorDateId() {
    return anchorDateId;
  }

  public void setAnchorDateId(Integer anchorDateId) {
    this.anchorDateId = anchorDateId;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Float getVersion() {
    return version;
  }

  public void setVersion(Float version) {
    this.version = version;
  }

  public Integer getLive() {
    return live;
  }

  public void setLive(Integer live) {
    this.live = live;
  }

  public Boolean getIsChange() {
    return isChange;
  }

  public void setIsChange(Boolean isChange) {
    this.isChange = isChange;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public Boolean getIsUsedInQuestionnaire() {
    return isUsedInQuestionnaire;
  }

  public Boolean getIsUsedInActiveTask() {
    return isUsedInActiveTask;
  }

  public Boolean getIsUsedInResource() {
    return isUsedInResource;
  }

  public void setIsUsedInQuestionnaire(Boolean isUsedInQuestionnaire) {
    this.isUsedInQuestionnaire = isUsedInQuestionnaire;
  }

  public void setIsUsedInActiveTask(Boolean isUsedInActiveTask) {
    this.isUsedInActiveTask = isUsedInActiveTask;
  }

  public void setIsUsedInResource(Boolean isUsedInResource) {
    this.isUsedInResource = isUsedInResource;
  }
}
