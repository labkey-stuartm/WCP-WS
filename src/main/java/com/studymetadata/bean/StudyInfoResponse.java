package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides study metadata information in response. i.e. study details
 * {@link InfoBean}, anchor date details {@link AnchorDateBean} and withdrawal
 * configuration details {@link WithdrawalConfigBean}.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:15:18 PM
 *
 */
public class StudyInfoResponse {

	private String message = StudyMetaDataConstants.FAILURE;
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
