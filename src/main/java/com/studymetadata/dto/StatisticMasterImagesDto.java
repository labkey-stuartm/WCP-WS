package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:31:17 PM
 *
 */
@Entity
@Table(name="statistic_master_images")
public class StatisticMasterImagesDto implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2694988134032652377L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="statistic_image_id")
	private Integer statisticImageId;
	
	@Column(name = "value")
	private String value;

	public Integer getStatisticImageId() {
		return statisticImageId;
	}

	public void setStatisticImageId(Integer statisticImageId) {
		this.statisticImageId = statisticImageId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
