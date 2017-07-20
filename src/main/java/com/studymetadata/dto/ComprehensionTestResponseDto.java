package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Mohan
 *
 */
@Entity
@Table(name="comprehension_test_response")
public class ComprehensionTestResponseDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	@Column(name="comprehension_test_question_id")
	private Integer comprehensionTestQuestionId;

	@Column(name="response_option")
	private String responseOption;

	@Column(name="correct_answer")
	private Integer correctAnswer;

	@Column(name = "study_version")
	private Integer studyVersion=1;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getComprehensionTestQuestionId() {
		return comprehensionTestQuestionId;
	}

	public void setComprehensionTestQuestionId(Integer comprehensionTestQuestionId) {
		this.comprehensionTestQuestionId = comprehensionTestQuestionId;
	}

	public String getResponseOption() {
		return responseOption;
	}

	public void setResponseOption(String responseOption) {
		this.responseOption = responseOption;
	}

	public Integer getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(Integer correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	
}
