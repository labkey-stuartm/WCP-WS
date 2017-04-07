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
@Table(name="questionnaires_steps")
public class QuestionnairesStepsDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="step_id")
	private Integer stepId;
	
	@Column(name="questionnaires_id")
	private Integer questionnairesId;
	
	@Column(name="instruction_form_id")
	private Integer instructionFormId;
	
	@Column(name="step_type")
	private String stepType;
	
	@Column(name="randomization")
	private String randomization;

	@Column(name="sequence_no")
	private Integer sequenceNo;
	
	@Column(name="id")
	private Integer id;

	@Column(name="repeatable")
	private String repeatable;
	
	@Column(name="repeatable_text")
	private String repeatableText;
	
	@Column(name="skiappable")
	private String skiappable;
	
	@Column(name="step_short_title")
	private String stepShortTitle;
	
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

	public String getRandomization() {
		return randomization;
	}

	public void setRandomization(String randomization) {
		this.randomization = randomization;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSkiappable() {
		return skiappable;
	}

	public void setSkiappable(String skiappable) {
		this.skiappable = skiappable;
	}

	public String getStepShortTitle() {
		return stepShortTitle;
	}

	public void setStepShortTitle(String stepShortTitle) {
		this.stepShortTitle = stepShortTitle;
	}

}
