/**
 * 
 */
package com.studymetadata.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Mohan
 *
 */
@Entity
@Table(name="question_condtion_branching")
@NamedQueries({
	@NamedQuery(name="getQuestionConditionBranchList", query="from QuestionConditionBranchDto QCBDTO where QCBDTO.questionId =:questionId ORDER BY QCBDTO.sequenceNo"),
})
public class QuestionConditionBranchDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2517716372949869931L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="condition_id")
	private Integer conditionId;
	
	@Column(name="question_id")
	private Integer questionId;
	
	@Column(name="input_type")
	private String inputType;
	
	@Column(name="input_type_value")
	private String inputTypeValue;
	
	@Column(name="sequence_no")
	private Integer sequenceNo;
	
	@Column(name="parent_sequence_no")
	private Integer parentSequenceNo;
	
	@Column(name="active")
	private Boolean active;

	public Integer getConditionId() {
		return conditionId;
	}

	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getInputTypeValue() {
		return inputTypeValue;
	}

	public void setInputTypeValue(String inputTypeValue) {
		this.inputTypeValue = inputTypeValue;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Integer getParentSequenceNo() {
		return parentSequenceNo;
	}

	public void setParentSequenceNo(Integer parentSequenceNo) {
		this.parentSequenceNo = parentSequenceNo;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
