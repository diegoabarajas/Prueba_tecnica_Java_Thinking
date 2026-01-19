package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductWithPrices;

public record ProductoResponse(
		String codigo,
		String nombre,
		String caracteristicas,
		String empresaNit,
		java.util.List<ProductoPrecioResponse> precios
) {
	public static ProductoResponse fromCore(ProductWithPrices p) {
		return new ProductoResponse(
				p.product().codigo(),
				p.product().nombre(),
				p.product().caracteristicas(),
				p.product().empresaNit(),
				p.precios() == null ? java.util.List.of() : p.precios().stream().map(ProductoPrecioResponse::fromCore).toList()
		);
	}
}

