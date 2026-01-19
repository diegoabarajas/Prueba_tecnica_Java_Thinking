package com.diegoabarajas.pruebatecnica.adapters.in.rest.inventario;

public record InventarioItemResponse(
		String empresaNit,
		String productoCodigo,
		String productoNombre,
		String caracteristicas
) {
}

