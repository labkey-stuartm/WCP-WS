package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mohan
 *
 */
public class ActivityFrequencyBean {
	private String type = "";
	private List<ActivityFrequencyScheduleBean> runs = new ArrayList<ActivityFrequencyScheduleBean>();
	
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
