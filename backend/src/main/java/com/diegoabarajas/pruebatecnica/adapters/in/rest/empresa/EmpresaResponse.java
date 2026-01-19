package com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa;

import com.diegoabarajas.pruebatecnica.core.application.empresa.Company;

/**
 * DTO de salida para no exponer directamente entidades JPA.
 */
public record EmpresaResponse(
		String nit,
		String nombre,
		String direccion,
		String telefono
) {
	public static EmpresaResponse fromCore(Company c) {
		return new EmpresaResponse(c.nit(), c.nombre(), c.direccion(), c.telefono());
	}
}

