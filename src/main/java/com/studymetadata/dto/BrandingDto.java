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
 * 
 * @author Mohan
 * @createdOn Jan 4, 2018 3:26:01 PM
 *
 */
@Entity
@Table(name="branding")
@NamedQueries({
	
	@NamedQuery(name="brandingDetailsByStudyId", query="from BrandingDto BDTO"
			+ " where BDTO.studyId =:studyId"),
})
public class BrandingDto implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3782834655524137288L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name="study_id")
	private Integer studyId;
	
	@Column(name="background")
	private String background;
	
	@Column(name="font")
	private String font;
	
	@Column(name="tint")
	private String tint;
	
	@Column(name="logo_image_path")
	private String logoImagePath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getTint() {
		return tint;
	}

	public void setTint(String tint) {
		this.tint = tint;
	}

	public String getLogoImagePath() {
		return logoImagePath;
	}

	public void setLogoImagePath(String logoImagePath) {
		this.logoImagePath = logoImagePath;
	}
}
