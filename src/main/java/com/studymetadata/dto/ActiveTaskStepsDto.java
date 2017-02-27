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
@Table(name="active_task_steps")
public class ActiveTaskStepsDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="step_id")
	private Integer step_id;
	
	@Column(name="active_task_id")
	private Integer activeTaskId;
	
	@Column(name="active_task_stepscol")
	private String activeTaskStepscol;
	
	@Column(name="sd_live_form_id")
	private String sdLiveFormId;
	
	@Column(name="sequence_no")
	private Integer sequenceNo;

	public Integer getStep_id() {
		return step_id;
	}

	public void setStep_id(Integer step_id) {
		this.step_id = step_id;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public String getActiveTaskStepscol() {
		return activeTaskStepscol;
	}

	public void setActiveTaskStepscol(String activeTaskStepscol) {
		this.activeTaskStepscol = activeTaskStepscol;
	}

	public String getSdLiveFormId() {
		return sdLiveFormId;
	}

	public void setSdLiveFormId(String sdLiveFormId) {
		this.sdLiveFormId = sdLiveFormId;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
}
