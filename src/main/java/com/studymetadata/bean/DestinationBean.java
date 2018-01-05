package com.studymetadata.bean;

/**
 * 
 * @author BTC
 * @createdOn Jan 4, 2018 3:10:40 PM
 *
 */
public class DestinationBean {

	private String condition = "";
	private String operator = "";
	private String destination = "";

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "DestinationBean [condition=" + condition + ", operator="
				+ operator + ", destination=" + destination + "]";
	}

}
