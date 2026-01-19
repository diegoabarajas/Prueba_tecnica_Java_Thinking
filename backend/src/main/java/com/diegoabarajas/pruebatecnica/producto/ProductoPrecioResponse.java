package com.diegoabarajas.pruebatecnica.producto;

public record ProductoPrecioResponse(
		String moneda,
		Double precio
) {
	public static ProductoPrecioResponse fromEntity(ProductoPrecio p) {
		return new ProductoPrecioResponse(p.getMoneda(), p.getPrecio());
	}
}

