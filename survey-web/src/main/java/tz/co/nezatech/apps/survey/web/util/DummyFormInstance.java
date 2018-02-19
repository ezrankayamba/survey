package tz.co.nezatech.apps.survey.web.util;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class DummyFormInstance implements Serializable {
	int id;
    String name;
    String json;
    UUID uuid;
    int status = 0;

    public DummyFormInstance() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
