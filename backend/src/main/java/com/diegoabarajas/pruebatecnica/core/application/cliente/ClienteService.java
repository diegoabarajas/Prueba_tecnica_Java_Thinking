package com.diegoabarajas.pruebatecnica.core.application.cliente;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ClienteRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Casos de uso para clientes (CRUD).
 */
@Service
public class ClienteService {

	private final ClienteRepositoryPort clienteRepository;

	public ClienteService(ClienteRepositoryPort clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@Transactional(readOnly = true)
	public List<Cliente> list() {
		return clienteRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Cliente get(Long id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
	}

	@Transactional
	public Cliente create(UpsertClienteCommand cmd) {
		if (cmd == null || cmd.correo() == null || cmd.correo().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "correo es requerido");
		}
		if (clienteRepository.existsByCorreo(cmd.correo().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese correo");
		}
		return clienteRepository.save(new Cliente(null, cmd.correo().trim(), cmd.nombre() == null ? null : cmd.nombre().trim()));
	}

	@Transactional
	public Cliente update(Long id, UpsertClienteCommand cmd) {
		if (!clienteRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
		}
		if (cmd == null || cmd.correo() == null || cmd.correo().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "correo es requerido");
		}
		// Verificar que el correo no esté en uso por otro cliente
		clienteRepository.findByCorreo(cmd.correo().trim()).ifPresent(existing -> {
			if (!existing.id().equals(id)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro cliente con ese correo");
			}
		});
		return clienteRepository.save(new Cliente(id, cmd.correo().trim(), cmd.nombre() == null ? null : cmd.nombre().trim()));
	}

	@Transactional
	public void delete(Long id) {
		if (!clienteRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
		}
		clienteRepository.deleteById(id);
	}
}
