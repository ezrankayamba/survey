package tz.co.nezatech.apps.survey.web.util;

public class SetupQuery {
	String type, lastUpdate;

	public SetupQuery() {
		super();
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

	public SetupQuery(String type, String lastUpdate) {
		super();
		this.type = type;
		this.lastUpdate = lastUpdate;
	}

}
