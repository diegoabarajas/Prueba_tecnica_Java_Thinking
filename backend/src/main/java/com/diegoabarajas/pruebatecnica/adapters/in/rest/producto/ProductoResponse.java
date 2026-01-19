package com.diegoabarajas.pruebatecnica.adapters.in.rest.producto;

import com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto.Producto;

public record ProductoResponse(
		String codigo,
		String nombre,
		String caracteristicas,
		String empresaNit,
		java.util.List<ProductoPrecioResponse> precios
) {
	public static ProductoResponse fromEntity(Producto p) {
		return new ProductoResponse(
				p.getCodigo(),
				p.getNombre(),
				p.getCaracteristicas(),
				p.getEmpresa().getNit(),
				java.util.List.of()
		);
	}
}

