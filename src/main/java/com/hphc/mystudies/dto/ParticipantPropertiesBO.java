package com.hphc.mystudies.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "participant_properties")
public class ParticipantPropertiesBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "short_title")
	private String shortTitle;

	@Column(name = "property_name")
	private String propertyName;

	@Column(name = "property_type")
	private String propertyType;

	@Column(name = "data_type")
	private String dataType;

	@Column(name = "use_as_anchor_date")
	private boolean useAsAnchorDate;

	@Column(name = "data_source")
	private String dataSource;

	@Column(name = "populated_at_enrollment")
	private boolean populatedAtEnrollment;

	@Column(name = "refreshed_value")
	private boolean refreshedValue;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date")
	private String createdDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date")
	private String modifiedDate;

	@Column(name = "custom_study_id")
	private String customStudyId;

	@Column(name = "study_id")
	private Integer studyId;

	@Column(name = "status")
	private boolean status;

	@Column(name = "active")
	private boolean active;

	@Column(name = "anchor_date_id")
	private Integer anchorDateId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isUseAsAnchorDate() {
		return useAsAnchorDate;
	}

	public void setUseAsAnchorDate(boolean useAsAnchorDate) {
		this.useAsAnchorDate = useAsAnchorDate;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public boolean isPopulatedAtEnrollment() {
		return populatedAtEnrollment;
	}

	public void setPopulatedAtEnrollment(boolean populatedAtEnrollment) {
		this.populatedAtEnrollment = populatedAtEnrollment;
	}

	public boolean isRefreshedValue() {
		return refreshedValue;
	}

	public void setRefreshedValue(boolean refreshedValue) {
		this.refreshedValue = refreshedValue;
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

	public String getCustomStudyId() {
		return customStudyId;
	}

	public void setCustomStudyId(String customStudyId) {
		this.customStudyId = customStudyId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getAnchorDateId() {
		return anchorDateId;
	}

	public void setAnchorDateId(Integer anchorDateId) {
		this.anchorDateId = anchorDateId;
	}

	@Override
	public String toString() {
		return "ParticipantPropertiesBO [id=" + id + ", shortTitle=" + shortTitle + ", propertyName=" + propertyName
				+ ", propertyType=" + propertyType + ", dataType=" + dataType + ", useAsAnchorDate=" + useAsAnchorDate
				+ ", dataSource=" + dataSource + ", populatedAtEnrollment=" + populatedAtEnrollment
				+ ", refreshedValue=" + refreshedValue + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + ", customStudyId=" + customStudyId
				+ ", studyId=" + studyId + ", status=" + status + ", active=" + active + ", anchorDateId="
				+ anchorDateId + "]";
	}

}
