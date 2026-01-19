package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.producto.Product;

import java.util.Collection;
import java.util.List;

/**
 * Puerto de persistencia (outbound port) para productos.
 */
public interface ProductRepositoryPort {
	boolean existsByCodigo(String codigo);

	List<Product> findAll();

	List<Product> findByEmpresaNit(String empresaNit);

	List<Product> findByCodigos(Collection<String> codigos);

	Product save(Product product);
}

