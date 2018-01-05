package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides study category information for the below categories:
 * <ol>
 * <li>Categories
 * <li>Data Partner
 * <li>Research Sponsors
 * <ol>
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:30:54 PM
 *
 */
@Entity
@Table(name = "reference_tables")
public class ReferenceTablesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7979150175337442310L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "str_value")
	private String value;

	@Column(name = "category")
	private String category;

	@Column(name = "type")
	private String type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
