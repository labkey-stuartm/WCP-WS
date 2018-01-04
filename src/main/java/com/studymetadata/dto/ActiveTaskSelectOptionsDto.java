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
 * @createdOn Jan 4, 2018 3:25:37 PM
 *
 */
@Entity
@Table(name="active_task_select_options")
public class ActiveTaskSelectOptionsDto implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3788580522549249379L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="active_task_select_options_id")
	private Integer activeTaskSelectOptionsId;
	
	@Column(name="active_task_master_attr_id")
	private Integer activeTaskMasterAttrId;
	
	@Column(name="option_val")
	private String optionVal;

	public Integer getActiveTaskSelectOptionsId() {
		return activeTaskSelectOptionsId;
	}

	public void setActiveTaskSelectOptionsId(Integer activeTaskSelectOptionsId) {
		this.activeTaskSelectOptionsId = activeTaskSelectOptionsId;
	}

	public Integer getActiveTaskMasterAttrId() {
		return activeTaskMasterAttrId;
	}

	public void setActiveTaskMasterAttrId(Integer activeTaskMasterAttrId) {
		this.activeTaskMasterAttrId = activeTaskMasterAttrId;
	}

	public String getOptionVal() {
		return optionVal;
	}

	public void setOptionVal(String optionVal) {
		this.optionVal = optionVal;
	}
	
}
