package com.diegoabarajas.pruebatecnica.core.application.orden;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ClienteRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.OrdenItemRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.OrdenRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Casos de uso para órdenes.
 */
@Service
public class OrdenService {

	private final OrdenRepositoryPort ordenRepository;
	private final ClienteRepositoryPort clienteRepository;
	private final ProductRepositoryPort productRepository;
	private final OrdenItemRepositoryPort ordenItemRepository;

	public OrdenService(
			OrdenRepositoryPort ordenRepository,
			ClienteRepositoryPort clienteRepository,
			ProductRepositoryPort productRepository,
			OrdenItemRepositoryPort ordenItemRepository
	) {
		this.ordenRepository = ordenRepository;
		this.clienteRepository = clienteRepository;
		this.productRepository = productRepository;
		this.ordenItemRepository = ordenItemRepository;
	}

	@Transactional(readOnly = true)
	public List<Orden> list() {
		return ordenRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Orden> listByCliente(Long clienteId) {
		return ordenRepository.findByClienteId(clienteId);
	}

	@Transactional(readOnly = true)
	public Orden get(Long id) {
		Orden orden = ordenRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
		// Cargar items de la orden
		List<OrdenItem> items = ordenItemRepository.findItemsByOrdenId(id);
		return new Orden(orden.id(), orden.clienteId(), orden.fechaCreacion(), items);
	}

	@Transactional
	public Orden create(CreateOrdenCommand cmd) {
		if (cmd == null || cmd.clienteId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es requerido");
		}
		if (!clienteRepository.existsById(cmd.clienteId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no existe: " + cmd.clienteId());
		}
		if (cmd.items() == null || cmd.items().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe tener al menos un item");
		}

		// Validar que todos los productos existan
		for (OrdenItem item : cmd.items()) {
			if (item.cantidad() == null || item.cantidad() <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida para producto: " + item.productoCodigo());
			}
			if (!productRepository.existsByCodigo(item.productoCodigo())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe: " + item.productoCodigo());
			}
		}

		// Crear orden
		Orden orden = ordenRepository.save(new Orden(null, cmd.clienteId(), Instant.now(), List.of()));

		// Guardar items
		ordenItemRepository.replaceOrdenItems(orden.id(), cmd.items());

		// Retornar orden con items cargados
		List<OrdenItem> items = ordenItemRepository.findItemsByOrdenId(orden.id());
		return new Orden(orden.id(), orden.clienteId(), orden.fechaCreacion(), items);
	}
}
