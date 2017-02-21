package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class DashboardBean {
	private List<StatisticsBean> statistics = new ArrayList<StatisticsBean>();
	private List<ChartsBean> charts = new ArrayList<ChartsBean>();
	
	public List<StatisticsBean> getStatistics() {
		return statistics;
	}
	public void setStatistics(List<StatisticsBean> statistics) {
		this.statistics = statistics;
	}
	public List<ChartsBean> getCharts() {
		return charts;
	}
	public void setCharts(List<ChartsBean> charts) {
		this.charts = charts;
	}
}
