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

import tz.co.nezatech.apps.survey.model.User;
import tz.co.nezatech.apps.survey.repository.RoleRepository;
import tz.co.nezatech.apps.survey.repository.UserRepository;
import tz.co.nezatech.apps.survey.web.MyUtil;
import tz.co.nezatech.apps.survey.web.service.EmailService;

@Controller
@RequestMapping("/pwd")
@PreAuthorize("permitAll")
public class UserPwdController {
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	EmailService emailService;

	@GetMapping("/reset/{id}")
	@PreAuthorize("hasAnyAuthority('makePasswordReset')")
	public String resetPwd(Model m, @PathVariable Long id, Principal p, HttpServletRequest req,
			HttpServletResponse res) {
		User e = userRepository.findById(id);
		// set password
		String pwd = MyUtil.alphaNumericRandom(6);
		e.setPassword(passwordEncoder.encode(pwd));
		userRepository.resetPwd(e);

		String url = emailService.url("/pwd/verify/" + MyUtil.encode(e.getId() + "-" + pwd), req);

		// encode and send mail
		String thml = "<div style=\"border: 1px solid #333;padding: 0.5rem;\">Welcome to Survey Tool. Your account is reset. Kindly click below link to access your account. <br/> <br/> "
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
			m.addAttribute("msg", "Your password is successfully reset. Kindly check your email inbox");
			return "redirect:/home";
		}
		return "redirect:/users";
	}

	@GetMapping("/reset")
	@PreAuthorize("hasAnyAuthority('makeMyOwnPasswordReset')")
	public String resetMyPwd(Model m, Principal p, HttpServletRequest req, HttpServletResponse res) {
		List<User> tmp = userRepository.getAll("username", p.getName());
		User e = tmp.get(0);
		// set password
		String pwd = MyUtil.alphaNumericRandom(6);
		e.setPassword(passwordEncoder.encode(pwd));
		userRepository.resetPwd(e);

		String url = emailService.url("/pwd/verify/" + MyUtil.encode(e.getId() + "-" + pwd), req);

		// encode and send mail
		String thml = "<div style=\"border: 1px solid #333;padding: 0.5rem;\">Welcome to Survey Tool. Your account is reset. Kindly click below link to access your account. <br/> <br/> "
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
			m.addAttribute("msg", "Your password is successfully reset. Kindly check your email inbox");
			return "redirect:/home";
		}
		return "redirect:/users";
	}

	@GetMapping("/verify/{data}")
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
	@PreAuthorize("hasAnyAuthority('changePassword')")
	public String changePwd(Model m) {
		return "users/changepwd";
	}

	@PostMapping("/changepwd")
	@PreAuthorize("hasAnyAuthority('changePassword')")
	public String saveChangePwd(Model m, User user) {
		if (user.getPassword().equals(user.getPassword2())) {
			userRepository.updateChangePwd(passwordEncoder.encode(user.getPassword()), user.getId());
			return "redirect:/";
		} else {
			return "users/changepwd";
		}
	}
}
