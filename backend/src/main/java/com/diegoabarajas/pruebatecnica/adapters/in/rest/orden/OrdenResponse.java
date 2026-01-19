package com.diegoabarajas.pruebatecnica.adapters.in.rest.orden;

import com.diegoabarajas.pruebatecnica.core.application.orden.Orden;
import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenItem;

import java.time.Instant;
import java.util.List;

/**
 * DTO de respuesta para orden.
 */
public record OrdenResponse(
		Long id,
		Long clienteId,
		Instant fechaCreacion,
		List<OrdenItemResponse> items
) {
	public static OrdenResponse fromCore(Orden orden) {
		List<OrdenItemResponse> items = orden.items() == null ? List.of() : orden.items().stream()
				.map(OrdenItemResponse::fromCore)
				.toList();
		return new OrdenResponse(orden.id(), orden.clienteId(), orden.fechaCreacion(), items);
	}
}
