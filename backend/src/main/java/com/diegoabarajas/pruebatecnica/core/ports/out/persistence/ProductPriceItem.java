package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

/**
 * Proyección de persistencia para precios de producto:
 * incluye el código del producto (para agrupar) + moneda + precio.
 */
public record ProductPriceItem(
		String productoCodigo,
		String moneda,
		Double precio
) {
}

