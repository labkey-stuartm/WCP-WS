package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class GatewayInfoResponse{
	private String message = "FAILURE";
	private List<InfoBean> info = new ArrayList<>();
	private List<GatewayInfoResourceBean> resources = new ArrayList<>();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<InfoBean> getInfo() {
		return info;
	}
	public void setInfo(List<InfoBean> info) {
		this.info = info;
	}
	public List<GatewayInfoResourceBean> getResources() {
		return resources;
	}
	public void setResources(List<GatewayInfoResourceBean> resources) {
		this.resources = resources;
	}
	
}
