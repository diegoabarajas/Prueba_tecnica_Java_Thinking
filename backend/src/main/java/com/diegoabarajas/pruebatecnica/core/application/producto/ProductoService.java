package com.diegoabarajas.pruebatecnica.core.application.producto;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductPriceItem;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductPriceRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
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

	private final ProductRepositoryPort productRepository;
	private final CompanyRepositoryPort companyRepository;
	private final ProductPriceRepositoryPort productPriceRepository;

	public ProductoService(
			ProductRepositoryPort productRepository,
			CompanyRepositoryPort companyRepository,
			ProductPriceRepositoryPort productPriceRepository
	) {
		this.productRepository = productRepository;
		this.companyRepository = companyRepository;
		this.productPriceRepository = productPriceRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductWithPrices> list(String empresaNit) {
		List<Product> productos = (empresaNit == null || empresaNit.isBlank())
				? productRepository.findAll()
				: productRepository.findByEmpresaNit(empresaNit);

		if (productos.isEmpty()) return List.of();

		List<String> codigos = productos.stream().map(Product::codigo).toList();
		Map<String, List<ProductPrice>> preciosByCodigo = productPriceRepository.findByProductoCodigos(codigos).stream()
				.collect(Collectors.groupingBy(ProductPriceItem::productoCodigo,
						Collectors.mapping(p -> new ProductPrice(p.moneda(), p.precio()), Collectors.toList())));

		return productos.stream()
				.map(p -> new ProductWithPrices(p, preciosByCodigo.getOrDefault(p.codigo(), List.of())))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ProductWithPrices> listByCodigos(List<String> codigos) {
		if (codigos == null || codigos.isEmpty()) return List.of();
		List<Product> productos = productRepository.findByCodigos(codigos);
		if (productos.isEmpty()) return List.of();

		List<String> keys = productos.stream().map(Product::codigo).toList();
		Map<String, List<ProductPrice>> preciosByCodigo = productPriceRepository.findByProductoCodigos(keys).stream()
				.collect(Collectors.groupingBy(ProductPriceItem::productoCodigo,
						Collectors.mapping(p -> new ProductPrice(p.moneda(), p.precio()), Collectors.toList())));

		return productos.stream()
				.map(p -> new ProductWithPrices(p, preciosByCodigo.getOrDefault(p.codigo(), List.of())))
				.toList();
	}

	@Transactional
	public ProductWithPrices create(CreateProductCommand req) {
		if (productRepository.existsByCodigo(req.codigo())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
		}
		if (!companyRepository.existsByNit(req.empresaNit())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa no existe: " + req.empresaNit());
		}

		Product saved = productRepository.save(new Product(req.codigo(), req.nombre(), req.caracteristicas(), req.empresaNit()));

		List<ProductPrice> preciosReq = req.precios() == null ? List.of() : req.precios().stream().filter(Objects::nonNull).toList();
		Map<String, ProductPrice> unique = new HashMap<>();
		for (ProductPrice pr : preciosReq) {
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
			unique.put(moneda, new ProductPrice(moneda, pr.precio()));
		}

		List<ProductPrice> preciosSaved = productPriceRepository.saveAll(saved.codigo(), unique.values().stream().toList());
		return new ProductWithPrices(saved, preciosSaved);
	}
}

