package com.studymetadata.bean.appendix;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:21:39 PM
 *
 */
public class QuestionnaireConfigurationStructureBean {

	private boolean branching = false;
	private boolean randomization = false;
	private List<FrequencyBean> frequency = new ArrayList<>();

	public boolean isBranching() {
		return branching;
	}

	public void setBranching(boolean branching) {
		this.branching = branching;
	}

	public boolean isRandomization() {
		return randomization;
	}

	public void setRandomization(boolean randomization) {
		this.randomization = randomization;
	}

	public List<FrequencyBean> getFrequency() {
		return frequency;
	}

	public void setFrequency(List<FrequencyBean> frequency) {
		this.frequency = frequency;
	}

}
