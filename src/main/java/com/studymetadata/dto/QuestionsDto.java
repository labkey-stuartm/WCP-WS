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
	private Integer mandatory;
	
	@Column(name="skip_and_return")
	private Integer skip_and_return;
	
	@Column(name="phi")
	private Integer phi;
	
	@Column(name="otc")
	private Integer otc;
	
	@Column(name="demographics")
	private Integer demographics;
	
	@Column(name="randomize")
	private String randomize;
	
	@Column(name="data_for_health")
	private Integer dataForHealth;
	
	@Column(name="health_data_type")
	private String healthDataType;
	
	@Column(name="time_range")
	private String timeRange;
	
	@Column(name="response_type")
	private String responseType;
	
	@Column(name="condition_definition")
	private String conditionDefinition;
	
	@Column(name="define_condition")
	private String define_Condition;
	
	@Column(name="form_id")
	private Integer formId;

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

	public Integer getMandatory() {
		return mandatory;
	}

	public void setMandatory(Integer mandatory) {
		this.mandatory = mandatory;
	}

	public Integer getSkip_and_return() {
		return skip_and_return;
	}

	public void setSkip_and_return(Integer skip_and_return) {
		this.skip_and_return = skip_and_return;
	}

	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public Integer getOtc() {
		return otc;
	}

	public void setOtc(Integer otc) {
		this.otc = otc;
	}

	public Integer getDemographics() {
		return demographics;
	}

	public void setDemographics(Integer demographics) {
		this.demographics = demographics;
	}

	public String getRandomize() {
		return randomize;
	}

	public void setRandomize(String randomize) {
		this.randomize = randomize;
	}

	public Integer getDataForHealth() {
		return dataForHealth;
	}

	public void setDataForHealth(Integer dataForHealth) {
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

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getConditionDefinition() {
		return conditionDefinition;
	}

	public void setConditionDefinition(String conditionDefinition) {
		this.conditionDefinition = conditionDefinition;
	}

	public String getDefine_Condition() {
		return define_Condition;
	}

	public void setDefine_Condition(String define_Condition) {
		this.define_Condition = define_Condition;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	
}
