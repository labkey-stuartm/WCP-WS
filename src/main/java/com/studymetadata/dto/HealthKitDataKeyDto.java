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
 * Provides health kit keys information.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:28:43 PM
 *
 */
@Entity
@Table(name = "health_kit_keys_info")
@NamedQueries({

		@NamedQuery(name = "getHealthKitDataList", query = "from HealthKitDataKeyDto HKDKDTO"),

		@NamedQuery(name = "getHealthKitDisplayNameByKeyText", query = "select HKDKDTO.displayName"
				+ " from HealthKitDataKeyDto HKDKDTO"
				+ " where HKDKDTO.key=:key"), })
public class HealthKitDataKeyDto implements Serializable {

	private static final long serialVersionUID = 8578293542156754826L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "key_text")
	private String key;

	@Column(name = "category")
	private String category;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "result_type")
	private String resultType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
}
