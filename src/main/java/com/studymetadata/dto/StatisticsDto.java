package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides activity statistics information for study.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:31:23 PM
 *
 */
@Entity
@Table(name = "statistics")
public class StatisticsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452743894242933509L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "short_title")
	private String shortTitle;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "stat_type")
	private String statType;

	@Column(name = "display_unit")
	private String displayUnit;

	@Column(name = "formula")
	private String formula;

	@Column(name = "data_source")
	private Integer data_source;

	@Column(name = "time_range")
	private String timeRange;

	@Column(name = "custom")
	private Integer custom;

	@Column(name = "custom_start")
	private String customStart;

	@Column(name = "custom_end")
	private String customEnd;

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getDisplayUnit() {
		return displayUnit;
	}

	public void setDisplayUnit(String displayUnit) {
		this.displayUnit = displayUnit;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public Integer getData_source() {
		return data_source;
	}

	public void setData_source(Integer data_source) {
		this.data_source = data_source;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public Integer getCustom() {
		return custom;
	}

	public void setCustom(Integer custom) {
		this.custom = custom;
	}

	public String getCustomStart() {
		return customStart;
	}

	public void setCustomStart(String customStart) {
		this.customStart = customStart;
	}

	public String getCustomEnd() {
		return customEnd;
	}

	public void setCustomEnd(String customEnd) {
		this.customEnd = customEnd;
	}

}
