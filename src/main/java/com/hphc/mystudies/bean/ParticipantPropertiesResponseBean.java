package com.hphc.mystudies.bean;

import com.hphc.mystudies.util.StudyMetaDataConstants;
import java.util.ArrayList;
import java.util.List;

public class ParticipantPropertiesResponseBean {
  private String message = StudyMetaDataConstants.FAILURE;
  private ParticipantPropertiesMetadata metadata = new ParticipantPropertiesMetadata();
  private List<ParticipantPropertyMetaData> participantProperties = new ArrayList<>();

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ParticipantPropertiesMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(ParticipantPropertiesMetadata metadata) {
    this.metadata = metadata;
  }

  public List<ParticipantPropertyMetaData> getParticipantProperties() {
    return participantProperties;
  }

  public void setParticipantProperties(List<ParticipantPropertyMetaData> participantProperties) {
    this.participantProperties = participantProperties;
  }
}
