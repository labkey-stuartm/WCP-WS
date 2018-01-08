package com.studymetadata.bean;

/**
 * Provides chart data source information and activity dashboard details
 * {@link DashboardActivityBean}
 * 
 * @author BTC
 * @since Jan 4, 2018 3:08:12 PM
 *
 */
public class ChartDataSourceBean {

	private String type = "";
	private String key = "";
	private DashboardActivityBean activity = new DashboardActivityBean();
	private String timeRangeType = "";
	private String startTime = "";
	private String endTime = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DashboardActivityBean getActivity() {
		return activity;
	}

	public void setActivity(DashboardActivityBean activity) {
		this.activity = activity;
	}

	public String getTimeRangeType() {
		return timeRangeType;
	}

	public void setTimeRangeType(String timeRangeType) {
		this.timeRangeType = timeRangeType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
