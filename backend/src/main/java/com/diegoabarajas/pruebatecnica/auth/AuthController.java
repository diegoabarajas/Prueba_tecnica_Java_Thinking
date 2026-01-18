package com.diegoabarajas.pruebatecnica.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@GetMapping("/me")
	public AuthMeResponse me(Authentication auth) {
		String email = auth.getName();
		String role = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(a -> a.startsWith("ROLE_"))
				.map(a -> a.substring("ROLE_".length()))
				.findFirst()
				.orElse("EXTERNO");
		return new AuthMeResponse(email, role);
	}
}

