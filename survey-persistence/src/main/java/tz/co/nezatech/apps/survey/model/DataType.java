package tz.co.nezatech.apps.survey.model;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class DataType extends BaseData{
	String name;
	String type;
	String lastUpdate;
	
	public DataType() {
		super();
	}
	public DataType(String name, String type, String lastUpdate) {
		super();
		this.name = name;
		this.type = type;
		this.lastUpdate=lastUpdate;
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
	@Override
	public String toString() {
		return String.format("Type: %s, Name: %s, LastUpdate: %s", getType(), getName(), getLastUpdate());
	}
}
