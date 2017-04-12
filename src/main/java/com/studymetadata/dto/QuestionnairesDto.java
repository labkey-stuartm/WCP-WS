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
@Table(name="questionnaires")
@NamedQueries({
	@NamedQuery(name="questionnairesListByStudyId", query="from QuestionnairesDto QDTO where QDTO.studyId =:studyId"),
})
public class QuestionnairesDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="study_id")
	private Integer studyId;
	
	@Column(name="frequency")
	private String frequency;
	
	@Column(name="title")
	private String title;
	
	@Column(name="study_lifetime_start")
	private String studyLifetimeStart;
	
	@Column(name="study_lifetime_end")
	private String studyLifetimeEnd;
	
	@Column(name="created_by")
	private Integer createdBy;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="modified_by")
	private Integer modifiedBy;
	
	@Column(name="modified_date")
	private String modifiedDate;
	
	@Column(name="day_of_the_week")
	private String dayOfTheWeek;
	
	@Column(name="repeat_questionnaire")
	private Integer repeatQuestionnaire;
	
	@Column(name="short_title")
	private String shortTitle;
	
	@Column(name="branching")
	private Boolean branching=false;
	
	@Column(name = "study_version")
	private Integer studyVersion=1;
	
	@Column(name = "active")
	private Boolean active;
	
	public Integer getStudyVersion() {
		return studyVersion;
	}

	public void setStudyVersion(Integer studyVersion) {
		this.studyVersion = studyVersion;
	}
	
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

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStudyLifetimeStart() {
		return studyLifetimeStart;
	}

	public void setStudyLifetimeStart(String studyLifetimeStart) {
		this.studyLifetimeStart = studyLifetimeStart;
	}

	public String getStudyLifetimeEnd() {
		return studyLifetimeEnd;
	}

	public void setStudyLifetimeEnd(String studyLifetimeEnd) {
		this.studyLifetimeEnd = studyLifetimeEnd;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public Integer getRepeatQuestionnaire() {
		return repeatQuestionnaire;
	}

	public void setRepeatQuestionnaire(Integer repeatQuestionnaire) {
		this.repeatQuestionnaire = repeatQuestionnaire;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public Boolean getBranching() {
		return branching;
	}

	public void setBranching(Boolean branching) {
		this.branching = branching;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
