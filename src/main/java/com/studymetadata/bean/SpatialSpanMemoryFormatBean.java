/**
 * 
 */
package com.studymetadata.bean;

/**
 * @author BTC
 * @createdOn Nov 3, 2017 12:20:02 PM
 *
 */
public class SpatialSpanMemoryFormatBean {

	private Integer initialSpan = 0;
	private Integer minimumSpan = 0;
	private Integer maximumSpan = 0;
	private Float playSpeed = 0F;
	private Integer maximumTests = 0;
	private Integer maximumConsecutiveFailures = 0;
	private String customTargetImage = "";
	private String customTargetPluralName = "";
	private boolean requireReversal = false;

	public Integer getInitialSpan() {
		return initialSpan;
	}

	public void setInitialSpan(Integer initialSpan) {
		this.initialSpan = initialSpan;
	}

	public Integer getMinimumSpan() {
		return minimumSpan;
	}

	public void setMinimumSpan(Integer minimumSpan) {
		this.minimumSpan = minimumSpan;
	}

	public Integer getMaximumSpan() {
		return maximumSpan;
	}

	public void setMaximumSpan(Integer maximumSpan) {
		this.maximumSpan = maximumSpan;
	}

	public Float getPlaySpeed() {
		return playSpeed;
	}

	public void setPlaySpeed(Float playSpeed) {
		this.playSpeed = playSpeed;
	}

	public Integer getMaximumTests() {
		return maximumTests;
	}

	public void setMaximumTests(Integer maximumTests) {
		this.maximumTests = maximumTests;
	}

	public Integer getMaximumConsecutiveFailures() {
		return maximumConsecutiveFailures;
	}

	public void setMaximumConsecutiveFailures(Integer maximumConsecutiveFailures) {
		this.maximumConsecutiveFailures = maximumConsecutiveFailures;
	}

	public String getCustomTargetImage() {
		return customTargetImage;
	}

	public void setCustomTargetImage(String customTargetImage) {
		this.customTargetImage = customTargetImage;
	}

	public String getCustomTargetPluralName() {
		return customTargetPluralName;
	}

	public void setCustomTargetPluralName(String customTargetPluralName) {
		this.customTargetPluralName = customTargetPluralName;
	}

	public boolean isRequireReversal() {
		return requireReversal;
	}

	public void setRequireReversal(boolean requireReversal) {
		this.requireReversal = requireReversal;
	}

}
