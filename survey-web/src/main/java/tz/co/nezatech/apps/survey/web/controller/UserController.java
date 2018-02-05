package tz.co.nezatech.apps.survey.web.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tz.co.nezatech.apps.survey.model.User;
import tz.co.nezatech.apps.survey.repository.RoleRepository;
import tz.co.nezatech.apps.survey.repository.UserRepository;
import tz.co.nezatech.apps.survey.web.MyUtil;
import tz.co.nezatech.apps.survey.web.service.EmailService;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	EmailService emailService;

	@GetMapping()
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String index(Model m) {
		m.addAttribute("usersMenu", true);
		m.addAttribute("users", userRepository.getAll());
		return "users/index";
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String search(Model m, String search) {
		m.addAttribute("search", search);
		System.out.println("Search: " + search);
		if (search == null)
			search = "";
		List<User> users = userRepository.search(search);
		m.addAttribute("usersMenu", true);
		m.addAttribute("users", users);
		return "users/index";
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String edit(Model m, @PathVariable Long id) {
		m.addAttribute("usersMenu", true);
		m.addAttribute("roles", roleRepository.getAll());
		m.addAttribute("userEntity", userRepository.findById(id));
		return "users/edit";
	}

	@GetMapping("/create")
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String create(Model m) {
		m.addAttribute("usersMenu", true);
		m.addAttribute("roles", roleRepository.getAll());
		m.addAttribute("userEntity", new User());
		return "users/edit";
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String delete(Model m, @PathVariable Long id) {
		userRepository.delete(id);
		return "redirect:/users";
	}

	@PostMapping("/save")
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String save(User e, Model m, RedirectAttributes redirect, HttpServletRequest req) {
		Status s = null;
		Long id = e.getId();
		if (e.getId() != null && e.getId() > 0) {
			s = userRepository.update(e);
		} else {
			// set password
			String pwd = MyUtil.alphaNumericRandom(6);
			e.setPassword(passwordEncoder.encode(pwd));
			s = userRepository.create(e);

			String path = "/users/pwd/" + MyUtil.encode(s.getGeneratedId() + "-" + pwd);
			String url = emailService.url(path, req);

			// encode and send mail
			String thml = "<div>Welcome to [Service name]. Kindly click below link to access your account. <br/> <br/> "
					+ "<a href=\"" + url
					+ "\" style=\"display: block; font-size:120%; text-decoration:none; width: 120px;background: #4E9CAF;text-align: center;color: white;font-weight: bold;padding: 0.5rem\" >"
					+ "Survey Tool Login</a>" + "</div>";
			String to = e.getEmail();
			String from = "Survey Tool <nezatech18@gmail.com>";
			String subject = "Complete registration";
			emailService.sendMail(from, to, subject, thml);
		}
		// redirect.addAttribute("status", s);

		if (s.isSuccess())
			redirect.addFlashAttribute("success", true).addFlashAttribute("successMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (!s.isSuccess())
			redirect.addFlashAttribute("error", true).addFlashAttribute("errorMessage", s.getMessage())
					.addFlashAttribute("statusCode", s.getCode());
		if (s.isSuccess())
			return "redirect:/users";
		else
			return id == null ? "redirect:/users/create" : "redirect:/users/edit/" + id;
	}

	@GetMapping("/pwdreset/{id}")
	@PreAuthorize("hasAnyRole('Root','Admin')")
	public String resetPwd(Model m, @PathVariable Long id, Principal p, HttpServletRequest req,
			HttpServletResponse res) {
		User e = userRepository.findById(id);
		// set password
		String pwd = MyUtil.alphaNumericRandom(6);
		e.setPassword(passwordEncoder.encode(pwd));
		userRepository.resetPwd(e);

		String url = "http://localhost:8001/bulkpay/users/pwd/" + MyUtil.encode(e.getId() + "-" + pwd);

		// encode and send mail
		String thml = "<div style=\"max-width: 100rem;border: 1px solid #333;padding: 0.5rem;\">Welcome to [Service name]. Your account is reset. Kindly click below link to access your account. <br/> <br/> "
				+ "<a href=\"" + url
				+ "\" style=\"display: block; font-size:120%; text-decoration:none; width: 248px;background: #4E9CAF;text-align: center;color: white;font-weight: bold;padding: 0.5rem\" >"
				+ "Survey Tool Login</a>" + "</div>";
		String to = e.getEmail();
		String from = "Survey Tool <nezatech18@gmail.com>";
		String subject = "Complete registration";
		emailService.sendMail(from, to, subject, thml);
		if (p.getName().equals(e.getUsername())) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(req, res, auth);
			}

			return "redirect:/";
		}
		return "redirect:/users";
	}

	@GetMapping("/pwd/{data}")
	public String verifyPwd(@PathVariable String data, Model m, Principal p) {
		String idPwd = MyUtil.decode(data);
		Long id = Long.parseLong(idPwd.split("-")[0]);
		String pwd = idPwd.split("-")[1];
		User user = userRepository.findById(id);
		m.addAttribute("username", user.getUsername());
		m.addAttribute("password", pwd);

		return "users/defaultpwd";
	}

	@GetMapping("/changepwd")
	public String changePwd(Model m) {
		return "users/changepwd";
	}

	@PostMapping("/changepwd")
	public String saveChangePwd(Model m, User user) {
		if (user.getPassword().equals(user.getPassword2())) {
			userRepository.updateChangePwd(passwordEncoder.encode(user.getPassword()), user.getId());
			return "redirect:/";
		} else {
			return "users/changepwd";
		}

	}
}
