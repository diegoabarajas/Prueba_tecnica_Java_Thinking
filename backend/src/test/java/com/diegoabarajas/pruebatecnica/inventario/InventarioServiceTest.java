package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService;
import com.diegoabarajas.pruebatecnica.core.application.producto.Product;
import com.diegoabarajas.pruebatecnica.core.ports.out.pdf.InventarioPdfRendererPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

	@Mock
	ProductRepositoryPort productRepository;

	@Mock
	CompanyRepositoryPort companyRepository;

	@Mock
	InventarioPdfRendererPort pdfRenderer;

	InventarioService inventarioService;

	@Test
	void list_whenEmpresaNitBlank_throws400() {
		inventarioService = new InventarioService(productRepository, companyRepository, pdfRenderer);
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list(" "));
		assertEquals(400, ex.getStatusCode().value());
	}

	@Test
	void list_whenEmpresaNotFound_throws404() {
		inventarioService = new InventarioService(productRepository, companyRepository, pdfRenderer);
		when(companyRepository.existsByNit("900")).thenReturn(false);
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list("900"));
		assertEquals(404, ex.getStatusCode().value());
	}

	@Test
	void buildPdf_returnsNonEmptyBytes() {
		inventarioService = new InventarioService(productRepository, companyRepository, pdfRenderer);
		when(companyRepository.existsByNit("900")).thenReturn(true);
		when(productRepository.findByEmpresaNit("900"))
				.thenReturn(List.of(new Product("P1", "Laptop", "C", "900")));
		try {
			when(pdfRenderer.render(eq("900"), anyList())).thenReturn("%PDF-1.4\n%...".getBytes());
		} catch (Exception e) {
			fail(e);
		}

		byte[] bytes = inventarioService.buildPdf("900");
		assertNotNull(bytes);
		assertTrue(bytes.length > 10);
	}
}

