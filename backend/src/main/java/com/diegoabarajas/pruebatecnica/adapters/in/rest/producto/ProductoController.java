package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.CreateProductCommand;
import com.diegoabarajas.pruebatecnica.core.application.producto.ProductPrice;
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
		return productoService.list(empresaNit).stream().map(ProductoResponse::fromCore).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse create(@Valid @RequestBody ProductoRequest req) {
		List<ProductPrice> precios = (req.precios() == null ? List.<ProductoPrecioRequest>of() : req.precios()).stream()
				.filter(java.util.Objects::nonNull)
				.map(p -> new ProductPrice(p.moneda(), p.precio()))
				.toList();
		CreateProductCommand cmd = new CreateProductCommand(
				req.codigo(),
				req.nombre(),
				req.caracteristicas(),
				req.empresaNit(),
				precios
		);
		return ProductoResponse.fromCore(productoService.create(cmd));
	}
}

