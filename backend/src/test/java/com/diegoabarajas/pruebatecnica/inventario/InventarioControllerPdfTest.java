package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.inventario.InventarioController;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.security.SecurityConfig;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventarioController.class)
@Import(SecurityConfig.class)
class InventarioControllerPdfTest {

	@MockitoBean
	InventarioService inventarioService;

	@MockitoBean
	UsuarioRepository usuarioRepository;

	@Autowired
	MockMvc mvc;

	@Test
	void pdf_returnsAttachmentPdf() throws Exception {
		when(inventarioService.buildPdf("900")).thenReturn("%PDF-1.4\n%...".getBytes());

		mvc.perform(get("/api/inventario/pdf").param("empresaNit", "900"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment; filename=\"inventario_900.pdf\"")))
				.andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/pdf")));
	}
}

