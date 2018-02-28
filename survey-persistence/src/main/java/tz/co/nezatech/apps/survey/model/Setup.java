package tz.co.nezatech.apps.survey.model;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class Setup extends BaseData{
	String uuid;
	String name;
	String type;
	String lastUpdate;
	
	public Setup() {
		super();
	}
	public Setup(String uuid, String name, String type, String lastUpdate) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.lastUpdate=lastUpdate;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
