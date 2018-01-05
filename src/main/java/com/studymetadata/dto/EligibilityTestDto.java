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
 * @author BTC
 * @createdOn Jan 4, 2018 3:27:50 PM
 *
 */
@Entity
@Table(name = "eligibility_test")
public class EligibilityTestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6692773747185719256L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "eligibility_id")
	private Integer eligibilityId;

	@Column(name = "short_title")
	private String shortTitle;

	@Column(name = "question")
	private String question;

	@Column(name = "response_format")
	private String responseFormat;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "status")
	private Boolean status = false;

	@Column(name = "eligibility_test")
	private Integer eligibilityTest;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "active")
	private Boolean active = false;

	@Column(name = "response_yes_option")
	private Boolean responseYesOption = false;

	@Column(name = "response_no_option")
	private Boolean responseNoOption = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEligibilityId() {
		return eligibilityId;
	}

	public void setEligibilityId(Integer eligibilityId) {
		this.eligibilityId = eligibilityId;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getEligibilityTest() {
		return eligibilityTest;
	}

	public void setEligibilityTest(Integer eligibilityTest) {
		this.eligibilityTest = eligibilityTest;
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

	public Boolean getResponseYesOption() {
		return responseYesOption;
	}

	public void setResponseYesOption(Boolean responseYesOption) {
		this.responseYesOption = responseYesOption;
	}

	public Boolean getResponseNoOption() {
		return responseNoOption;
	}

	public void setResponseNoOption(Boolean responseNoOption) {
		this.responseNoOption = responseNoOption;
	}

}
