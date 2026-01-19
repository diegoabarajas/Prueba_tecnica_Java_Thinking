package com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa;

import com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa.Empresa;

/**
 * DTO de salida para no exponer directamente entidades JPA.
 */
public record EmpresaResponse(
		String nit,
		String nombre,
		String direccion,
		String telefono
) {
	public static EmpresaResponse fromEntity(Empresa e) {
		return new EmpresaResponse(e.getNit(), e.getNombre(), e.getDireccion(), e.getTelefono());
	}
}

