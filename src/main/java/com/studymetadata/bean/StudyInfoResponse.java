package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

public class StudyInfoResponse {
	private String message = "FAILURE";
	private String studyWebsite = "";
	private List<InfoBean> info = new ArrayList<>();
	private AnchorDateBean anchorDate = new AnchorDateBean();
	private WithdrawalConfigBean withdrawalConfig = new WithdrawalConfigBean();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStudyWebsite() {
		return studyWebsite;
	}
	public void setStudyWebsite(String studyWebsite) {
		this.studyWebsite = studyWebsite;
	}
	public List<InfoBean> getInfo() {
		return info;
	}
	public void setInfo(List<InfoBean> info) {
		this.info = info;
	}
	public AnchorDateBean getAnchorDate() {
		return anchorDate;
	}
	public void setAnchorDate(AnchorDateBean anchorDate) {
		this.anchorDate = anchorDate;
	}
	public WithdrawalConfigBean getWithdrawalConfig() {
		return withdrawalConfig;
	}
	public void setWithdrawalConfig(WithdrawalConfigBean withdrawalConfig) {
		this.withdrawalConfig = withdrawalConfig;
	}
	
}
