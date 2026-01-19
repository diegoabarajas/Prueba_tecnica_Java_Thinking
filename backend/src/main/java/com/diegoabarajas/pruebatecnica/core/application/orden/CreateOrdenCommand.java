package com.diegoabarajas.pruebatecnica.core.application.orden;

import java.util.List;

/**
 * Comando para crear una orden.
 */
public record CreateOrdenCommand(
		Long clienteId,
		List<OrdenItem> items
) {
}
