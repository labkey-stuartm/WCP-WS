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
 * @author BTC
 * @createdOn Jan 4, 2018 3:30:24 PM
 *
 */
@Entity
@Table(name = "question_responsetype_master_info")
public class QuestionResponsetypeMasterInfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 721954555522068688L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "anchor_date")
	private Boolean anchorDate = false;

	@Column(name = "choice_based_branching")
	private Boolean choiceBasedBranching = false;

	@Column(name = "dashboard_allowed")
	private Boolean dashboardAllowed = false;

	@Column(name = "data_type")
	private String dataType;

	@Column(name = "description")
	private String description;

	@Column(name = "formula_based_logic")
	private Boolean formulaBasedLogic = false;

	@Column(name = "healthkit_alternative")
	private Boolean healthkitAlternative = false;

	@Column(name = "response_type")
	private String responseType;

	@Column(name = "response_type_code")
	private String responseTypeCode;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getAnchorDate() {
		return anchorDate;
	}

	public void setAnchorDate(Boolean anchorDate) {
		this.anchorDate = anchorDate;
	}

	public Boolean getChoiceBasedBranching() {
		return choiceBasedBranching;
	}

	public void setChoiceBasedBranching(Boolean choiceBasedBranching) {
		this.choiceBasedBranching = choiceBasedBranching;
	}

	public Boolean getDashboardAllowed() {
		return dashboardAllowed;
	}

	public void setDashboardAllowed(Boolean dashboardAllowed) {
		this.dashboardAllowed = dashboardAllowed;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getFormulaBasedLogic() {
		return formulaBasedLogic;
	}

	public void setFormulaBasedLogic(Boolean formulaBasedLogic) {
		this.formulaBasedLogic = formulaBasedLogic;
	}

	public Boolean getHealthkitAlternative() {
		return healthkitAlternative;
	}

	public void setHealthkitAlternative(Boolean healthkitAlternative) {
		this.healthkitAlternative = healthkitAlternative;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getResponseTypeCode() {
		return responseTypeCode;
	}

	public void setResponseTypeCode(String responseTypeCode) {
		this.responseTypeCode = responseTypeCode;
	}

}
