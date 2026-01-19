package com.diegoabarajas.pruebatecnica.core.application.cliente;

/**
 * Comando para crear/actualizar un cliente.
 */
public record UpsertClienteCommand(
		String correo,
		String nombre
) {
}
