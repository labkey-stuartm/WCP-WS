package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Mohan
 *
 */
@Entity
@Table(name="questionnaires_frequencies")
public class QuestionnairesFrequenciesDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="questionnaires_id")
	private Integer questionnairesId;
	
	@Column(name="frequency_date")
	private String frequencyDate;
	
	@Column(name="frequency_time")
	private String frequencyTime;

	@Column(name="is_launch_study")
	private Integer isLaunchStudy;
	
	@Column(name="is_study_life_time")
	private Integer isStudyLifeTime;
	
	@Column(name="repeat_questionnaire")
	private Integer repeatQuestionnaire;
	
	@Column(name="hours_intervals")
	private Integer hoursIntervals;
	
	@Column(name="day_of_the_week")
	private String dayOfTheWeek;
	
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

	public Integer getIsLaunchStudy() {
		return isLaunchStudy;
	}

	public void setIsLaunchStudy(Integer isLaunchStudy) {
		this.isLaunchStudy = isLaunchStudy;
	}

	public Integer getIsStudyLifeTime() {
		return isStudyLifeTime;
	}

	public void setIsStudyLifeTime(Integer isStudyLifeTime) {
		this.isStudyLifeTime = isStudyLifeTime;
	}

	public Integer getRepeatQuestionnaire() {
		return repeatQuestionnaire;
	}

	public void setRepeatQuestionnaire(Integer repeatQuestionnaire) {
		this.repeatQuestionnaire = repeatQuestionnaire;
	}

	public Integer getHoursIntervals() {
		return hoursIntervals;
	}

	public void setHoursIntervals(Integer hoursIntervals) {
		this.hoursIntervals = hoursIntervals;
	}

	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}
	
}
