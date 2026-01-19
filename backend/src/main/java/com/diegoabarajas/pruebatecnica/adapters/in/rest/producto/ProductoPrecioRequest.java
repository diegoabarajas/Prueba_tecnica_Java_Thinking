package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ProductoPrecioRequest(
		@NotBlank
		@Pattern(regexp = "(?i)(COP|USD|EU)", message = "moneda debe ser una de: COP, USD, EU")
		String moneda,
		@NotNull
		Double precio
) {
}

