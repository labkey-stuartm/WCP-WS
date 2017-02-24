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
 *
 */
@Entity
@Table(name="consent")
@NamedQueries({
	@NamedQuery(name="consentDtoByStudyId", query=" from ConsentDto CDTO where CDTO.studyId =:studyId "),
})
public class ConsentDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "study_id")
	private Integer studyId;
	
	@Column(name = "comprehension_test_minimum_score")
	private Integer comprehensionTestMinimumScore;
	
	@Column(name = "share_data_permissions")
	private Integer shareDataPermissions;
	
	@Column(name = "text_of_the_permission")
	private String textOfThePermission;
	
	@Column(name = "affirmation_text")
	private String affirmationText;
	
	@Column(name = "denial_text")
	private String denialText;
	
	@Column(name = "allow_without_permission")
	private Integer allowWithoutPermission;
	
	@Column(name = "html_consent")
	private String htmlConsent;
	
	@Column(name = "e_consent_firstname")
	private String eConsentFirstName;
	
	@Column(name = "e_consent_lastname")
	private String eConsentLastName;
	
	@Column(name = "e_consent_agree")
	private Integer e_consent_agree;
	
	@Column(name = "e_consent_signature")
	private String eConsentSignature;
	
	@Column(name = "created_on")
	private String createdOn;
	
	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "modified_by")
	private Integer modifiedBy;

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

	public Integer getComprehensionTestMinimumScore() {
		return comprehensionTestMinimumScore;
	}

	public void setComprehensionTestMinimumScore(Integer comprehensionTestMinimumScore) {
		this.comprehensionTestMinimumScore = comprehensionTestMinimumScore;
	}

	public Integer getShareDataPermissions() {
		return shareDataPermissions;
	}

	public void setShareDataPermissions(Integer shareDataPermissions) {
		this.shareDataPermissions = shareDataPermissions;
	}

	public String getTextOfThePermission() {
		return textOfThePermission;
	}

	public void setTextOfThePermission(String textOfThePermission) {
		this.textOfThePermission = textOfThePermission;
	}

	public String getAffirmationText() {
		return affirmationText;
	}

	public void setAffirmationText(String affirmationText) {
		this.affirmationText = affirmationText;
	}

	public String getDenialText() {
		return denialText;
	}

	public void setDenialText(String denialText) {
		this.denialText = denialText;
	}

	public Integer getAllowWithoutPermission() {
		return allowWithoutPermission;
	}

	public void setAllowWithoutPermission(Integer allowWithoutPermission) {
		this.allowWithoutPermission = allowWithoutPermission;
	}

	public String getHtmlConsent() {
		return htmlConsent;
	}

	public void setHtmlConsent(String htmlConsent) {
		this.htmlConsent = htmlConsent;
	}

	public String geteConsentFirstName() {
		return eConsentFirstName;
	}

	public void seteConsentFirstName(String eConsentFirstName) {
		this.eConsentFirstName = eConsentFirstName;
	}

	public String geteConsentLastName() {
		return eConsentLastName;
	}

	public void seteConsentLastName(String eConsentLastName) {
		this.eConsentLastName = eConsentLastName;
	}

	public Integer getE_consent_agree() {
		return e_consent_agree;
	}

	public void setE_consent_agree(Integer e_consent_agree) {
		this.e_consent_agree = e_consent_agree;
	}

	public String geteConsentSignature() {
		return eConsentSignature;
	}

	public void seteConsentSignature(String eConsentSignature) {
		this.eConsentSignature = eConsentSignature;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
}
