package com.studymetadata.bean.appendix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Mohan
 *
 */
public class StepsStructureBean {
	private String type="";
	private String resultType="";
	private String key="";
	private String title="";
	private String text="";
	private String image="";
	private boolean skippable=false;
	private Map<String, Object> destinations = new HashMap<>();
	private String groupName="";
	private String phi="";
	private boolean repeatable=false;
	private String repeatableText="";
	private List<String> randomizationSets = new ArrayList<>();
	private List<ResourceContextStructureBean> resourceContext = new ArrayList<>();
	private List<StepsStructureBean> steps = new ArrayList<>();
	private Map<String, List<Object>> format = new HashMap<>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public boolean isSkippable() {
		return skippable;
	}
	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	public Map<String, Object> getDestinations() {
		return destinations;
	}
	public void setDestinations(Map<String, Object> destinations) {
		this.destinations = destinations;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getPhi() {
		return phi;
	}
	public void setPhi(String phi) {
		this.phi = phi;
	}
	public boolean isRepeatable() {
		return repeatable;
	}
	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}
	public String getRepeatableText() {
		return repeatableText;
	}
	public void setRepeatableText(String repeatableText) {
		this.repeatableText = repeatableText;
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
	}
	public List<StepsStructureBean> getSteps() {
		return steps;
	}
	public void setSteps(List<StepsStructureBean> steps) {
		this.steps = steps;
	}
	public Map<String, List<Object>> getFormat() {
		return format;
	}
	public void setFormat(Map<String, List<Object>> format) {
		this.format = format;
	}
	
}
