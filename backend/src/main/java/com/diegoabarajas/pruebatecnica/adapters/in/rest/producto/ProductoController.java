package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public List<ProductoResponse> list(@RequestParam(required = false) String empresaNit) {
		return productoService.list(empresaNit);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse create(@Valid @RequestBody ProductoRequest req) {
		return productoService.create(req);
	}
}

