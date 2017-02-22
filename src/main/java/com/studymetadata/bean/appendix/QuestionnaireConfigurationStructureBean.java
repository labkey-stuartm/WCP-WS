package com.studymetadata.bean.appendix;

/**
 * 
 * @author Mohan
 *
 */
public class QuestionnaireConfigurationStructureBean {
	private boolean branching=false;
	private boolean randomization=false;
	private String frequency="";
	
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
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
}
