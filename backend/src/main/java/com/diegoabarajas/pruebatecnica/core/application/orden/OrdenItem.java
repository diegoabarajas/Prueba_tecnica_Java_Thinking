package com.diegoabarajas.pruebatecnica.core.application.orden;

/**
 * Item de una orden (producto + cantidad).
 */
public record OrdenItem(
		String productoCodigo,
		Integer cantidad
) {
}
