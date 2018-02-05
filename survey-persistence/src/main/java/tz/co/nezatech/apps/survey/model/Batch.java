package tz.co.nezatech.apps.survey.model;

import java.util.List;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class Batch extends BaseData {
	private long id, records;
	private String method, name;
	private double amount;
	private int status, level;
	private Company company;
	private List<Payment> payments;

	public Batch() {
		super();
	}

	public Batch(long records, String method, String name, double amount, int status, int level) {
		super();
		this.records = records;
		this.method = method;
		this.name = name;
		this.amount = amount;
		this.status = status;
		this.level = level;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public long getRecords() {
		return records;
	}

	public void setRecords(long records) {
		this.records = records;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
}
