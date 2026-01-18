package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.producto.Producto;
import com.diegoabarajas.pruebatecnica.producto.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@InjectMocks
	InventarioService inventarioService;

	@Test
	void list_whenEmpresaNitBlank_throws400() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list(" "));
		assertEquals(400, ex.getStatusCode().value());
	}

	@Test
	void list_whenEmpresaNotFound_throws404() {
		when(empresaRepository.existsById("900")).thenReturn(false);
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> inventarioService.list("900"));
		assertEquals(404, ex.getStatusCode().value());
	}

	@Test
	void buildPdf_returnsNonEmptyBytes() {
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

