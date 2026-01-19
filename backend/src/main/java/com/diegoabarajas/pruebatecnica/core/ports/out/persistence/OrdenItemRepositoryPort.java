package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenItem;

import java.util.List;

/**
 * Puerto de persistencia para items de orden (orden_producto).
 */
public interface OrdenItemRepositoryPort {
	/**
	 * Reemplaza todos los items de una orden.
	 */
	void replaceOrdenItems(Long ordenId, List<OrdenItem> items);

	/**
	 * Lista items de una orden.
	 */
	List<OrdenItem> findItemsByOrdenId(Long ordenId);
}
