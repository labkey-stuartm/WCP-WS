package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides form questionnaire step {@link QuestionnairesStepsDto} details.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:28:05 PM
 *
 */
@Entity
@Table(name = "form")
public class FormDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8069151978635052398L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "active")
	private Boolean active;

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
