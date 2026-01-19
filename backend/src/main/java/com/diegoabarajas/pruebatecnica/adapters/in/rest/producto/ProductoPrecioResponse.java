package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductPrice;

public record ProductoPrecioResponse(
		String moneda,
		Double precio
) {
	public static ProductoPrecioResponse fromCore(ProductPrice p) {
		return new ProductoPrecioResponse(p.moneda(), p.precio());
	}
}

