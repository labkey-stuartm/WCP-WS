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
@Table(name="active_task_attrtibutes_values")
public class ActiveTaskAttrtibutesValuesDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="active_task_attribute_id")
	private Integer attributeValueId;
	
	@Column(name = "active_task_id")
	private Integer activeTaskId;
	
	@Column(name = "active_task_master_attr_id")
	private Integer activeTaskMasterAttrId;
	
	@Column(name = "attribute_val")
	private String attributeVal;
	
	@Column(name = "add_to_line_chart")
	@Type(type="yes_no")
	private boolean addToLineChart = false;
	
	@Column(name = "time_range_chart")
	private String timeRangeChart;
	
	@Column(name = "rollback_chat")
	private String rollbackChat;
	
	@Column(name = "title_chat")
	private String titleChat;
	
	@Column(name = "use_for_statistic")
	@Type(type="yes_no")
	private boolean useForStatistic = false;
	
	@Column(name = "identifier_name_stat")
	private String identifierNameStat;
	
	@Column(name = "display_name_stat")
	private String displayNameStat;
	
	@Column(name = "display_units_stat")
	private String displayUnitStat;
	
	@Column(name = "upload_type_stat")
	private String uploadTypeStat;
	
	@Column(name = "formula_applied_stat")
	private String formulaAppliedStat;
	
	@Column(name = "time_range_stat")
	private String timeRangeStat;

	@Column(name = "study_version")
	private Integer studyVersion=1;
	
	public Integer getAttributeValueId() {
		return attributeValueId;
	}

	public void setAttributeValueId(Integer attributeValueId) {
		this.attributeValueId = attributeValueId;
	}

	public Integer getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public Integer getActiveTaskMasterAttrId() {
		return activeTaskMasterAttrId;
	}

	public void setActiveTaskMasterAttrId(Integer activeTaskMasterAttrId) {
		this.activeTaskMasterAttrId = activeTaskMasterAttrId;
	}

	public String getAttributeVal() {
		return attributeVal;
	}

	public void setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
	}

	public boolean isAddToLineChart() {
		return addToLineChart;
	}

	public void setAddToLineChart(boolean addToLineChart) {
		this.addToLineChart = addToLineChart;
	}

	public String getTimeRangeChart() {
		return timeRangeChart;
	}

	public void setTimeRangeChart(String timeRangeChart) {
		this.timeRangeChart = timeRangeChart;
	}

	public String getRollbackChat() {
		return rollbackChat;
	}

	public void setRollbackChat(String rollbackChat) {
		this.rollbackChat = rollbackChat;
	}

	public String getTitleChat() {
		return titleChat;
	}

	public void setTitleChat(String titleChat) {
		this.titleChat = titleChat;
	}

	public boolean isUseForStatistic() {
		return useForStatistic;
	}

	public void setUseForStatistic(boolean useForStatistic) {
		this.useForStatistic = useForStatistic;
	}

	public String getIdentifierNameStat() {
		return identifierNameStat;
	}

	public void setIdentifierNameStat(String identifierNameStat) {
		this.identifierNameStat = identifierNameStat;
	}

	public String getDisplayNameStat() {
		return displayNameStat;
	}

	public void setDisplayNameStat(String displayNameStat) {
		this.displayNameStat = displayNameStat;
	}

	public String getDisplayUnitStat() {
		return displayUnitStat;
	}

	public void setDisplayUnitStat(String displayUnitStat) {
		this.displayUnitStat = displayUnitStat;
	}

	public String getUploadTypeStat() {
		return uploadTypeStat;
	}

	public void setUploadTypeStat(String uploadTypeStat) {
		this.uploadTypeStat = uploadTypeStat;
	}

	public String getFormulaAppliedStat() {
		return formulaAppliedStat;
	}

	public void setFormulaAppliedStat(String formulaAppliedStat) {
		this.formulaAppliedStat = formulaAppliedStat;
	}

	public String getTimeRangeStat() {
		return timeRangeStat;
	}

	public void setTimeRangeStat(String timeRangeStat) {
		this.timeRangeStat = timeRangeStat;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}
	
}
