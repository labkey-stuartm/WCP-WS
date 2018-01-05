package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:26:34 PM
 *
 */
@Entity
@Table(name = "comprehension_test_response")
@NamedQueries({

@NamedQuery(name = "comprehensionQuestionResponseByCTID", query = " from ComprehensionTestResponseDto CTRDTO"
		+ " where CTRDTO.comprehensionTestQuestionId =:comprehensionTestQuestionId"), })
public class ComprehensionTestResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8004751419746704475L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "comprehension_test_question_id")
	private Integer comprehensionTestQuestionId;

	@Column(name = "response_option")
	private String responseOption;

	@Column(name = "correct_answer")
	private Boolean correctAnswer = false;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getComprehensionTestQuestionId() {
		return comprehensionTestQuestionId;
	}

	public void setComprehensionTestQuestionId(
			Integer comprehensionTestQuestionId) {
		this.comprehensionTestQuestionId = comprehensionTestQuestionId;
	}

	public String getResponseOption() {
		return responseOption;
	}

	public void setResponseOption(String responseOption) {
		this.responseOption = responseOption;
	}

	public Boolean getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(Boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

}
