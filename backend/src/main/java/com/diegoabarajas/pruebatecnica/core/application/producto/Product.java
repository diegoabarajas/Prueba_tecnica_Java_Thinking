package com.diegoabarajas.pruebatecnica.core.application.producto;

/**
 * Modelo core para producto.
 */
public record Product(
		String codigo,
		String nombre,
		String caracteristicas,
		String empresaNit
) {
}

