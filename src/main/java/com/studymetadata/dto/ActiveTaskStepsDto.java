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
 * @createdOn Jan 4, 2018 3:25:44 PM
 *
 */
@Entity
@Table(name = "active_task_steps")
public class ActiveTaskStepsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8677367389857232011L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "step_id")
	private Integer stepId;

	@Column(name = "active_task_id")
	private Integer activeTaskId;

	@Column(name = "active_task_stepscol")
	private String activeTaskStepscol;

	@Column(name = "sd_live_form_id")
	private String sdLiveFormId;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

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

	public Integer getStepId() {
		return stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

}
