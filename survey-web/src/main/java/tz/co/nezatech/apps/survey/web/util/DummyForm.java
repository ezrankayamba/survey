package tz.co.nezatech.apps.survey.web.util;

import java.io.Serializable;

public class DummyForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
    int formId;
    String name;
    String description;
    String json;
    String display;

    public DummyForm() {
    }

    public DummyForm(int formId, String name, String description, String json, String display) {
        this.formId = formId;
        this.name = name;
        this.description = description;
        this.json = json;
        this.display = display;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
