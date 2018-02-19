package tz.co.nezatech.apps.survey.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tz.co.nezatech.apps.survey.model.Form;
import tz.co.nezatech.apps.survey.model.FormInstance;
import tz.co.nezatech.apps.survey.repository.FormInstanceRepository;
import tz.co.nezatech.apps.survey.repository.FormRepository;

@Controller
@RequestMapping("/instances")
@PreAuthorize("hasAnyAuthority('manageFormInstances')")
public class FormInstanceController {
	@Autowired
	FormRepository formRepository;
	@Autowired
	FormInstanceRepository fiRepository;

	@GetMapping("/repos/{formId}")
	@PreAuthorize("hasAnyAuthority('viewFormInstances')")
	public String index(Model m, @PathVariable Long formId) {
		fiRepository.setOrderBy(" order by fi.name asc");
		List<FormInstance> instances = fiRepository.getAll("form_id", formId);
		Form form = formRepository.findById(formId);
		m.addAttribute("formsMenu", true);
		m.addAttribute("instances", instances);
		m.addAttribute("form", form);

		return "forms/instances";
	}

	@PostMapping("/repos/{formId}")
	@PreAuthorize("hasAnyAuthority('viewFormInstances')")
	public String indexSearch(Model m, @PathVariable Long formId, String search) {
		fiRepository.setOrderBy(" order by fi.name asc");
		m.addAttribute("search", search);
		if (search == null)
			search = "";
		List<FormInstance> instances = fiRepository.search(formId + ":" + search);
		Form form = formRepository.findById(formId);
		m.addAttribute("formsMenu", true);
		m.addAttribute("instances", instances);
		m.addAttribute("form", form);

		return "forms/instances";
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('deleteFormInstances')")
	public String delete(Model m, @PathVariable long id) {
		formRepository.delete(id);
		return "redirect:/forms";
	}
	@GetMapping("/deleteall/{formId}")
	@PreAuthorize("hasAnyAuthority('deleteFormInstances')")
	public String deleteAllFromRepos(Model m, @PathVariable long formId) {
		formRepository.deleteLinked(formId);
		return "redirect:/forms";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
