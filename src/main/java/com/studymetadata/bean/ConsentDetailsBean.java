package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class ConsentDetailsBean {
	private String version = "";
	private List<ConsentBean> visualScreens = new ArrayList<ConsentBean>();
	private ComprehensionDetailsBean comprehension = new ComprehensionDetailsBean();
	private SharingBean sharing = new SharingBean();
	private ReviewBean review = new ReviewBean();
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<ConsentBean> getVisualScreens() {
		return visualScreens;
	}
	public void setVisualScreens(List<ConsentBean> visualScreens) {
		this.visualScreens = visualScreens;
	}
	public ComprehensionDetailsBean getComprehension() {
		return comprehension;
	}
	public void setComprehension(ComprehensionDetailsBean comprehension) {
		this.comprehension = comprehension;
	}
	public SharingBean getSharing() {
		return sharing;
	}
	public void setSharing(SharingBean sharing) {
		this.sharing = sharing;
	}
	public ReviewBean getReview() {
		return review;
	}
	public void setReview(ReviewBean review) {
		this.review = review;
	}
	
}
