package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="response_sub_type_value")
public class QuestionResponseSubTypeDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="response_sub_type_value_id")
	private Integer responseSubTypeValueId;
	
	@Column(name="reponse_type_id")
	private Integer responseTypeId;
	
	@Column(name="text")
	private String text;
	
	@Column(name="value")
	private String value;
	
	@Column(name="detail")
	private String detail;
	
	@Column(name="exclusive")
	private Boolean exclusive;
	
	@Column(name="image")
	private String image;
	
	@Column(name="selected_image")
	private String selectedImage;
	
	@Column(name = "study_version")
	private Integer studyVersion=1;
	
	@Column(name="destination_step_id")
	private Integer destinationStepId;

	public Integer getResponseSubTypeValueId() {
		return responseSubTypeValueId;
	}

	public void setResponseSubTypeValueId(Integer responseSubTypeValueId) {
		this.responseSubTypeValueId = responseSubTypeValueId;
	}

	public Integer getResponseTypeId() {
		return responseTypeId;
	}

	public void setResponseTypeId(Integer responseTypeId) {
		this.responseTypeId = responseTypeId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Boolean getExclusive() {
		return exclusive;
	}

	public void setExclusive(Boolean exclusive) {
		this.exclusive = exclusive;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getDestinationStepId() {
		return destinationStepId;
	}

	public void setDestinationStepId(Integer destinationStepId) {
		this.destinationStepId = destinationStepId;
	}
	
}

