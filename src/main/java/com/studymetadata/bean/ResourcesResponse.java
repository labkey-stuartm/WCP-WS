package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:13:51 PM
 *
 */
public class ResourcesResponse {
	
	private String message = StudyMetaDataConstants.FAILURE;
	private List<ResourcesBean> resources = new ArrayList<>();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ResourcesBean> getResources() {
		return resources;
	}
	public void setResources(List<ResourcesBean> resources) {
		this.resources = resources;
	}
}
