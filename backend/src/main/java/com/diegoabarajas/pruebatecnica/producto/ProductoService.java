package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.empresa.Empresa;
import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;
	private final ProductoPrecioRepository productoPrecioRepository;

	public ProductoService(
			ProductoRepository productoRepository,
			EmpresaRepository empresaRepository,
			ProductoPrecioRepository productoPrecioRepository
	) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
		this.productoPrecioRepository = productoPrecioRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductoResponse> list(String empresaNit) {
		List<Producto> productos = (empresaNit == null || empresaNit.isBlank())
				? productoRepository.findAll()
				: productoRepository.findByEmpresa_Nit(empresaNit);

		if (productos.isEmpty()) return List.of();

		List<String> codigos = productos.stream().map(Producto::getCodigo).toList();
		Map<String, List<ProductoPrecioResponse>> preciosByCodigo = productoPrecioRepository.findByProducto_CodigoIn(codigos).stream()
				.collect(Collectors.groupingBy(p -> p.getProducto().getCodigo(),
						Collectors.mapping(ProductoPrecioResponse::fromEntity, Collectors.toList())));

		return productos.stream()
				.map(p -> new ProductoResponse(
						p.getCodigo(),
						p.getNombre(),
						p.getCaracteristicas(),
						p.getEmpresa().getNit(),
						preciosByCodigo.getOrDefault(p.getCodigo(), List.of())
				))
				.toList();
	}

	@Transactional
	public ProductoResponse create(ProductoRequest req) {
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
		Producto saved = productoRepository.save(p);

		List<ProductoPrecioRequest> preciosReq = req.precios() == null ? List.of() : req.precios().stream().filter(Objects::nonNull).toList();
		Map<String, ProductoPrecioRequest> unique = new HashMap<>();
		for (ProductoPrecioRequest pr : preciosReq) {
			String moneda = pr.moneda() == null ? "" : pr.moneda().trim().toUpperCase(Locale.ROOT);
			if (moneda.isBlank()) continue;
			if (unique.containsKey(moneda)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moneda duplicada: " + moneda);
			}
			unique.put(moneda, new ProductoPrecioRequest(moneda, pr.precio()));
		}

		List<ProductoPrecioResponse> preciosSaved = unique.values().stream().map(pr -> {
			ProductoPrecio precio = new ProductoPrecio();
			precio.setProducto(saved);
			precio.setMoneda(pr.moneda());
			precio.setPrecio(pr.precio());
			return ProductoPrecioResponse.fromEntity(productoPrecioRepository.save(precio));
		}).toList();

		return new ProductoResponse(
				saved.getCodigo(),
				saved.getNombre(),
				saved.getCaracteristicas(),
				saved.getEmpresa().getNit(),
				preciosSaved
		);
	}
}

