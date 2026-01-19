package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.CreateProductCommand;
import com.diegoabarajas.pruebatecnica.core.application.producto.ProductPrice;
import com.diegoabarajas.pruebatecnica.core.application.producto.ProductWithPrices;
import com.diegoabarajas.pruebatecnica.core.application.producto.ProductoService;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductPriceRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

	@Mock
	ProductRepositoryPort productRepository;

	@Mock
	CompanyRepositoryPort companyRepository;

	@Mock
	ProductPriceRepositoryPort productPriceRepository;

	@InjectMocks
	ProductoService productoService;

	@Test
	void create_whenCodigoExists_throws409() {
		when(productRepository.existsByCodigo("P1")).thenReturn(true);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> productoService.create(new CreateProductCommand("P1", "X", null, "900", List.of())));

		assertEquals(409, ex.getStatusCode().value());
	}

	@Test
	void create_whenEmpresaNotFound_throws400() {
		when(productRepository.existsByCodigo("P1")).thenReturn(false);
		when(companyRepository.existsByNit("900")).thenReturn(false);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> productoService.create(new CreateProductCommand("P1", "X", null, "900", List.of())));

		assertEquals(400, ex.getStatusCode().value());
		verify(productRepository, never()).save(any());
	}

	@Test
	void create_whenOk_savesProducto() {
		when(productRepository.existsByCodigo("P1")).thenReturn(false);
		when(companyRepository.existsByNit("900")).thenReturn(true);
		when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(productPriceRepository.saveAll(eq("P1"), any())).thenAnswer(inv -> inv.getArgument(1));

		ProductWithPrices res = productoService.create(new CreateProductCommand(
				"P1",
				"X",
				"C",
				"900",
				List.of(new ProductPrice("cop", 10.5))
		));

		assertEquals("P1", res.product().codigo());
		assertEquals("900", res.product().empresaNit());
		assertEquals(1, res.precios().size());
		assertEquals("COP", res.precios().get(0).moneda());
		verify(productRepository).save(any());
		verify(productPriceRepository).saveAll(eq("P1"), any());
	}
}

