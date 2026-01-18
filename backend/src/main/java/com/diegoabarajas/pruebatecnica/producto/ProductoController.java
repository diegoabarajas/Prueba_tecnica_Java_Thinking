package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.empresa.Empresa;
import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;

	public ProductoController(ProductoRepository productoRepository, EmpresaRepository empresaRepository) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
	}

	@GetMapping
	public List<ProductoResponse> list(@RequestParam(required = false) String empresaNit) {
		List<Producto> productos = (empresaNit == null || empresaNit.isBlank())
				? productoRepository.findAll()
				: productoRepository.findByEmpresa_Nit(empresaNit);
		return productos.stream().map(ProductoResponse::fromEntity).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse create(@Valid @RequestBody ProductoRequest req) {
		if (productoRepository.existsById(req.codigo())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
		}

		Empresa empresa = empresaRepository.findById(req.empresaNit())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa no existe: " + req.empresaNit()));

		Producto p = new Producto();
		p.setCodigo(req.codigo());
		p.setNombre(req.nombre());
		p.setCaracteristicas(req.caracteristicas());
		p.setEmpresa(empresa);
		return ProductoResponse.fromEntity(productoRepository.save(p));
	}
}

