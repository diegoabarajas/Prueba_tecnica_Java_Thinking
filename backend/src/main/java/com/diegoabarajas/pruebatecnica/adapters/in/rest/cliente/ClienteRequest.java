package com.diegoabarajas.pruebatecnica.adapters.in.rest.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de request para crear/actualizar cliente.
 */
public record ClienteRequest(
		@NotBlank(message = "correo es requerido")
		@Email(message = "correo debe ser un email válido")
		String correo,
		String nombre
) {
}
