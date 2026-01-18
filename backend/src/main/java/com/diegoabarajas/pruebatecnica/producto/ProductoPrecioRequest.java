package com.diegoabarajas.pruebatecnica.producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ProductoPrecioRequest(
		@NotBlank
		@Pattern(regexp = "(?i)[a-z]{3}", message = "moneda debe ser un código ISO de 3 letras (ej: COP, USD)")
		String moneda,
		@NotNull
		@DecimalMin(value = "0.0", inclusive = false, message = "precio debe ser > 0")
		BigDecimal precio
) {
}

