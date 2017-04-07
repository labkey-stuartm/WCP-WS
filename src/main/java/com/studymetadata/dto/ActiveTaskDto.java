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
 * @author Mohan
 *
 */
@Entity
@Table(name="active_task")
@NamedQueries({
	@NamedQuery(name="activeTaskByStudyId", query="from ActiveTaskDto ATDTO where ATDTO.studyId =:studyId"),
})
public class ActiveTaskDto implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="study_id")
	private Integer studyId;
	
	@Column(name="task_name")
	private String taskName;
	
	@Column(name="duration")
	private String duration;
	
	@Column(name="active_task_lifetime_start")
	private String activeTaskLifetimeStart;
	
	@Column(name="active_task_lifetime_end")
	private String activeTaskLifetimeEnd;
	
	@Column(name="frequency")
	private String frequency;
	
	@Column(name="repeat_questionnaire")
	private Integer repeatQuestionnaire;
	
	@Column(name="day_of_the_week")
	private String dayOfTheWeek;
	
	@Column(name="repeat_active_task")
	private Integer repeatActiveTask;
	
	@Column(name="created_by")
	private Integer createdBy;
	
	@Column(name="created_on")
	private String createdOn;
	
	@Column(name="modified_by")
	private Integer modifiedBy;
	
	@Column(name="modified_on")
	private String modifiedOn;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="instruction")
	private String instruction;
	
	@Column(name="short_title")
	private String shortTitle;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="modified_date")
	private String modifiedDate;
	
	@Column(name="task_title")
	private String taskTitle;
	
	@Column(name="task_type")
	private Integer taskType;
	
	@Column(name="action")
	private Boolean action=false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getActiveTaskLifetimeStart() {
		return activeTaskLifetimeStart;
	}

	public void setActiveTaskLifetimeStart(String activeTaskLifetimeStart) {
		this.activeTaskLifetimeStart = activeTaskLifetimeStart;
	}

	public String getActiveTaskLifetimeEnd() {
		return activeTaskLifetimeEnd;
	}

	public void setActiveTaskLifetimeEnd(String activeTaskLifetimeEnd) {
		this.activeTaskLifetimeEnd = activeTaskLifetimeEnd;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Integer getRepeatQuestionnaire() {
		return repeatQuestionnaire;
	}

	public void setRepeatQuestionnaire(Integer repeatQuestionnaire) {
		this.repeatQuestionnaire = repeatQuestionnaire;
	}

	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public Integer getRepeatActiveTask() {
		return repeatActiveTask;
	}

	public void setRepeatActiveTask(Integer repeatActiveTask) {
		this.repeatActiveTask = repeatActiveTask;
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

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}
	
}
