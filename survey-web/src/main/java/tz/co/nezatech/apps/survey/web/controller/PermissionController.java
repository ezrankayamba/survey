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
import tz.co.nezatech.apps.survey.repository.PermissionRepository;
import tz.co.nezatech.apps.survey.repository.RoleRepository;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Controller
@RequestMapping("/permissions")
@PreAuthorize("hasRole('Root')")
public class PermissionController {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PermissionRepository permissionRepository;

	@GetMapping()
	public String index(Model m) {
		permissionRepository.setOrderBy(" order by p.name asc");
		List<Permission> permissions = permissionRepository.getAll();
		m.addAttribute("permissionsMenu", true);
		m.addAttribute("permissions", permissions);

		return "permissions/index";
	}

	@PostMapping()
	public String search(Model m, String search) {
		permissionRepository.setOrderBy(" order by p.name asc");
		m.addAttribute("search", search);
		System.out.println("Search: " + search);
		if (search == null)
			search = "";
		List<Permission> permissions = permissionRepository.search(search);
		m.addAttribute("permissionsMenu", true);
		m.addAttribute("permissions", permissions);
		return "permissions/index";
	}

	@GetMapping("/edit/{id}")
	public String edit(Model m, @PathVariable Integer id) {
		List<Permission> parents = permissionRepository.getAll();
		Permission p = permissionRepository.findById(id);
		parents.remove(p);
		m.addAttribute("permissionsMenu", true);
		m.addAttribute("parents", parents);
		m.addAttribute("permission", p);
		return "permissions/edit";
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('deletePermission')")
	public String delete(Model m, @PathVariable Integer id) {
		permissionRepository.delete(id);
		return "redirect:/permissions";
	}

	@GetMapping("/create")
	public String create(Model m) {
		List<Permission> parents = permissionRepository.getAll();
		Permission p = new Permission();
		m.addAttribute("permissionsMenu", true);
		m.addAttribute("parents", parents);
		m.addAttribute("permission", p);
		return "permissions/edit";
	}

	@PostMapping("/save")
	public String save(Permission p, Model m, RedirectAttributes redirect) {
		Status s = null;
		Long id = p.getId();
		if (p.getId() != null && p.getId() > 0) {
			s = permissionRepository.update(p);
		} else {
			s = permissionRepository.create(p);
		}
		// redirect.addAttribute("status", s);

		if (s.isSuccess())
			redirect.addFlashAttribute("success", true).addFlashAttribute("successMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (!s.isSuccess())
			redirect.addFlashAttribute("error", true).addFlashAttribute("errorMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (s.isSuccess())
			return "redirect:/permissions";
		else
			return id == null ? "redirect:/permissions/create" : "redirect:/permissions/edit/" + id;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
