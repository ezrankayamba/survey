package tz.co.nezatech.apps.survey.model;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class Audit extends BaseData {
	private long id;
	private String task;
	private Role role;
	private User user;
	

	public Audit() {
		super();
	}

	public Audit(String task) {
		super();
		this.task = task;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
