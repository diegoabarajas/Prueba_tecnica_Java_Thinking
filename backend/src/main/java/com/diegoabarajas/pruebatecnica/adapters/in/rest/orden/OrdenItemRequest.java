package com.diegoabarajas.pruebatecnica.adapters.in.rest.orden;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de request para item de orden.
 */
public record OrdenItemRequest(
		@NotBlank(message = "productoCodigo es requerido")
		String productoCodigo,
		@NotNull(message = "cantidad es requerida")
		@Min(value = 1, message = "cantidad debe ser mayor a 0")
		Integer cantidad
) {
}
