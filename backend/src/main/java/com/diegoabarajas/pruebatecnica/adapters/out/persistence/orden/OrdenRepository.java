package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
	List<Orden> findByClienteId(Long clienteId);
}
