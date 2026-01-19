package com.diegoabarajas.pruebatecnica.security;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria.CategoryController;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.Usuario;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import com.diegoabarajas.pruebatecnica.core.application.categoria.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@Import(SecurityConfig.class)
class CategoriasSecuritySmokeTest {

	@MockitoBean
	CategoryService categoryService;

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void getCategorias_withoutAuth_is200() throws Exception {
		mvc.perform(get("/api/categorias"))
				.andExpect(status().isOk());
	}

	@Test
	void postCategorias_withoutAuth_is401() throws Exception {
		mvc.perform(post("/api/categorias")
						.contentType("application/json")
						.content("{\"nombre\":\"Tech\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void postCategorias_withExterno_is403() throws Exception {
		when(usuarioRepository.findByEmailIgnoreCase("externo@local.test"))
				.thenReturn(Optional.of(user("externo@local.test", "EXTERNO")));

		mvc.perform(post("/api/categorias")
						.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic(
								"externo@local.test", "ChangeMe123!"))
						.contentType("application/json")
						.content("{\"nombre\":\"Tech\"}"))
				.andExpect(status().isForbidden());
	}

	private static Usuario user(String email, String rol) {
		Usuario u = new Usuario();
		u.setEmail(email);
		u.setRol(rol);
		u.setPasswordHash(new BCryptPasswordEncoder().encode("ChangeMe123!"));
		return u;
	}
}

