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
 * Provides information about status of the sub tasks for study {@link StudyDto}.
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:31:52 PM
 *
 */
@Entity
@Table(name = "study_sequence")
@NamedQueries({

@NamedQuery(name = "getStudySequenceDetailsByStudyId", query = "from StudySequenceDto SSDTO"
		+ " where SSDTO.studyId =:studyId "), })
public class StudySequenceDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6095431690838787358L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_sequence_id")
	private Integer studySequenceId;

	@Column(name = "actions")
	private String actions;

	@Column(name = "basic_info")
	private String basicInfo;

	@Column(name = "check_list")
	private String checkList;

	@Column(name = "comprehension_test")
	private String comprehensionTest;

	@Column(name = "consent_edu_info")
	private String consentEduInfo;

	@Column(name = "e_consent")
	private String eConsent;

	@Column(name = "eligibility")
	private String eligibility;

	@Column(name = "miscellaneous_branding")
	private String miscellaneousBranding;

	@Column(name = "miscellaneous_notification")
	private String miscellaneousNotification;

	@Column(name = "miscellaneous_resources")
	private String miscellaneousResources;

	@Column(name = "over_view")
	private String overView;

	@Column(name = "setting_admins")
	private String settingAdmins;

	@Column(name = "study_dashboard_chart")
	private String studyDashboardChart;

	@Column(name = "study_dashboard_stats")
	private String studyDashboardStats;

	@Column(name = "study_exc_active_task")
	private String studyExcActiveTask;

	@Column(name = "study_exc_questionnaries")
	private String studyExcQuestionnaries;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

	public Integer getStudySequenceId() {
		return studySequenceId;
	}

	public void setStudySequenceId(Integer studySequenceId) {
		this.studySequenceId = studySequenceId;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(String basicInfo) {
		this.basicInfo = basicInfo;
	}

	public String getCheckList() {
		return checkList;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	public String getComprehensionTest() {
		return comprehensionTest;
	}

	public void setComprehensionTest(String comprehensionTest) {
		this.comprehensionTest = comprehensionTest;
	}

	public String getConsentEduInfo() {
		return consentEduInfo;
	}

	public void setConsentEduInfo(String consentEduInfo) {
		this.consentEduInfo = consentEduInfo;
	}

	public String geteConsent() {
		return eConsent;
	}

	public void seteConsent(String eConsent) {
		this.eConsent = eConsent;
	}

	public String getEligibility() {
		return eligibility;
	}

	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}

	public String getMiscellaneousBranding() {
		return miscellaneousBranding;
	}

	public void setMiscellaneousBranding(String miscellaneousBranding) {
		this.miscellaneousBranding = miscellaneousBranding;
	}

	public String getMiscellaneousNotification() {
		return miscellaneousNotification;
	}

	public void setMiscellaneousNotification(String miscellaneousNotification) {
		this.miscellaneousNotification = miscellaneousNotification;
	}

	public String getMiscellaneousResources() {
		return miscellaneousResources;
	}

	public void setMiscellaneousResources(String miscellaneousResources) {
		this.miscellaneousResources = miscellaneousResources;
	}

	public String getOverView() {
		return overView;
	}

	public void setOverView(String overView) {
		this.overView = overView;
	}

	public String getSettingAdmins() {
		return settingAdmins;
	}

	public void setSettingAdmins(String settingAdmins) {
		this.settingAdmins = settingAdmins;
	}

	public String getStudyDashboardChart() {
		return studyDashboardChart;
	}

	public void setStudyDashboardChart(String studyDashboardChart) {
		this.studyDashboardChart = studyDashboardChart;
	}

	public String getStudyDashboardStats() {
		return studyDashboardStats;
	}

	public void setStudyDashboardStats(String studyDashboardStats) {
		this.studyDashboardStats = studyDashboardStats;
	}

	public String getStudyExcActiveTask() {
		return studyExcActiveTask;
	}

	public void setStudyExcActiveTask(String studyExcActiveTask) {
		this.studyExcActiveTask = studyExcActiveTask;
	}

	public String getStudyExcQuestionnaries() {
		return studyExcQuestionnaries;
	}

	public void setStudyExcQuestionnaries(String studyExcQuestionnaries) {
		this.studyExcQuestionnaries = studyExcQuestionnaries;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

}
