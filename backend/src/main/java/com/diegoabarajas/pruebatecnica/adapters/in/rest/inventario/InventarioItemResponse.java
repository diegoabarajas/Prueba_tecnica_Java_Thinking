package com.diegoabarajas.pruebatecnica.adapters.in.rest.inventario;

import com.diegoabarajas.pruebatecnica.core.application.inventario.InventoryItem;

public record InventarioItemResponse(
		String empresaNit,
		String productoCodigo,
		String productoNombre,
		String caracteristicas
) {
	public static InventarioItemResponse fromCore(InventoryItem item) {
		return new InventarioItemResponse(
				item.empresaNit(),
				item.productoCodigo(),
				item.productoNombre(),
				item.caracteristicas()
		);
	}
}

