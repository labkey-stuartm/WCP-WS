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
	
	@Column(name="short_title")
	private String shortTitle;
	
	@Column(name="question")
	private String question;
	
	@Column(name="mandatory")
	private Boolean mandatory=false;
	
	@Column(name="skip_and_return")
	private Boolean skipAndReturn=false;
	
	@Column(name="phi")
	private Boolean phi=false;
	
	@Column(name="otc")
	private Boolean otc=false;
	
	@Column(name="demographics")
	private Boolean demographics=false;
	
	@Column(name="randomize")
	private String randomize;
	
	@Column(name="data_for_health")
	private Boolean dataForHealth=false;
	
	@Column(name="health_data_type")
	private String healthDataType;
	
	@Column(name="time_range")
	private String timeRange;
	
	@Column(name="response_type")
	private Integer responseType;
	
	@Column(name="condition_definition")
	private String conditionDefinition;
	
	@Column(name="define_condition")
	private String defineCondition;
	
	@Column(name="pass_fail")
	private String passFail;
	
	@Column(name = "created_on")
	private String createdOn;
	
	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name="form_id")
	private Integer formId;
	
	@Column(name="add_line_chart")
	private String addLineChart;
	
	@Column(name="allow_rollback_chart")
	private String allowRollbackChart;
	
	@Column(name="chart_title")
	private String chartTitle;
	
	@Column(name="description")
	private String description;
	
	@Column(name="line_chart_timerange")
	private String lineChartTimerange;
	
	@Column(name="skippable")
	private String skippable;
	
	@Column(name="stat_display_name")
	private String statDisplayName;
	
	@Column(name="stat_diaplay_units")
	private String statDiaplayUnits;
	
	@Column(name="stat_formula")
	private String statFormula;
	
	@Column(name="stat_short_name")
	private String statShortName;
	
	@Column(name="stat_type")
	private String statType;
	
	@Column(name="use_stastic_data")
	private String useStasticData;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Boolean getSkipAndReturn() {
		return skipAndReturn;
	}

	public void setSkipAndReturn(Boolean skipAndReturn) {
		this.skipAndReturn = skipAndReturn;
	}

	public Boolean getPhi() {
		return phi;
	}

	public void setPhi(Boolean phi) {
		this.phi = phi;
	}

	public Boolean getOtc() {
		return otc;
	}

	public void setOtc(Boolean otc) {
		this.otc = otc;
	}

	public Boolean getDemographics() {
		return demographics;
	}

	public void setDemographics(Boolean demographics) {
		this.demographics = demographics;
	}

	public String getRandomize() {
		return randomize;
	}

	public void setRandomize(String randomize) {
		this.randomize = randomize;
	}

	public Boolean getDataForHealth() {
		return dataForHealth;
	}

	public void setDataForHealth(Boolean dataForHealth) {
		this.dataForHealth = dataForHealth;
	}

	public String getHealthDataType() {
		return healthDataType;
	}

	public void setHealthDataType(String healthDataType) {
		this.healthDataType = healthDataType;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public Integer getResponseType() {
		return responseType;
	}

	public void setResponseType(Integer responseType) {
		this.responseType = responseType;
	}

	public String getConditionDefinition() {
		return conditionDefinition;
	}

	public void setConditionDefinition(String conditionDefinition) {
		this.conditionDefinition = conditionDefinition;
	}

	public String getDefineCondition() {
		return defineCondition;
	}

	public void setDefineCondition(String defineCondition) {
		this.defineCondition = defineCondition;
	}

	public String getPassFail() {
		return passFail;
	}

	public void setPassFail(String passFail) {
		this.passFail = passFail;
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

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public String getAddLineChart() {
		return addLineChart;
	}

	public void setAddLineChart(String addLineChart) {
		this.addLineChart = addLineChart;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLineChartTimerange() {
		return lineChartTimerange;
	}

	public void setLineChartTimerange(String lineChartTimerange) {
		this.lineChartTimerange = lineChartTimerange;
	}

	public String getSkippable() {
		return skippable;
	}

	public void setSkippable(String skippable) {
		this.skippable = skippable;
	}

	public String getStatDisplayName() {
		return statDisplayName;
	}

	public void setStatDisplayName(String statDisplayName) {
		this.statDisplayName = statDisplayName;
	}

	public String getStatDiaplayUnits() {
		return statDiaplayUnits;
	}

	public void setStatDiaplayUnits(String statDiaplayUnits) {
		this.statDiaplayUnits = statDiaplayUnits;
	}

	public String getStatFormula() {
		return statFormula;
	}

	public void setStatFormula(String statFormula) {
		this.statFormula = statFormula;
	}

	public String getStatShortName() {
		return statShortName;
	}

	public void setStatShortName(String statShortName) {
		this.statShortName = statShortName;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getUseStasticData() {
		return useStasticData;
	}

	public void setUseStasticData(String useStasticData) {
		this.useStasticData = useStasticData;
	}
	
}
