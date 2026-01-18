package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.empresa.Empresa;
import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

	@Mock
	ProductoRepository productoRepository;

	@Mock
	EmpresaRepository empresaRepository;

	@InjectMocks
	ProductoService productoService;

	@Test
	void create_whenCodigoExists_throws409() {
		when(productoRepository.existsById("P1")).thenReturn(true);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> productoService.create(new ProductoRequest("P1", "X", null, "900")));

		assertEquals(409, ex.getStatusCode().value());
	}

	@Test
	void create_whenEmpresaNotFound_throws400() {
		when(productoRepository.existsById("P1")).thenReturn(false);
		when(empresaRepository.findById("900")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> productoService.create(new ProductoRequest("P1", "X", null, "900")));

		assertEquals(400, ex.getStatusCode().value());
		verify(productoRepository, never()).save(any());
	}

	@Test
	void create_whenOk_savesProducto() {
		when(productoRepository.existsById("P1")).thenReturn(false);
		Empresa empresa = new Empresa();
		empresa.setNit("900");
		empresa.setNombre("Empresa");
		when(empresaRepository.findById("900")).thenReturn(Optional.of(empresa));
		when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		ProductoResponse res = productoService.create(new ProductoRequest("P1", "X", "C", "900"));

		assertEquals("P1", res.codigo());
		assertEquals("900", res.empresaNit());
		verify(productoRepository).save(any());
	}
}

