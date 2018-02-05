package tz.co.nezatech.apps.survey.model;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class Payment extends BaseData{
	private long id;
	private double amount;
	private String msisdn, message, transId;
	private Batch batch;
	
	public Payment() {
		super();
	}
	public Payment(double amount, String msisdn, String message, String transId) {
		super();
		this.amount = amount;
		this.msisdn = msisdn;
		this.message = message;
		this.transId = transId;
	}
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
}
