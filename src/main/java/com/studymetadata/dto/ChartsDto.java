package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides activities chart configuration details.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:26:11 PM
 *
 */
@Entity
@Table(name = "charts")
public class ChartsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2394186946423804987L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "reference_id")
	private Integer referenceId;

	@Column(name = "chart_title")
	private String chartTitle;

	@Column(name = "sequence_no")
	private Integer sequenceNo;

	@Column(name = "chart_type")
	private String chartType;

	@Column(name = "time_range")
	private String timeRange;

	@Column(name = "allow_previous_next")
	private Integer allowPreviousNext;

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

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public Integer getAllowPreviousNext() {
		return allowPreviousNext;
	}

	public void setAllowPreviousNext(Integer allowPreviousNext) {
		this.allowPreviousNext = allowPreviousNext;
	}

}
