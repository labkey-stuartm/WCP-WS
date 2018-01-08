package com.studymetadata.bean.appendix;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides question format structure details.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:20:59 PM
 *
 */
public class FormatStructureBean {

	List<String> textChoices = new ArrayList<>();
	private String unit = "";
	private int minValue = 0;
	private int maxValue = 0;
	private String placeholder = "";

	public List<String> getTextChoices() {
		return textChoices;
	}

	public void setTextChoices(List<String> textChoices) {
		this.textChoices = textChoices;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

}
