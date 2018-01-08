package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides questionnaire {@link QuestionnairesDto} frequency details.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:30:00 PM
 *
 */
@Entity
@Table(name = "questionnaires_frequencies")
public class QuestionnairesFrequenciesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5584696841111331744L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "questionnaires_id")
	private Integer questionnairesId;

	@Column(name = "frequency_date")
	private String frequencyDate;

	@Column(name = "frequency_time")
	private String frequencyTime;

	@Column(name = "is_launch_study")
	private Boolean isLaunchStudy = false;

	@Column(name = "is_study_life_time")
	private Boolean isStudyLifeTime = false;

	@Column(name = "study_version")
	private Integer studyVersion = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuestionnairesId() {
		return questionnairesId;
	}

	public void setQuestionnairesId(Integer questionnairesId) {
		this.questionnairesId = questionnairesId;
	}

	public String getFrequencyDate() {
		return frequencyDate;
	}

	public void setFrequencyDate(String frequencyDate) {
		this.frequencyDate = frequencyDate;
	}

	public String getFrequencyTime() {
		return frequencyTime;
	}

	public void setFrequencyTime(String frequencyTime) {
		this.frequencyTime = frequencyTime;
	}

	public Boolean getIsLaunchStudy() {
		return isLaunchStudy;
	}

	public void setIsLaunchStudy(Boolean isLaunchStudy) {
		this.isLaunchStudy = isLaunchStudy;
	}

	public Boolean getIsStudyLifeTime() {
		return isStudyLifeTime;
	}

	public void setIsStudyLifeTime(Boolean isStudyLifeTime) {
		this.isStudyLifeTime = isStudyLifeTime;
	}

	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}

}
