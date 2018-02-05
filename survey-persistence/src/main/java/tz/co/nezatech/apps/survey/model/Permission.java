package tz.co.nezatech.apps.survey.model;

public class Permission extends NamedData{
	private Permission parent;
	private boolean enabled;

	public Permission() {
		super();
	}

	public Permission(String name, String description) {
		super(name, description);
	}

	public Permission getParent() {
		return parent;
	}

	public void setParent(Permission parent) {
		this.parent = parent;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}