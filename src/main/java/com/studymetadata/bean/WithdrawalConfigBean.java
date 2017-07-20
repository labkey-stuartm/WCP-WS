package com.studymetadata.bean;

public class WithdrawalConfigBean{
	
	private String type = ""; //delete_data/ask_user/no_action
	private String message = "";
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
