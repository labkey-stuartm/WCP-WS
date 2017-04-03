package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="active_task_master_attribute")
public class ActiveTaskMasterAttributeDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="active_task_master_attr_id")
	private Integer masterId;
	
	@Column(name = "task_type_id")
	private Integer taskTypeId;
	
	@Column(name = "order_by")
	private Integer orderByTaskType;

	@Column(name = "attribute_type")
	private String attributeType;
	
	@Column(name = "attribute_name")
	private String attributeName;
	
	@Column(name = "display_name")
	private String displayName;
	
	@Column(name = "attribute_data_type")
	private String attributeDataType;
	
	@Column(name = "add_to_dashboard")
	@Type(type="yes_no")
	private boolean addToDashboard = false;

	public Integer getMasterId() {
		return masterId;
	}

	public void setMasterId(Integer masterId) {
		this.masterId = masterId;
	}

	public Integer getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(Integer taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	public Integer getOrderByTaskType() {
		return orderByTaskType;
	}

	public void setOrderByTaskType(Integer orderByTaskType) {
		this.orderByTaskType = orderByTaskType;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAttributeDataType() {
		return attributeDataType;
	}

	public void setAttributeDataType(String attributeDataType) {
		this.attributeDataType = attributeDataType;
	}

	public boolean isAddToDashboard() {
		return addToDashboard;
	}

	public void setAddToDashboard(boolean addToDashboard) {
		this.addToDashboard = addToDashboard;
	}
}
