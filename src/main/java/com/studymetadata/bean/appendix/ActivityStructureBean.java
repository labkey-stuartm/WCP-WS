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
	private List<ActivityStepsBean> steps = new ArrayList<ActivityStepsBean>();
	
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
	
	/*private String type="";
	private InfoStructureBean info = new InfoStructureBean();
	private QuestionnaireConfigurationStructureBean questionnaireConfiguration = new QuestionnaireConfigurationStructureBean();
	private List<StepsStructureBean> steps = new ArrayList<StepsStructureBean>();
	private List<String> randomizationSets = new ArrayList<String>();
	private List<ResourceContextStructureBean> resourceContext = new ArrayList<ResourceContextStructureBean>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public InfoStructureBean getInfo() {
		return info;
	}
	public void setInfo(InfoStructureBean info) {
		this.info = info;
	}
	public QuestionnaireConfigurationStructureBean getQuestionnaireConfiguration() {
		return questionnaireConfiguration;
	}
	public void setQuestionnaireConfiguration(
			QuestionnaireConfigurationStructureBean questionnaireConfiguration) {
		this.questionnaireConfiguration = questionnaireConfiguration;
	}
	public List<StepsStructureBean> getSteps() {
		return steps;
	}
	public void setSteps(List<StepsStructureBean> steps) {
		this.steps = steps;
	}
	public List<String> getRandomizationSets() {
		return randomizationSets;
	}
	public void setRandomizationSets(List<String> randomizationSets) {
		this.randomizationSets = randomizationSets;
	}
	public List<ResourceContextStructureBean> getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(
			List<ResourceContextStructureBean> resourceContext) {
		this.resourceContext = resourceContext;
	}*/
	
}
