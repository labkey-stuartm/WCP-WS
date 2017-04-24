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
@Table(name="questions")
public class QuestionsDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="question")
	private String question;
	
	@Column(name="description")
	private String description;
	
	@Column(name="response_type")
	private Integer responseType;
	
	@Column(name="skippable")
	private String skippable;
	
	@Column(name="add_line_chart")
	private String addLineChart;
	
	@Column(name="line_chart_timerange")
	private String lineChartTimeRange;
	
	@Column(name="allow_rollback_chart")
	private String allowRollbackChart;
	
	@Column(name="chart_title")
	private String chartTitle;
	
	@Column(name="use_stastic_data")
	private String useStasticData;
	
	@Column(name="stat_short_name")
	private String statShortName;
	
	@Column(name="stat_display_name")
	private String statDisplayName;
	
	@Column(name="stat_diaplay_units")
	private String statDisplayUnits;
	
	@Column(name="stat_type")
	private Integer statType;
	
	@Column(name="stat_formula")
	private Integer statFormula;
	
	@Column(name = "created_on")
	private String createdOn;
	
	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name = "study_version")
	private Integer studyVersion=1;
	
	@Column(name="active")
	private Boolean active;
	
	@Column(name = "short_title")
	private String shortTitle;
	
	@Column(name="status")
	private Boolean status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getResponseType() {
		return responseType;
	}

	public void setResponseType(Integer responseType) {
		this.responseType = responseType;
	}

	public String getSkippable() {
		return skippable;
	}

	public void setSkippable(String skippable) {
		this.skippable = skippable;
	}

	public String getAddLineChart() {
		return addLineChart;
	}

	public void setAddLineChart(String addLineChart) {
		this.addLineChart = addLineChart;
	}

	public String getLineChartTimeRange() {
		return lineChartTimeRange;
	}

	public void setLineChartTimeRange(String lineChartTimeRange) {
		this.lineChartTimeRange = lineChartTimeRange;
	}

	public String getAllowRollbackChart() {
		return allowRollbackChart;
	}

	public void setAllowRollbackChart(String allowRollbackChart) {
		this.allowRollbackChart = allowRollbackChart;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getUseStasticData() {
		return useStasticData;
	}

	public void setUseStasticData(String useStasticData) {
		this.useStasticData = useStasticData;
	}

	public String getStatShortName() {
		return statShortName;
	}

	public void setStatShortName(String statShortName) {
		this.statShortName = statShortName;
	}

	public String getStatDisplayName() {
		return statDisplayName;
	}

	public void setStatDisplayName(String statDisplayName) {
		this.statDisplayName = statDisplayName;
	}

	public String getStatDisplayUnits() {
		return statDisplayUnits;
	}

	public void setStatDisplayUnits(String statDisplayUnits) {
		this.statDisplayUnits = statDisplayUnits;
	}

	public Integer getStatType() {
		return statType;
	}

	public void setStatType(Integer statType) {
		this.statType = statType;
	}

	public Integer getStatFormula() {
		return statFormula;
	}

	public void setStatFormula(Integer statFormula) {
		this.statFormula = statFormula;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
