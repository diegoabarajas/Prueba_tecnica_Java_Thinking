package com.diegoabarajas.pruebatecnica.producto;

import java.math.BigDecimal;

public record ProductoPrecioResponse(
		String moneda,
		BigDecimal precio
) {
	public static ProductoPrecioResponse fromEntity(ProductoPrecio p) {
		return new ProductoPrecioResponse(p.getMoneda(), p.getPrecio());
	}
}

