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
@Table(name="active_task_frequencies")
public class ActiveTaskFrequencyDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="active_task_id")
	private Integer activeTaskId;
	
	@Column(name="frequency_date")
	private String frequencyDate;
	
	@Column(name="frequency_time")
	private String frequencyTime;
	
	@Column(name="is_launch_study")
	private boolean isLaunchStudy;
	
	@Column(name="is_study_life_time")
	private boolean isStudyLifeTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public String getFrequencyDate() {
		return frequencyDate;
	}

	public void setFrequencyDate(String frequencyDate) {
		this.frequencyDate = frequencyDate;
	}

	public String getFrequencyTime() {
		return frequencyTime;
	}

	public void setFrequencyTime(String frequencyTime) {
		this.frequencyTime = frequencyTime;
	}

	public boolean isLaunchStudy() {
		return isLaunchStudy;
	}

	public void setLaunchStudy(boolean isLaunchStudy) {
		this.isLaunchStudy = isLaunchStudy;
	}

	public boolean isStudyLifeTime() {
		return isStudyLifeTime;
	}

	public void setStudyLifeTime(boolean isStudyLifeTime) {
		this.isStudyLifeTime = isStudyLifeTime;
	}
	
}
