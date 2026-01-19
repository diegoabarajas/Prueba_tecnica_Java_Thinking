package com.diegoabarajas.pruebatecnica.adapters.in.rest.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	/**
	 * Devuelve informaciÃ³n mÃ­nima del usuario autenticado.
	 *
	 * <p>Uso principal:
	 * - El frontend usa este endpoint para validar credenciales (HTTP Basic) y obtener el rol real desde backend,
	 *   en lugar de â€œasumirâ€ un rol en el cliente.
	 *
	 * <p>Notas:
	 * - Spring Security inyecta {@link Authentication} ya resuelto por el filtro de seguridad.
	 * - El rol viene como authority con prefijo "ROLE_" (convenciÃ³n Spring).
	 */
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

