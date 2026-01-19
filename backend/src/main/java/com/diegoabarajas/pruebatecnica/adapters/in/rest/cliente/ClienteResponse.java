package com.diegoabarajas.pruebatecnica.adapters.in.rest.cliente;

import com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente;

/**
 * DTO de respuesta para cliente.
 */
public record ClienteResponse(
		Long id,
		String correo,
		String nombre
) {
	public static ClienteResponse fromCore(Cliente cliente) {
		return new ClienteResponse(cliente.id(), cliente.correo(), cliente.nombre());
	}
}
