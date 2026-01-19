package com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
		@NotBlank @Size(max = 32) String nit,
		@NotBlank @Size(max = 255) String nombre,
		@Size(max = 255) String direccion,
		@Size(max = 50) String telefono
) {
}

