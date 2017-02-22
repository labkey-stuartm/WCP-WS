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
	
}
