package com.diegoabarajas.pruebatecnica.core.application.cliente;

/**
 * Modelo core para Cliente.
 */
public record Cliente(
		Long id,
		String correo,
		String nombre
) {
}
