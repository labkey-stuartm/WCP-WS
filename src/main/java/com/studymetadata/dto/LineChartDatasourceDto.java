package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides line chart datasource details for the activity.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:29:09 PM
 *
 */
@Entity
@Table(name = "line_chart_datasource")
public class LineChartDatasourceDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5758103951133634472L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "data_source_id")
	private Integer dataSourceId;

	@Column(name = "plot_color")
	private String plot_color;

	@Column(name = "line_chart_id")
	private Integer lineChartId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Integer dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public String getPlot_color() {
		return plot_color;
	}

	public void setPlot_color(String plot_color) {
		this.plot_color = plot_color;
	}

	public Integer getLineChartId() {
		return lineChartId;
	}

	public void setLineChartId(Integer lineChartId) {
		this.lineChartId = lineChartId;
	}

}
