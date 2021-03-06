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
import tz.co.nezatech.apps.survey.web.util.FlashData;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasAnyAuthority('manageRoles')")
public class RoleController {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PermissionRepository permissionRepository;

	@GetMapping()
	@PreAuthorize("hasAnyAuthority('viewRoles')")
	public String index(Model m) {
		m.addAttribute("rolesMenu", true);
		m.addAttribute("roles", roleRepository.getAll());
		return "roles/index";
	}

	@PostMapping()
	@PreAuthorize("hasAnyAuthority('viewRoles')")
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
	@PreAuthorize("hasAnyAuthority('editRoles')")
	public String edit(Model m, @PathVariable Long id) {
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		return "roles/edit";
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('editRoles')")
	public String delete(Model m, @PathVariable Long id) {
		roleRepository.delete(id);
		return "redirect:/roles";
	}

	@GetMapping("/create")
	@PreAuthorize("hasAnyAuthority('createRoles')")
	public String create(Model m) {
		Role e = new Role();
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", e);
		return "roles/edit";
	}

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('createRoles')")
	public String save(Role e, Model m, RedirectAttributes redirect) {
		Status s = null;
		if (e.getId() != null && e.getId() > 0) {
			s = roleRepository.update(e);
		} else {
			s = roleRepository.create(e);
		}
		FlashData fd = new FlashData(s.getCode(), s.getMessage());
		if (s.getCode() == 200) {
			fd.setStyleClass("success");
		} else {
			fd.setStyleClass("alert");
		}
		redirect.addFlashAttribute("flashData", fd);
		return "redirect:/roles";
	}

	@GetMapping("/matrix/{id}")
	@PreAuthorize("hasAnyAuthority('viewRoleMatrix')")
	public String indexMatrix(Model m, @PathVariable Long id) {
		permissionRepository.setOrderBy(" order by prtchld asc");
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		m.addAttribute("permissions", permissionRepository.matrix(id));
		return "roles/matrix";
	}

	@PostMapping("/matrix/{id}")
	@PreAuthorize("hasAnyAuthority('viewRoleMatrix')")
	public String searchMatrix(Model m, String search, @PathVariable Long id) {
		m.addAttribute("search", search);
		permissionRepository.setOrderBy(" order by p.name asc");
		if (search == null)
			search = "";
		List<Permission> permissions = permissionRepository.matrixSearch(id, search);
		m.addAttribute("rolesMenu", true);
		m.addAttribute("role", roleRepository.findById(id));
		m.addAttribute("permissions", permissions);
		return "roles/matrix";
	}

	@PostMapping("/matrix/save")
	@PreAuthorize("hasAnyAuthority('createRoleMatrix')")
	public String saveMatrix(Role e, Model m, RedirectAttributes redirect) {
		Status s=roleRepository.manageMatrix(e);
		FlashData fd = new FlashData(s.getCode(), s.getMessage());
		if (s.getCode() == 200) {
			fd.setStyleClass("success");
		} else {
			fd.setStyleClass("alert");
		}
		redirect.addFlashAttribute("flashData", fd);
		return "redirect:/roles";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

}
