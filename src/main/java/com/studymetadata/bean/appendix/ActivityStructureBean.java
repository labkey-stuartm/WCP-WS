package com.studymetadata.bean.appendix;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.bean.ActivityMetadataBean;
import com.studymetadata.bean.ActivityStepsBean;

/**
 * 
 * @author Mohan
 *
 */
public class ActivityStructureBean {
	private String type = "";
	private ActivityMetadataBean metadata = new ActivityMetadataBean();
	private List<ActivityStepsBean> steps = new ArrayList<>();
	
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
	public List<ActivityStepsBean> getSteps() {
		return steps;
	}
	public void setSteps(List<ActivityStepsBean> steps) {
		this.steps = steps;
	}
}
