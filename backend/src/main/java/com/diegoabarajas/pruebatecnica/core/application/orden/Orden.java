package com.diegoabarajas.pruebatecnica.core.application.orden;

import java.time.Instant;
import java.util.List;

/**
 * Modelo core para Orden.
 */
public record Orden(
		Long id,
		Long clienteId,
		Instant fechaCreacion,
		List<OrdenItem> items
) {
}
