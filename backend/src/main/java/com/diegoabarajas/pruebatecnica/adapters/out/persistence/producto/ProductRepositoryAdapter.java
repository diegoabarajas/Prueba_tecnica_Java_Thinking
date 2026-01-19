package com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto;

import com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.core.application.producto.Product;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador outbound: implementa {@link ProductRepositoryPort} usando Spring Data JPA.
 */
@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;

	public ProductRepositoryAdapter(ProductoRepository productoRepository, EmpresaRepository empresaRepository) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
	}

	@Override
	public boolean existsByCodigo(String codigo) {
		return productoRepository.existsById(codigo);
	}

	@Override
	public List<Product> findAll() {
		return productoRepository.findAll().stream().map(ProductRepositoryAdapter::toCore).toList();
	}

	@Override
	public List<Product> findByEmpresaNit(String empresaNit) {
		return productoRepository.findByEmpresa_Nit(empresaNit).stream().map(ProductRepositoryAdapter::toCore).toList();
	}

	@Override
	public List<Product> findByCodigos(Collection<String> codigos) {
		return productoRepository.findAllById(codigos).stream().map(ProductRepositoryAdapter::toCore).toList();
	}

	@Override
	public Product save(Product product) {
		Producto p = new Producto();
		p.setCodigo(product.codigo());
		p.setNombre(product.nombre());
		p.setCaracteristicas(product.caracteristicas());
		// La empresa se resuelve aquí porque la entidad JPA requiere relación.
		p.setEmpresa(empresaRepository.findById(product.empresaNit()).orElseThrow());
		Producto saved = productoRepository.save(p);
		return toCore(saved);
	}

	private static Product toCore(Producto p) {
		return new Product(p.getCodigo(), p.getNombre(), p.getCaracteristicas(), p.getEmpresa().getNit());
	}
}

