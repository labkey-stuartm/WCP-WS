package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Provides questionnaire steps details for study {@link StudyDto}.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:30:05 PM
 *
 */
@Entity
@Table(name = "questionnaires_steps")
public class QuestionnairesStepsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626878023643784669L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "step_id")
	private Integer stepId;

	@Column(name = "questionnaires_id")
	private Integer questionnairesId;

	@Column(name = "instruction_form_id")
	private Integer instructionFormId;

	@Column(name = "step_type")
	private String stepType;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "step_short_title")
	private String stepShortTitle;

	@Column(name = "skiappable")
	private String skiappable;

	@Column(name = "destination_step")
	private Integer destinationStep;

	@Column(name = "repeatable")
	private String repeatable = "No";

	@Column(name = "repeatable_text")
	private String repeatableText;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	@Column(name = "active")
	private Boolean active;

	@Transient
	private String destinationStepType;

	public Integer getStepId() {
		return stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public Integer getQuestionnairesId() {
		return questionnairesId;
	}

	public void setQuestionnairesId(Integer questionnairesId) {
		this.questionnairesId = questionnairesId;
	}

	public Integer getInstructionFormId() {
		return instructionFormId;
	}

	public void setInstructionFormId(Integer instructionFormId) {
		this.instructionFormId = instructionFormId;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getStepShortTitle() {
		return stepShortTitle;
	}

	public void setStepShortTitle(String stepShortTitle) {
		this.stepShortTitle = stepShortTitle;
	}

	public String getSkiappable() {
		return skiappable;
	}

	public void setSkiappable(String skiappable) {
		this.skiappable = skiappable;
	}

	public Integer getDestinationStep() {
		return destinationStep;
	}

	public void setDestinationStep(Integer destinationStep) {
		this.destinationStep = destinationStep;
	}

	public String getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(String repeatable) {
		this.repeatable = repeatable;
	}

	public String getRepeatableText() {
		return repeatableText;
	}

	public void setRepeatableText(String repeatableText) {
		this.repeatableText = repeatableText;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
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

	public String getDestinationStepType() {
		return destinationStepType;
	}

	public void setDestinationStepType(String destinationStepType) {
		this.destinationStepType = destinationStepType;
	}

}
