package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides brief description about the consent like version of consent,
 * comprehension test details {@link ComprehensionDetailsBean}, sharing details
 * {@link SharingBean} and consent review details {@link ReviewBean}
 * 
 * @author BTC
 * @since Jan 4, 2018 3:09:50 PM
 *
 */
public class ConsentDetailsBean {

	private String version = "";
	private List<ConsentBean> visualScreens = new ArrayList<>();
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
