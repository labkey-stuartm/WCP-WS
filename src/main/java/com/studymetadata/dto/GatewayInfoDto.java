package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Provides gateway information.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:28:22 PM
 *
 */
@Entity
@Table(name = "gateway_info")
@NamedQueries({ @NamedQuery(name = "getGatewayInfo", query = " from GatewayInfoDto GWID "), })
public class GatewayInfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1150671454003333803L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "video_url")
	private String videoUrl;

	@Column(name = "email_inbox_address")
	private String emailInboxAddress;

	@Column(name = "fda_website_url")
	private String fdaWebsiteUrl;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getEmailInboxAddress() {
		return emailInboxAddress;
	}

	public void setEmailInboxAddress(String emailInboxAddress) {
		this.emailInboxAddress = emailInboxAddress;
	}

	public String getFdaWebsiteUrl() {
		return fdaWebsiteUrl;
	}

	public void setFdaWebsiteUrl(String fdaWebsiteUrl) {
		this.fdaWebsiteUrl = fdaWebsiteUrl;
	}

}
