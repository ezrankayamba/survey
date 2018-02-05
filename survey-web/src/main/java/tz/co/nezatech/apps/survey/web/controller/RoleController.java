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

import tz.co.nezatech.apps.survey.model.Permission;
import tz.co.nezatech.apps.survey.model.Role;
import tz.co.nezatech.apps.survey.repository.PermissionRepository;
import tz.co.nezatech.apps.survey.repository.RoleRepository;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasAnyRole('Root','Admin')")
public class RoleController {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PermissionRepository permissionRepository;

	@GetMapping()
	public String index(Model m) {
		m.addAttribute("rolesMenu", true);
		m.addAttribute("roles", roleRepository.getAll());
		return "roles/index";
	}

	@PostMapping()
	public String search(Model m, String search) {
		m.addAttribute("search", search);
		System.out.println("Search: " + search);
		if (search == null)
			search = "";
		List<Role> roles = roleRepository.search(search);
		m.addAttribute("rolesMenu", true);
		m.addAttribute("roles", roles);
		return "roles/index";
	}

	@GetMapping("/edit/{id}")
	public String edit(Model m, @PathVariable Long id) {
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		return "roles/edit";
	}

	@GetMapping("/delete/{id}")
	public String delete(Model m, @PathVariable Long id) {
		roleRepository.delete(id);
		return "redirect:/roles";
	}

	@GetMapping("/create")
	public String create(Model m) {
		Role e = new Role();
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", e);
		return "roles/edit";
	}

	@PostMapping("/save")
	public String save(Role e, Model m, RedirectAttributes redirect) {
		Status s = null;
		Long id = e.getId();
		if (e.getId() != null && e.getId() > 0) {
			s = roleRepository.update(e);
		} else {
			s = roleRepository.create(e);
		}
		// redirect.addAttribute("status", s);

		if (s.isSuccess())
			redirect.addFlashAttribute("success", true).addFlashAttribute("successMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (!s.isSuccess())
			redirect.addFlashAttribute("error", true).addFlashAttribute("errorMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (s.isSuccess())
			return "redirect:/roles";
		else
			return id == null ? "redirect:/roles/create" : "redirect:/roles/edit/" + id;
	}

	@GetMapping("/matrix/{id}")
	public String indexMatrix(Model m, @PathVariable Long id) {
		permissionRepository.setOrderBy(" order by p.name asc");
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		m.addAttribute("permissions", permissionRepository.matrix(id));
		return "roles/matrix";
	}

	@PostMapping("/matrix/{id}")
	public String searchMatrix(Model m, String search, @PathVariable Long id) {
		m.addAttribute("search", search);
		permissionRepository.setOrderBy(" order by p.name asc");
		System.out.println("Search: " + search);
		if (search == null)
			search = "";
		List<Permission> permissions = permissionRepository.matrixSearch(id, search);
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		m.addAttribute("permissions", permissions);
		return "roles/matrix";
	}

	@PostMapping("/matrix/save")
	public String saveMatrix(Role e, Model m, RedirectAttributes redirect) {
		roleRepository.manageMatrix(e);
		return "redirect:/roles";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

}
