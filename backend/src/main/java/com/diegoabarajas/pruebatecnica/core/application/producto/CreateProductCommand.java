package com.diegoabarajas.pruebatecnica.core.application.producto;

import java.util.List;

/**
 * Comando core para creación de producto.
 *
 * <p>Se usa para evitar que el core dependa de DTOs REST.
 */
public record CreateProductCommand(
		String codigo,
		String nombre,
		String caracteristicas,
		String empresaNit,
		List<ProductPrice> precios
) {
}

