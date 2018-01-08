package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides active task details. i.e. type of activity, metadata information
 * {@link ActivityMetadataBean} and steps details
 * {@link ActiveTaskActivityStepsBean}.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:04:40 PM
 *
 */
public class ActiveTaskActivityStructureBean {

	private String type = "";
	private ActivityMetadataBean metadata = new ActivityMetadataBean();
	private List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ActivityMetadataBean getMetadata() {
		return metadata;
	}

	public void setMetadata(ActivityMetadataBean metadata) {
		this.metadata = metadata;
	}

	public List<ActiveTaskActivityStepsBean> getSteps() {
		return steps;
	}

	public void setSteps(List<ActiveTaskActivityStepsBean> steps) {
		this.steps = steps;
	}

}
