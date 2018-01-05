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
 * @createdOn Jan 4, 2018 3:26:26 PM
 *
 */
@Entity
@Table(name = "comprehension_test_question")
@NamedQueries({

@NamedQuery(name = "comprehensionQuestionByStudyId", query = " from ComprehensionTestQuestionDto CTDTO"
		+ " where CTDTO.studyId =:studyId and CTDTO.status=true and CTDTO.active=true"
		+ " ORDER BY CTDTO.sequenceNo"), })
public class ComprehensionTestQuestionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6056127394260427728L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "question_text")
	private String questionText;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "structure_of_correct_ans")
	private Boolean structureOfCorrectAns = false;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private Integer modifiedOn;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "status")
	private Boolean status = false;

	@Column(name = "active")
	private Boolean active = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Boolean getStructureOfCorrectAns() {
		return structureOfCorrectAns;
	}

	public void setStructureOfCorrectAns(Boolean structureOfCorrectAns) {
		this.structureOfCorrectAns = structureOfCorrectAns;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Integer modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
