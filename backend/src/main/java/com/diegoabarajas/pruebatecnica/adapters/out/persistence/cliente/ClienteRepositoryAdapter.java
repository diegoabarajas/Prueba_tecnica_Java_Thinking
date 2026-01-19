package com.diegoabarajas.pruebatecnica.adapters.out.persistence.cliente;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ClienteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA que implementa ClienteRepositoryPort.
 */
@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

	private final ClienteRepository jpaRepository;

	public ClienteRepositoryAdapter(ClienteRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public List<com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente> findAll() {
		return jpaRepository.findAll().stream().map(this::toCore).toList();
	}

	@Override
	public Optional<com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente> findById(Long id) {
		return jpaRepository.findById(id).map(this::toCore);
	}

	@Override
	public Optional<com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente> findByCorreo(String correo) {
		return jpaRepository.findByCorreoIgnoreCase(correo).map(this::toCore);
	}

	@Override
	public boolean existsById(Long id) {
		return jpaRepository.existsById(id);
	}

	@Override
	public boolean existsByCorreo(String correo) {
		return jpaRepository.findByCorreoIgnoreCase(correo).isPresent();
	}

	@Override
	public com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente save(com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente cliente) {
		Cliente entity = toEntity(cliente);
		Cliente saved = jpaRepository.save(entity);
		return toCore(saved);
	}

	@Override
	public void deleteById(Long id) {
		jpaRepository.deleteById(id);
	}

	private com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente toCore(Cliente entity) {
		return new com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente(entity.getId(), entity.getCorreo(), entity.getNombre());
	}

	private Cliente toEntity(com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente core) {
		Cliente entity = new Cliente();
		if (core.id() != null) {
			entity.setId(core.id());
		}
		entity.setCorreo(core.correo());
		entity.setNombre(core.nombre());
		return entity;
	}
}
