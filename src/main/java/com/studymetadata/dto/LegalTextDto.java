package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides application privacy and policy, terms of use information.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:29:02 PM
 *
 */
@Entity
@Table(name = "legal_text")
public class LegalTextDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6256475646468023254L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "mobile_app_terms")
	private String mobileAppTerms;

	@Column(name = "mobile_app_terms_modified_datetime")
	private String mobileAppTermsModifiedDatetime;

	@Column(name = "mobile_app_privacy_policy")
	private String mobileAppPrivacyPolicy;

	@Column(name = "mobile_app_privacy_policy_modified_datetime")
	private String mobileAppPrivacyPolicyModifiedDatetime;

	@Column(name = "web_app_terms")
	private String webAppTerms;

	@Column(name = "web_app_terms_modified_datetime")
	private String webAppTermsModifiedDatetime;

	@Column(name = "web_app_privacy_policy")
	private String webAppPrivacyPolicy;

	@Column(name = "web_app_privacy_policy_modified_datetime")
	private String webAppPrivacyPolicyModifiedDatetime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMobileAppTerms() {
		return mobileAppTerms;
	}

	public void setMobileAppTerms(String mobileAppTerms) {
		this.mobileAppTerms = mobileAppTerms;
	}

	public String getMobileAppTermsModifiedDatetime() {
		return mobileAppTermsModifiedDatetime;
	}

	public void setMobileAppTermsModifiedDatetime(
			String mobileAppTermsModifiedDatetime) {
		this.mobileAppTermsModifiedDatetime = mobileAppTermsModifiedDatetime;
	}

	public String getMobileAppPrivacyPolicy() {
		return mobileAppPrivacyPolicy;
	}

	public void setMobileAppPrivacyPolicy(String mobileAppPrivacyPolicy) {
		this.mobileAppPrivacyPolicy = mobileAppPrivacyPolicy;
	}

	public String getMobileAppPrivacyPolicyModifiedDatetime() {
		return mobileAppPrivacyPolicyModifiedDatetime;
	}

	public void setMobileAppPrivacyPolicyModifiedDatetime(
			String mobileAppPrivacyPolicyModifiedDatetime) {
		this.mobileAppPrivacyPolicyModifiedDatetime = mobileAppPrivacyPolicyModifiedDatetime;
	}

	public String getWebAppTerms() {
		return webAppTerms;
	}

	public void setWebAppTerms(String webAppTerms) {
		this.webAppTerms = webAppTerms;
	}

	public String getWebAppTermsModifiedDatetime() {
		return webAppTermsModifiedDatetime;
	}

	public void setWebAppTermsModifiedDatetime(
			String webAppTermsModifiedDatetime) {
		this.webAppTermsModifiedDatetime = webAppTermsModifiedDatetime;
	}

	public String getWebAppPrivacyPolicy() {
		return webAppPrivacyPolicy;
	}

	public void setWebAppPrivacyPolicy(String webAppPrivacyPolicy) {
		this.webAppPrivacyPolicy = webAppPrivacyPolicy;
	}

	public String getWebAppPrivacyPolicyModifiedDatetime() {
		return webAppPrivacyPolicyModifiedDatetime;
	}

	public void setWebAppPrivacyPolicyModifiedDatetime(
			String webAppPrivacyPolicyModifiedDatetime) {
		this.webAppPrivacyPolicyModifiedDatetime = webAppPrivacyPolicyModifiedDatetime;
	}

}
