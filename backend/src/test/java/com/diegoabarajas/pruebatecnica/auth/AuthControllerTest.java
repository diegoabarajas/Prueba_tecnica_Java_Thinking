package com.diegoabarajas.pruebatecnica.auth;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.auth.AuthController;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void me_withoutAuth_is401() throws Exception {
		mvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void me_withAuth_returnsEmailAndRole() throws Exception {
		mvc.perform(get("/api/auth/me")
						.with(user("admin@local.test").roles("ADMIN")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("admin@local.test"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
	}
}

