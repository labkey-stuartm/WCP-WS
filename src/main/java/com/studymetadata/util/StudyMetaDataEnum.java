/**
 * 
 */
package com.studymetadata.util;

/**
 * Provides enumerations used across the application
 * 
 * @author BTC
 * @since Jan 8, 2018 12:46:37 PM
 *
 */
public enum StudyMetaDataEnum {

	// Request @param keys
	RP_AUTHORIZATION("Authorization"), RP_STUDY_IDENTIFIER("studyId"), RP_CONSENT_VERSION(
			"consentVersion"), RP_ACTIVITY_IDENTIFIER("activityId"), RP_ACTIVITY_VERSION(
			"activityVersion"), RP_SKIP("skip"), RP_SUBJECT("subject"), RP_BODY(
			"body"), RP_FIRST_NAME("firstName"), RP_EMAIL("email"), RP_APP_VERSION(
			"appVersion"), RP_STUDY_VERSION("studyVersion"), RP_FORCE_UPDATE(
			"forceUpdate"), RP_OS_TYPE("osType"), RP_BUNDLE_IDENTIFIER(
			"bundleId"), RP_MESSAGE("message"), RP_QUERY("dbQuery"),

	// Query @param keys
	QF_BUNDLE_ID("bundleId"), QF_OS_TYPE("osType"), QF_CUSTOM_STUDY_ID(
			"customStudyId"), QF_STUDY_VERSION("studyVersion"), QF_VERSION(
			"version"), QF_LIVE("live"), QF_ACTIVE("active"), QF_SHORT_TITLE(
			"shortTitle"), QF_ACTIVE_TASK_ID("activeTaskId"), QF_QUESTIONNAIRE_ID(
			"questionnairesId"), QF_RESPONSE_TYPE_ID("responseTypeId"), QF_TYPE(
			"type"), QF_STATUS("status"), QF_STUDY_ID("studyId");

	private final String value;

	/**
	 * @param value
	 */
	private StudyMetaDataEnum(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	/**
	 * @author BTC
	 * @param value
	 * @return {@link StudyMetaDataEnum}
	 */
	public static StudyMetaDataEnum fromValue(String value) {
		for (StudyMetaDataEnum smde : StudyMetaDataEnum.values()) {
			if (smde.value.equals(value)) {
				return smde;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + value
				+ "]");
	}
}
