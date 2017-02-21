package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class StudyInfoResponse {
	private String message = "FAILURE";
	private List<InfoBean> info = new ArrayList<InfoBean>();
	private BrandingBean branding = new BrandingBean();
	
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
	public BrandingBean getBranding() {
		return branding;
	}
	public void setBranding(BrandingBean branding) {
		this.branding = branding;
	}
	
	
}
