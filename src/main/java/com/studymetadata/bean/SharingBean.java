package com.studymetadata.bean;

/**
 * Provides consent document sharing details.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:14:17 PM
 *
 */
public class SharingBean {

	private String title = "";
	private String text = "";
	private String shortDesc = "";
	private String longDesc = "";
	private String learnMore = "";
	private boolean allowWithoutSharing = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

	public String getLearnMore() {
		return learnMore;
	}

	public void setLearnMore(String learnMore) {
		this.learnMore = learnMore;
	}

	public boolean isAllowWithoutSharing() {
		return allowWithoutSharing;
	}

	public void setAllowWithoutSharing(boolean allowWithoutSharing) {
		this.allowWithoutSharing = allowWithoutSharing;
	}

}
