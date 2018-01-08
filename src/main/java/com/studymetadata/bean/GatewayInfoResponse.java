package com.studymetadata.bean;

import java.util.ArrayList;
import java.util.List;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * Provides gateway details {@link InfoBean} and resources
 * {@link GatewayInfoResourceBean}
 * 
 * @author BTC
 * @since Jan 4, 2018 3:11:52 PM
 *
 */
public class GatewayInfoResponse {

	private String message = StudyMetaDataConstants.FAILURE;
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
