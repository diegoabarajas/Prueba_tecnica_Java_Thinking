package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductoRequest(
		@NotBlank @Size(max = 64) String codigo,
		@NotBlank @Size(max = 255) String nombre,
		String caracteristicas,
		@NotBlank @Size(max = 32) String empresaNit,
		@Valid List<ProductoPrecioRequest> precios
) {
}

