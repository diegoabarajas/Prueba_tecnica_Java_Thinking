package com.diegoabarajas.pruebatecnica.inventario;

public record InventarioItemResponse(
		String empresaNit,
		String productoCodigo,
		String productoNombre,
		String caracteristicas
) {
}

