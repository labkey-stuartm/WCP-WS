package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides statistics image details for the identifier.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:31:11 PM
 *
 */
@Entity
@Table(name = "statistic_master_images")
public class StatisticImageListDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3551713191125830055L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "statistic_image_id")
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
