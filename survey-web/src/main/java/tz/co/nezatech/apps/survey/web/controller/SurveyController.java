package tz.co.nezatech.apps.survey.web.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tz.co.nezatech.apps.survey.model.Form;
import tz.co.nezatech.apps.survey.repository.FormRepository;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@RestController
@RequestMapping("api")
public class SurveyController {
	@Autowired
	FormRepository formRepository;

	@GetMapping("/forms")
	public List<Form> forms(Principal p) {
		return formRepository.getAll();
	}

	@GetMapping("/forms/{id}")
	@ResponseBody
	public String form(Principal p, @PathVariable Long id) {
		Form f = formRepository.findById(id);
		return f.getJson();
	}

	@PostMapping("/forms")
	public Status formSubmit(Principal p, HttpServletRequest req) {
		return null;
	}
}
