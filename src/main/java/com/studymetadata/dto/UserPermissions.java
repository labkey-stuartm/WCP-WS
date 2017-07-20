package com.studymetadata.dto;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user_permissions")
public class UserPermissions implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer userRoleId;
	
	private String permissions;

	
	private Set<UserDto> users;
	
	public UserPermissions() {
	
	}

	public UserPermissions(Set<UserDto> users, String permissions) {
		this.setUsers(users);
		this.setPermissions(permissions);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permission_id", unique = true, nullable = false)
	public Integer getUserRoleId() {
		return this.userRoleId;
	}

	public void setUserRoleId(Integer userRoleId) {
		this.userRoleId = userRoleId;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	public Set<UserDto> getUsers() {
		return users;
	}

	public void setUsers(Set<UserDto> users) {
		this.users = users;
	}
	
	@Column(name = "permissions", nullable = false, length = 45)
	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

}
