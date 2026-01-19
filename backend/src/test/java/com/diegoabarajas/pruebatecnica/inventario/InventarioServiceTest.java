package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.adapters.out.pdf.InventarioPdfRenderer;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.Producto;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.ProductoRepository;
import com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService;
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
	ProductoRepository productoRepository;

	@Mock
	EmpresaRepository empresaRepository;

	InventarioService inventarioService;

	@Test
	void list_whenEmpresaNitBlank_throws400() {
		inventarioService = new InventarioService(productoRepository, empresaRepository, new InventarioPdfRenderer());
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list(" "));
		assertEquals(400, ex.getStatusCode().value());
	}

	@Test
	void list_whenEmpresaNotFound_throws404() {
		inventarioService = new InventarioService(productoRepository, empresaRepository, new InventarioPdfRenderer());
		when(empresaRepository.existsById("900")).thenReturn(false);
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list("900"));
		assertEquals(404, ex.getStatusCode().value());
	}

	@Test
	void buildPdf_returnsNonEmptyBytes() {
		inventarioService = new InventarioService(productoRepository, empresaRepository, new InventarioPdfRenderer());
		when(empresaRepository.existsById("900")).thenReturn(true);
		Producto p = new Producto();
		p.setCodigo("P1");
		p.setNombre("Laptop");
		p.setCaracteristicas("C");
		when(productoRepository.findByEmpresa_Nit("900")).thenReturn(List.of(p));

		byte[] bytes = inventarioService.buildPdf("900");
		assertNotNull(bytes);
		assertTrue(bytes.length > 100);
	}
}

