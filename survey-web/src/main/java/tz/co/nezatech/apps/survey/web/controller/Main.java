package tz.co.nezatech.apps.survey.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Main {

	@GetMapping("/")
	public String index() {
		return "redirect:/home";
	}

	@GetMapping("/403")
	public String _403() {
		return "403";
	}

	@GetMapping("/home")
	public String home(Model m) {
		m.addAttribute("homeMenu", true);
		return "home";
	}

	@PostMapping("/home")
	public String homePost() {
		return "redirect:/home";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
