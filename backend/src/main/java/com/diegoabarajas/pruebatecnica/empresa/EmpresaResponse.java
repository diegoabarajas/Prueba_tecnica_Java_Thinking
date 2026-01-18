package com.diegoabarajas.pruebatecnica.empresa;

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

