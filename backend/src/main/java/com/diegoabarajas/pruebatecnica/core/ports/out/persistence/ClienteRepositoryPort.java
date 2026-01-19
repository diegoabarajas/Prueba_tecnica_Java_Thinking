package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia (outbound port) para clientes.
 */
public interface ClienteRepositoryPort {
	List<Cliente> findAll();

	Optional<Cliente> findById(Long id);

	Optional<Cliente> findByCorreo(String correo);

	boolean existsById(Long id);

	boolean existsByCorreo(String correo);

	Cliente save(Cliente cliente);

	void deleteById(Long id);
}
