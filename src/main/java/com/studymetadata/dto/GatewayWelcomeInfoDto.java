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
 * Provides gateway welcome information.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:28:36 PM
 *
 */
@Entity
@Table(name = "gateway_welcome_info")
@NamedQueries({

@NamedQuery(name = "getGatewayWelcomeInfoList", query = "from GatewayWelcomeInfoDto GWWID "), })
public class GatewayWelcomeInfoDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5468183451984469709L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "app_title")
	private String appTitle;

	@Column(name = "description")
	private String description;

	@Column(name = "image_path")
	private String imagePath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
