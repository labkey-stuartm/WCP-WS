/*
 * Copyright � 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides active task steps information. For example, activity type, format
 * details, title of activity, destinations metadata {@link DestinationBean} and
 * steps details {@link QuestionnaireActivityStepsBean}
 * 
 * @author BTC
 *
 */
public class ActiveTaskActivityStepsBean {

	private String type = "";
	private String resultType = "";
	private String key = "";
	private String text = "";
	private String[] options = new String[0];
	private Object format = new Object();
	private String title = "";
	private Boolean skippable = false;
	private String groupName = "";
	private Boolean repeatable = false;
	private String repeatableText = "";
	private List<DestinationBean> destinations = new ArrayList<>();
	private String healthDataKey = "";
	private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public String getTitle() {
		return title;
	}

	public Object getFormat() {
		return format;
	}

	public void setFormat(Object format) {
		this.format = format;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getSkippable() {
		return skippable;
	}

	public void setSkippable(Boolean skippable) {
		this.skippable = skippable;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	public String getRepeatableText() {
		return repeatableText;
	}

	public void setRepeatableText(String repeatableText) {
		this.repeatableText = repeatableText;
	}

	public List<DestinationBean> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<DestinationBean> destinations) {
		this.destinations = destinations;
	}

	public String getHealthDataKey() {
		return healthDataKey;
	}

	public void setHealthDataKey(String healthDataKey) {
		this.healthDataKey = healthDataKey;
	}

	public List<QuestionnaireActivityStepsBean> getSteps() {
		return steps;
	}

	public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
		this.steps = steps;
	}

}
