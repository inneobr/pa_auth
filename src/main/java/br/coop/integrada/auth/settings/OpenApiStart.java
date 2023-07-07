package br.coop.integrada.auth.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*")
public class OpenApiStart {
	@GetMapping("/swagger-pa-auth")
	public String index() {
		return "/swagger-pa-auth/openapi/swagger-ui.html";
	}
}
