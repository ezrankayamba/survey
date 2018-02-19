package tz.co.nezatech.apps.survey.web.util;

import java.util.Date;

import tz.co.nezatech.apps.survey.model.Form;

public class FormUtil {
    public static final String FORM_REPOS_DATA = "formRepos";
    public static final String FORM_INSTANCE_DATA = "formInstance";

    public static final String formInstanceName(Form form, Date recordDate, String display) {
        return String.format("%s: %s", form.getName(), display);
        
    }
}
