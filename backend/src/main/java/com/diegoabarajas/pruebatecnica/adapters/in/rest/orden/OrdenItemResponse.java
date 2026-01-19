package com.diegoabarajas.pruebatecnica.adapters.in.rest.orden;

import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenItem;

/**
 * DTO de respuesta para item de orden.
 */
public record OrdenItemResponse(
		String productoCodigo,
		Integer cantidad
) {
	public static OrdenItemResponse fromCore(OrdenItem item) {
		return new OrdenItemResponse(item.productoCodigo(), item.cantidad());
	}
}
