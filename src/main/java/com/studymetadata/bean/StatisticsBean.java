package com.studymetadata.bean;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:14:37 PM
 *
 */
public class StatisticsBean {

	private String title = "";
	private String displayName = "";
	private String statType = "";
	private String unit = "";
	private String calculation = "";
	private StatisticsDataSourceBean dataSource = new StatisticsDataSourceBean();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCalculation() {
		return calculation;
	}

	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}

	public StatisticsDataSourceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(StatisticsDataSourceBean dataSource) {
		this.dataSource = dataSource;
	}

}
