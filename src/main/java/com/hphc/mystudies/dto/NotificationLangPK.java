package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class NotificationLangPK implements Serializable {

  @Column(name = "notification_id")
  private Integer notificationId;

  @Column(name = "lang_code")
  private String langCode;

  public NotificationLangPK(Integer notificationId, String langCode) {
    this.notificationId = notificationId;
    this.langCode = langCode;
  }

  public NotificationLangPK() {}

  public Integer getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(Integer notificationId) {
    this.notificationId = notificationId;
  }

  public String getLangCode() {
    return langCode;
  }

  public void setLangCode(String langCode) {
    this.langCode = langCode;
  }
}
