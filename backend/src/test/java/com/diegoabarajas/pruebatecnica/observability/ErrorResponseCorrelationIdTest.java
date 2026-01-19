package com.diegoabarajas.pruebatecnica.observability;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa.EmpresaController;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.error.ApiExceptionHandler;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.observability.CorrelationIdFilter;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import com.diegoabarajas.pruebatecnica.core.application.empresa.EmpresaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmpresaController.class)
@Import({SecurityConfig.class, CorrelationIdFilter.class, ApiExceptionHandler.class})
class ErrorResponseCorrelationIdTest {

	@MockitoBean
	EmpresaService empresaService;

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void errorResponse_includesCorrelationIdInBodyAndHeader() throws Exception {
		when(empresaService.get("NOPE")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));

		mvc.perform(get("/api/empresas/NOPE"))
				.andExpect(status().isNotFound())
				.andExpect(header().exists(CorrelationIdFilter.HEADER_NAME))
				.andExpect(jsonPath("$.correlationId").exists())
				.andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
	}
}

