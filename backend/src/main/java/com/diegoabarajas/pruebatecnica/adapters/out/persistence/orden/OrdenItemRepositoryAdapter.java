package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenItem;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.OrdenItemRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador JPA que implementa OrdenItemRepositoryPort.
 */
@Component
public class OrdenItemRepositoryAdapter implements OrdenItemRepositoryPort {

	private final OrdenProductoRepository jpaRepository;

	public OrdenItemRepositoryAdapter(OrdenProductoRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public void replaceOrdenItems(Long ordenId, List<OrdenItem> items) {
		// Eliminar items existentes
		jpaRepository.deleteById_OrdenId(ordenId);
		// Crear nuevos items
		for (OrdenItem item : items) {
			OrdenProductoId id = new OrdenProductoId(ordenId, item.productoCodigo());
			OrdenProducto entity = new OrdenProducto(id, item.cantidad());
			jpaRepository.save(entity);
		}
	}

	@Override
	public List<OrdenItem> findItemsByOrdenId(Long ordenId) {
		return jpaRepository.findById_OrdenId(ordenId).stream()
				.map(op -> new OrdenItem(op.getId().getProductoCodigo(), op.getCantidad()))
				.toList();
	}
}
