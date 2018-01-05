package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides question response types information.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:30:47 PM
 *
 */
@Entity
@Table(name = "questions_response_type")
public class QuestionsResponseTypeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7963662243467298886L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "question_id")
	private Integer questionId;

	@Column(name = "parameter_name")
	private String parameterName;

	@Column(name = "parameter_value")
	private String parameterValue;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

}
