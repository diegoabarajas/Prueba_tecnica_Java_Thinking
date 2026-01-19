package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.orden.Orden;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia (outbound port) para órdenes.
 */
public interface OrdenRepositoryPort {
	List<Orden> findAll();

	List<Orden> findByClienteId(Long clienteId);

	Optional<Orden> findById(Long id);

	Orden save(Orden orden);
}
