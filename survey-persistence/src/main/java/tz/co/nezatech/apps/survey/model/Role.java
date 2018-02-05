package tz.co.nezatech.apps.survey.model;

import java.util.List;

public class Role extends NamedData{
	private List<Permission> permissions;
	private List<String> permissionIds;
	private List<String> mtrxPermissionIds;

	public Role() {
		super();
	}

	public Role(String name, String description) {
		super(name, description);
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public List<String> getPermissionIds() {
		return permissionIds;
	}

	public void setPermissionIds(List<String> strPermissions) {
		this.permissionIds = strPermissions;
	}

	public List<String> getMtrxPermissionIds() {
		return mtrxPermissionIds;
	}

	public void setMtrxPermissionIds(List<String> tmpStrPermissions) {
		this.mtrxPermissionIds = tmpStrPermissions;
	}
}
