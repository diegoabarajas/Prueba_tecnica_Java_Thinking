package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ProductoPrecioRequest(
		@NotBlank
		@Pattern(regexp = "(?i)(COP|USD|UE)", message = "moneda debe ser una de: COP, USD, UE")
		String moneda,
		@NotNull
		Double precio
) {
}

