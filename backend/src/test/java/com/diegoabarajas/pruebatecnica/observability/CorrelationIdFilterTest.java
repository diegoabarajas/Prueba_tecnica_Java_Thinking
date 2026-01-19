package com.diegoabarajas.pruebatecnica.observability;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.health.HealthController;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.observability.CorrelationIdFilter;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class)
@Import({CorrelationIdFilter.class, SecurityConfig.class})
class CorrelationIdFilterTest {

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void addsCorrelationIdHeader() throws Exception {
		mvc.perform(get("/api/health"))
				.andExpect(status().isOk())
				.andExpect(header().exists(CorrelationIdFilter.HEADER_NAME));
	}
}

