package com.diegoabarajas.pruebatecnica.security;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa.EmpresaController;
import com.diegoabarajas.pruebatecnica.core.application.empresa.EmpresaService;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.Usuario;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmpresaController.class)
@Import(SecurityConfig.class)
class SecuritySmokeTest {

	@MockitoBean
	EmpresaService empresaService;

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void postEmpresas_withoutAuth_is401() throws Exception {
		mvc.perform(post("/api/empresas")
						.contentType("application/json")
						.content("{\"nit\":\"1\",\"nombre\":\"X\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void postEmpresas_withExterno_is403() throws Exception {
		when(usuarioRepository.findByEmailIgnoreCase("externo@local.test"))
				.thenReturn(Optional.of(user("externo@local.test", "EXTERNO")));

		mvc.perform(post("/api/empresas")
						.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic(
								"externo@local.test", "ChangeMe123!"))
						.contentType("application/json")
						.content("{\"nit\":\"1\",\"nombre\":\"X\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void getEmpresas_withoutAuth_is200() throws Exception {
		mvc.perform(get("/api/empresas"))
				.andExpect(status().isOk());
	}

	private static Usuario user(String email, String rol) {
		Usuario u = new Usuario();
		u.setEmail(email);
		u.setRol(rol);
		u.setPasswordHash(new BCryptPasswordEncoder().encode("ChangeMe123!"));
		return u;
	}
}

