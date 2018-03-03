package tz.co.nezatech.apps.survey.web.controller.api;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import tz.co.nezatech.apps.survey.model.Form;
import tz.co.nezatech.apps.survey.model.FormInstance;
import tz.co.nezatech.apps.survey.model.Setup;
import tz.co.nezatech.apps.survey.model.User;
import tz.co.nezatech.apps.survey.repository.FormInstanceRepository;
import tz.co.nezatech.apps.survey.repository.FormRepository;
import tz.co.nezatech.apps.survey.repository.SetupRepository;
import tz.co.nezatech.apps.survey.repository.UserRepository;
import tz.co.nezatech.apps.survey.web.util.DummyFormInstance;
import tz.co.nezatech.apps.survey.web.util.Instance;
import tz.co.nezatech.apps.survey.web.util.SetupQuery;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@RestController
@RequestMapping("api")
@PreAuthorize("hasAnyAuthority('manageSurveys')")
public class SurveyController {
	@Autowired
	FormRepository formRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FormInstanceRepository fiRepository;
	@Autowired
	SetupRepository setupRepository;

	Logger logger = LoggerFactory.getLogger(SurveyController.class.getName());

	@GetMapping("/forms")
	@PreAuthorize("hasAnyAuthority('viewSurveyForms')")
	public List<Form> forms(Principal p) {
		return formRepository.getAll();
	}

	@GetMapping("/forms/{id}")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('viewSurveyForms')")
	public String form(Principal p, @PathVariable Long id) {
		Form f = formRepository.findById(id);
		return f.getJson();
	}

	@PostMapping("/forms")
	@PreAuthorize("hasAnyAuthority('submitSurveyForms')")
	public Status formSubmit(@RequestBody DummyFormInstance fi, Principal p, HttpServletRequest req) {
		User user = userRepository.getAll("username", p.getName()).get(0);
		Status s = null;
		try {
			Gson g = new Gson();
			logger.error("Json: " + fi.getJson());

			Instance i = g.fromJson(fi.getJson(), Instance.class);

			Form form = formRepository.findById(i.getFormId());
			List<FormInstance> tmp = fiRepository.getAll("uuid", fi.getUuid().toString());
			FormInstance e = tmp.isEmpty() ? null : tmp.get(0);

			boolean newRecord = false;
			if (e == null) {// new record
				e = new FormInstance();
				e.setRecordDate(new Date());
				newRecord = true;
			}
			e.setForm(form);
			e.setJson(fi.getJson());
			e.setLastUpdate(new Date());
			e.setName(fi.getName());
			e.setRecordedBy(user);
			e.setStatus(1);
			e.setUuid(fi.getUuid().toString());
			if (newRecord) {
				s = fiRepository.create(e);
			} else {
				s = fiRepository.update(e);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: " + ex.getMessage());

		}
		return s;
	}

	@PostMapping("/setups")
	@PreAuthorize("hasAnyAuthority('querySetups')")
	public List<Setup> setups(@RequestBody SetupQuery q) {
		setupRepository.setOrderBy("order by s.last_update asc");
		logger.debug(String.format("Type: %s, LastUpdate: %s", q.getType(), q.getLastUpdate()));
		String value=q.getType().toLowerCase().equals("all")?q.getLastUpdate():q.getType() + "/" + q.getLastUpdate();
		return setupRepository.search(value);
	}
}
