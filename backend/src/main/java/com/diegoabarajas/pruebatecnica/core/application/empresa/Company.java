package com.diegoabarajas.pruebatecnica.core.application.empresa;

/**
 * Modelo del core para una empresa.
 *
 * <p>Nota: este tipo no depende de JPA ni de DTOs REST. Los adaptadores se encargan de mapear.
 */
public record Company(
		String nit,
		String nombre,
		String direccion,
		String telefono
) {
}

