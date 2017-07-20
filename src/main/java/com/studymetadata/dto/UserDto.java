package com.studymetadata.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;


@Entity
@Table(name = "users")
public class UserDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String userEmail;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name="role_id")
	private Integer roleId;

	@Column(name = "password")
	private String userPassword;
	
	@Column(name = "status", length = 1)
	private boolean enabled;
	
	@Column(name = "created_date")
	private String createdOn;
	
	@Column(name = "modified_date")
	private String modifiedOn;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name = "accountNonExpired", length = 1)
	private boolean accountNonExpired;
	
	@Column(name = "credentialsNonExpired", length = 1)
	private boolean credentialsNonExpired;
	
	@Column(name = "accountNonLocked", length = 1)
	private boolean accountNonLocked;

	@Column(name = "access_code")
	private String accessCode;
	
	@Column(name = "security_token")
	private String securityToken;
	
	@Column(name = "token_used")
	private Boolean tokenUsed;
	
	@Column(name = "token_expiry_date")
	private String tokenExpiryDate;
	
	@Column(name = "password_expairded_datetime")
	private String passwordExpairdedDateTime;
	
	@Transient
	private String roleName;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	 @JoinTable(name = "user_permission_mapping", joinColumns = {
	   @JoinColumn(name = "user_id", nullable = false) },
	   inverseJoinColumns = { @JoinColumn(name = "permission_id", nullable = false) })
	private Set<UserPermissions> permissionList = new HashSet<UserPermissions>(0);
	
	@Column(name = "force_logout")
	@Type(type="yes_no")
	private boolean forceLogout = false;
	
	@Transient
	private String userFullName;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the accountNonExpired
	 */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * @param accountNonExpired the accountNonExpired to set
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * @return the credentialsNonExpired
	 */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * @param credentialsNonExpired the credentialsNonExpired to set
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * @return the accountNonLocked
	 */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * @param accountNonLocked the accountNonLocked to set
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * @return the permissions
	 */
	public Set<UserPermissions> getPermissions() {
		return permissionList;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<UserPermissions> permissionList) {
		this.permissionList = permissionList;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the accessCode
	 */
	public String getAccessCode() {
		return accessCode;
	}

	/**
	 * @param accessCode the accessCode to set
	 */
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	/**
	 * @return the securityToken
	 */
	public String getSecurityToken() {
		return securityToken;
	}

	/**
	 * @param securityToken the securityToken to set
	 */
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	/**
	 * @return the tokenExpiryDate
	 */
	public String getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	/**
	 * @param tokenExpiryDate the tokenExpiryDate to set
	 */
	public void setTokenExpiryDate(String tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}

	/**
	 * @return the permissionList
	 */
	public Set<UserPermissions> getPermissionList() {
		return permissionList;
	}

	/**
	 * @param permissionList the permissionList to set
	 */
	public void setPermissionList(Set<UserPermissions> permissionList) {
		this.permissionList = permissionList;
	}

	/**
	 * @return the tokenUsed
	 */
	public Boolean getTokenUsed() {
		return tokenUsed;
	}

	/**
	 * @param tokenUsed the tokenUsed to set
	 */
	public void setTokenUsed(Boolean tokenUsed) {
		this.tokenUsed = tokenUsed;
	}

	public String getPasswordExpairdedDateTime() {
		return passwordExpairdedDateTime;
	}

	public void setPasswordExpairdedDateTime(String passwordExpairdedDateTime) {
		this.passwordExpairdedDateTime = passwordExpairdedDateTime;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isForceLogout() {
		return forceLogout;
	}

	public void setForceLogout(boolean forceLogout) {
		this.forceLogout = forceLogout;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
}

