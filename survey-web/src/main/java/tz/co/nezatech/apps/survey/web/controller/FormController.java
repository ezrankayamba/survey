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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tz.co.nezatech.apps.survey.model.Form;
import tz.co.nezatech.apps.survey.repository.FormRepository;
import tz.co.nezatech.apps.survey.web.util.FlashData;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Controller
@RequestMapping("/forms")
@PreAuthorize("hasAnyAuthority('manageFormRepos')")
public class FormController {
	@Autowired
	FormRepository formRepository;
	

	@GetMapping()
	@PreAuthorize("hasAnyAuthority('viewFormRepos')")
	public String index(Model m) {
		formRepository.setOrderBy(" order by f.name asc");
		List<Form> forms = formRepository.getAll();
		m.addAttribute("formsMenu", true);
		m.addAttribute("forms", forms);

		return "forms/index";
	}

	@PostMapping()
	@PreAuthorize("hasAnyAuthority('viewFormRepos')")
	public String search(Model m, String search) {
		formRepository.setOrderBy(" order by f.name asc");
		m.addAttribute("search", search);
		System.out.println("Search: " + search);
		if (search == null)
			search = "";
		List<Form> forms = formRepository.search(search);
		m.addAttribute("formsMenu", true);
		m.addAttribute("forms", forms);
		return "forms/index";
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasAnyAuthority('editFormRepos')")
	public String edit(Model m, @PathVariable Long id) {
		List<Form> forms = formRepository.getAll();
		m.addAttribute("formsMenu", true);
		m.addAttribute("forms", forms);
		m.addAttribute("form", formRepository.findById(id));
		return "forms/edit";
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('deleteFormRepos')")
	public String delete(Model m, @PathVariable Integer id) {
		formRepository.delete(id);
		return "redirect:/forms";
	}

	@GetMapping("/create")
	@PreAuthorize("hasAnyAuthority('createFormRepos')")
	public String create(Model m) {
		List<Form> forms = formRepository.getAll();
		Form f = new Form();
		m.addAttribute("formsMenu", true);
		m.addAttribute("forms", forms);
		m.addAttribute("form", f);
		return "forms/edit";
	}

	
	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('createFormRepos')")
	public String save(Form p, Model m, RedirectAttributes redirect) {
		Status s = null;
		if (p.getId() != null && p.getId() > 0) {
			s = formRepository.update(p);
		} else {
			s = formRepository.create(p);
		}
		FlashData fd = new FlashData(s.getCode(), s.getMessage());
		if (s.getCode() == 200) {
			fd.setStyleClass("success");
		} else {
			fd.setStyleClass("alert");
		}
		redirect.addFlashAttribute("flashData", fd);

		return "redirect:/forms";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
