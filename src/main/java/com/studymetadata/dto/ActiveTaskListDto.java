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
 * @createdOn Jan 4, 2018 3:25:22 PM
 *
 */
@Entity
@Table(name="active_task_list")
public class ActiveTaskListDto implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4743834604812647713L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="active_task_list_id")
	private Integer activeTaskListId;
	
	@Column(name="task_name")
	private String taskName;
	
	@Column(name="type")
	private String type;

	public Integer getActiveTaskListId() {
		return activeTaskListId;
	}

	public void setActiveTaskListId(Integer activeTaskListId) {
		this.activeTaskListId = activeTaskListId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
