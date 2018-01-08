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
 * Provides study {@link StudyDto} pages information.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:31:44 PM
 *
 */
@Entity
@Table(name = "study_page")
@NamedQueries({

@NamedQuery(name = "studyPageDetailsByStudyId", query = "from StudyPageDto SPDTO"
		+ " where SPDTO.studyId =:studyId"), })
public class StudyPageDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1910738533774432289L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "page_id")
	private Integer pageId;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "title")
	private String title;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "description")
	private String description;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
