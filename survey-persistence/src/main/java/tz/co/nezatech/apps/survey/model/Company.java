package tz.co.nezatech.apps.survey.model;

import java.util.List;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class Company extends BaseData {
	private long id;
	private String name, msisdn, pin;
	private List<CompanyUser> users;
	

	public Company() {
		super();
	}

	public Company(String name, String msisdn, String pin) {
		super();
		this.name = name;
		this.msisdn = msisdn;
		this.pin = pin;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public List<CompanyUser> getUsers() {
		return users;
	}

	public void setUsers(List<CompanyUser> users) {
		this.users = users;
	}
}
