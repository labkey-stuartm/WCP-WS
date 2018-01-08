package com.studymetadata.bean;

/**
 * Extends {@link SuccessResponse} class to provide details about user in
 * response.
 * 
 * @author BTC
 * @since Jan 4, 2018 3:03:53 PM
 *
 */
public class AccessResponse extends SuccessResponse {

	private int userId = 0;
	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private String userName = "";
	private String accessCode = "";
	private String email = "";
	private String phone = "";
	private String authKey = "0";

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

}
