package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides activity frequency information {@link ActivityFrequencyScheduleBean}
 * and frequency type
 * 
 * @author BTC
 * @since Jan 4, 2018 3:05:06 PM
 *
 */
public class ActivityFrequencyBean {

	private String type = "";
	private List<ActivityFrequencyScheduleBean> runs = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ActivityFrequencyScheduleBean> getRuns() {
		return runs;
	}

	public void setRuns(List<ActivityFrequencyScheduleBean> runs) {
		this.runs = runs;
	}

}
