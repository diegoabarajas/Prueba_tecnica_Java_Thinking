package com.diegoabarajas.pruebatecnica.core.application.producto;

/**
 * Precio (moneda + valor) en el core.
 */
public record ProductPrice(
		String moneda,
		Double precio
) {
}

