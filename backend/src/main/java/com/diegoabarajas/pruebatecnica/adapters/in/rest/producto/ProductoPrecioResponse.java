package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.ProductoPrecio;

public record ProductoPrecioResponse(
		String moneda,
		Double precio
) {
	public static ProductoPrecioResponse fromEntity(ProductoPrecio p) {
		return new ProductoPrecioResponse(p.getMoneda(), p.getPrecio());
	}
}

