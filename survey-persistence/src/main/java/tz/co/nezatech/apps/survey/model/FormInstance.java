package tz.co.nezatech.apps.survey.model;



import java.util.Date;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class FormInstance extends BaseData {
    long id;
    String name;
    Form form;
    Date recordDate;
    String json;
    String uuid;
    int status = 0;
    Date lastUpdate;
    User recordedBy;

    public FormInstance() {
    }

	public FormInstance(String name, Form form, String json, String uuid, int status, User recordedBy) {
		super();
		this.name = name;
		this.form = form;
		this.json = json;
		this.uuid = uuid;
		this.status = status;
		this.recordedBy = recordedBy;
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

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public User getRecordedBy() {
		return recordedBy;
	}

	public void setRecordedBy(User recordedBy) {
		this.recordedBy = recordedBy;
	}
    
}
