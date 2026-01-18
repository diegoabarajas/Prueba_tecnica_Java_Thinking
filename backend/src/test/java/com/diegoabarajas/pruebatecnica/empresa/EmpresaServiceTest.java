package com.diegoabarajas.pruebatecnica.empresa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

	@Mock
	EmpresaRepository empresaRepository;

	@InjectMocks
	EmpresaService empresaService;

	@Test
	void create_whenNitAlreadyExists_throws409() {
		when(empresaRepository.existsById("900")).thenReturn(true);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> empresaService.create(new EmpresaRequest("900", "X", null, null)));

		assertEquals(409, ex.getStatusCode().value());
	}

	@Test
	void update_whenNitMismatch_throws400() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> empresaService.update("900", new EmpresaRequest("901", "X", null, null)));

		assertEquals(400, ex.getStatusCode().value());
	}

	@Test
	void get_whenNotFound_throws404() {
		when(empresaRepository.findById("900")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> empresaService.get("900"));

		assertEquals(404, ex.getStatusCode().value());
	}

	@Test
	void delete_whenNotFound_throws404() {
		when(empresaRepository.existsById("900")).thenReturn(false);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> empresaService.delete("900"));

		assertEquals(404, ex.getStatusCode().value());
		verify(empresaRepository, never()).deleteById(any());
	}
}

