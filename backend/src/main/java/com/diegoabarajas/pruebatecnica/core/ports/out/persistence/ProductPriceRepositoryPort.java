package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductPrice;

import java.util.Collection;
import java.util.List;

/**
 * Puerto de persistencia (outbound port) para precios de productos.
 */
public interface ProductPriceRepositoryPort {
	List<ProductPriceItem> findByProductoCodigos(Collection<String> codigos);

	List<ProductPrice> saveAll(String productoCodigo, List<ProductPrice> precios);
}

