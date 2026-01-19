package com.diegoabarajas.pruebatecnica.core.application.producto;

import com.diegoabarajas.pruebatecnica.adapters.in.rest.producto.ProductoPrecioRequest;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.producto.ProductoPrecioResponse;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.producto.ProductoRequest;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.producto.ProductoResponse;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa.Empresa;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.Producto;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.ProductoPrecio;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.ProductoPrecioRepository;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductoService {

	private static final Set<String> MONEDAS_PERMITIDAS = Set.of("COP", "USD", "UE");

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
		Map<String, List<ProductoPrecioResponse>> preciosByCodigo = productoPrecioRepository.findByProducto_CodigoInOrderByIdAsc(codigos).stream()
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
			if (!MONEDAS_PERMITIDAS.contains(moneda)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moneda no permitida: " + moneda);
			}
			if (pr.precio() == null || !(pr.precio() > 0)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Precio inválido para " + moneda);
			}
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

