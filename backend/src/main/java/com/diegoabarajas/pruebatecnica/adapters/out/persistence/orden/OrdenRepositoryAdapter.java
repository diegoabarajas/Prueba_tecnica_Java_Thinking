package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.OrdenRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador JPA que implementa OrdenRepositoryPort.
 */
@Component
public class OrdenRepositoryAdapter implements OrdenRepositoryPort {

	private final OrdenRepository jpaRepository;

	public OrdenRepositoryAdapter(OrdenRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public List<com.diegoabarajas.pruebatecnica.core.application.orden.Orden> findAll() {
		return jpaRepository.findAll().stream().map(this::toCore).toList();
	}

	@Override
	public List<com.diegoabarajas.pruebatecnica.core.application.orden.Orden> findByClienteId(Long clienteId) {
		return jpaRepository.findByClienteId(clienteId).stream().map(this::toCore).toList();
	}

	@Override
	public java.util.Optional<com.diegoabarajas.pruebatecnica.core.application.orden.Orden> findById(Long id) {
		return jpaRepository.findById(id).map(this::toCore);
	}

	@Override
	public com.diegoabarajas.pruebatecnica.core.application.orden.Orden save(com.diegoabarajas.pruebatecnica.core.application.orden.Orden orden) {
		Orden entity = toEntity(orden);
		Orden saved = jpaRepository.save(entity);
		return toCore(saved);
	}

	private com.diegoabarajas.pruebatecnica.core.application.orden.Orden toCore(Orden entity) {
		return new com.diegoabarajas.pruebatecnica.core.application.orden.Orden(entity.getId(), entity.getClienteId(), entity.getFechaCreacion(), List.of());
	}

	private Orden toEntity(com.diegoabarajas.pruebatecnica.core.application.orden.Orden core) {
		Orden entity = new Orden();
		if (core.id() != null) {
			entity.setId(core.id());
		}
		entity.setClienteId(core.clienteId());
		entity.setFechaCreacion(core.fechaCreacion());
		return entity;
	}
}
