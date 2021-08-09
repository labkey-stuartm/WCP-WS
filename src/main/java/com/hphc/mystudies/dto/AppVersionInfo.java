package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "version_info")
@NamedQueries(
    value = {
      @NamedQuery(
          name = "AppVersionInfo.findAll",
          query = "FROM AppVersionInfo where appId=:appId AND orgId=:orgId"),
    })
public class AppVersionInfo implements Serializable {

  /** */
  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "version_info_id")
  private int versionInfoId;

  @Column(name = "android")
  private String androidVersion;

  @Column(name = "ios")
  private String iosVersion = "";

  @Column(name = "app_id")
  private String appId;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "android_force_update")
  private Boolean androidForceUpdate;

  @Column(name = "ios_force_update")
  private Boolean iosForceUpdate;

  public int getVersionInfoId() {
    return versionInfoId;
  }

  public void setVersionInfoId(int versionInfoId) {
    this.versionInfoId = versionInfoId;
  }

  public String getAndroidVersion() {
    return androidVersion;
  }

  public void setAndroidVersion(String androidVersion) {
    this.androidVersion = androidVersion;
  }

  public String getIosVersion() {
    return iosVersion;
  }

  public void setIosVersion(String iosVersion) {
    this.iosVersion = iosVersion;
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

  public Boolean getAndroidForceUpdate() {
    return androidForceUpdate;
  }

  public void setAndroidForceUpdate(Boolean androidForceUpdate) {
    this.androidForceUpdate = androidForceUpdate;
  }

  public Boolean getIosForceUpdate() {
    return iosForceUpdate;
  }

  public void setIosForceUpdate(Boolean iosForceUpdate) {
    this.iosForceUpdate = iosForceUpdate;
  }
}
