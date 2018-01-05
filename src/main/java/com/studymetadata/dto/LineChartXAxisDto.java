package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides line chart x-axis configuration details.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:29:25 PM
 *
 */
@Entity
@Table(name = "line_chart_x_axis")
public class LineChartXAxisDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1502788661705421580L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "line_chart_id")
	private Integer lineChartId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getLineChartId() {
		return lineChartId;
	}

	public void setLineChartId(Integer lineChartId) {
		this.lineChartId = lineChartId;
	}

}
