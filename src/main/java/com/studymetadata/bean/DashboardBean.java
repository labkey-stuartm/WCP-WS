package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:10:35 PM
 *
 */
public class DashboardBean {

	private List<StatisticsBean> statistics = new ArrayList<>();
	private List<ChartsBean> charts = new ArrayList<>();

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
