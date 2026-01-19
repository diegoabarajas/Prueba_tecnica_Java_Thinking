package com.diegoabarajas.pruebatecnica.core.application.empresa;

/**
 * Comando del core para crear/actualizar una empresa.
 *
 * <p>Se usa para evitar que el core reciba DTOs REST directamente.
 */
public record CompanyUpsert(
		String nit,
		String nombre,
		String direccion,
		String telefono
) {
}

