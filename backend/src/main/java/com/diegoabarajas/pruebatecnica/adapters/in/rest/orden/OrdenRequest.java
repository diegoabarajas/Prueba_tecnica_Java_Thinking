package com.diegoabarajas.pruebatecnica.adapters.in.rest.orden;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO de request para crear orden.
 */
public record OrdenRequest(
		@NotNull(message = "clienteId es requerido")
		Long clienteId,
		@NotEmpty(message = "items es requerido y no puede estar vacío")
		@Valid
		List<OrdenItemRequest> items
) {
}
