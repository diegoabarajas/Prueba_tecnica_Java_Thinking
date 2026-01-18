package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.producto.Producto;
import com.diegoabarajas.pruebatecnica.producto.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;

	public InventarioController(ProductoRepository productoRepository, EmpresaRepository empresaRepository) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
	}

	/**
	 * Inventario inicial: productos por empresa.
	 * Más adelante podemos extenderlo a una tabla inventario con cantidades, etc.
	 */
	@GetMapping
	public List<InventarioItemResponse> list(@RequestParam String empresaNit) {
		if (empresaNit == null || empresaNit.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empresaNit es requerido");
		}
		if (!empresaRepository.existsById(empresaNit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no existe: " + empresaNit);
		}
		List<Producto> productos = productoRepository.findByEmpresa_Nit(empresaNit);
		return productos.stream()
				.map(p -> new InventarioItemResponse(
						empresaNit,
						p.getCodigo(),
						p.getNombre(),
						p.getCaracteristicas()
				))
				.toList();
	}
}

