package com.diegoabarajas.pruebatecnica.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
		@NotBlank @Size(max = 64) String codigo,
		@NotBlank @Size(max = 255) String nombre,
		String caracteristicas,
		@NotBlank @Size(max = 32) String empresaNit
) {
}

