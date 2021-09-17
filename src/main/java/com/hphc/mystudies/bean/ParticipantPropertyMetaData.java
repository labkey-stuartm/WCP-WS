package com.hphc.mystudies.bean;

public class ParticipantPropertyMetaData {
  private String propertyId = "", propertyName = "", propertyType = "", propertyDataType = "";
  private boolean shouldRefresh = false;
  private String dataSource = "", status = "", version = "";

  public String getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(String propertyId) {
    this.propertyId = propertyId;
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

  public String getPropertyDataType() {
    return propertyDataType;
  }

  public void setPropertyDataType(String propertyDataType) {
    this.propertyDataType = propertyDataType;
  }

  public boolean isShouldRefresh() {
    return shouldRefresh;
  }

  public void setShouldRefresh(boolean shouldRefresh) {
    this.shouldRefresh = shouldRefresh;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
