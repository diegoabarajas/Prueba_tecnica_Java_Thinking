package com.diegoabarajas.pruebatecnica.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductoPrecioRepository extends JpaRepository<ProductoPrecio, Long> {
	List<ProductoPrecio> findByProducto_CodigoInOrderByIdAsc(Collection<String> codigos);
}

