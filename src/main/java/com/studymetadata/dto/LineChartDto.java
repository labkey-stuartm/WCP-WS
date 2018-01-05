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
 * @createdOn Jan 4, 2018 3:29:18 PM
 *
 */
@Entity
@Table(name = "line_chart")
public class LineChartDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8093637693491035141L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "line_chartcol")
	private String lineChartcol;

	@Column(name = "no_data_text")
	private String noDataText;

	@Column(name = "show_ver_hor_line")
	private Integer showVerHorLine;

	@Column(name = "x_axis_color")
	private String xAxisColor;

	@Column(name = "y_axis_color")
	private String yAxisColor;

	@Column(name = "animation_needed")
	private Integer animationNeeded;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLineChartcol() {
		return lineChartcol;
	}

	public void setLineChartcol(String lineChartcol) {
		this.lineChartcol = lineChartcol;
	}

	public String getNoDataText() {
		return noDataText;
	}

	public void setNoDataText(String noDataText) {
		this.noDataText = noDataText;
	}

	public Integer getShowVerHorLine() {
		return showVerHorLine;
	}

	public void setShowVerHorLine(Integer showVerHorLine) {
		this.showVerHorLine = showVerHorLine;
	}

	public String getxAxisColor() {
		return xAxisColor;
	}

	public void setxAxisColor(String xAxisColor) {
		this.xAxisColor = xAxisColor;
	}

	public String getyAxisColor() {
		return yAxisColor;
	}

	public void setyAxisColor(String yAxisColor) {
		this.yAxisColor = yAxisColor;
	}

	public Integer getAnimationNeeded() {
		return animationNeeded;
	}

	public void setAnimationNeeded(Integer animationNeeded) {
		this.animationNeeded = animationNeeded;
	}

}
