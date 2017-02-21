package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class ResourcesResponse {
	private String message = "FAILURE";
	private List<ResourcesBean> resources = new ArrayList<ResourcesBean>();
	
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
