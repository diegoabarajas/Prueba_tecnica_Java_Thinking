package com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
		@NotBlank @Size(max = 255) String nombre
) {
}

