package com.diegoabarajas.pruebatecnica.core.application.producto;

import java.util.List;

/**
 * Proyección core: producto + lista de precios.
 *
 * <p>Se usa para devolver datos a adaptadores (REST) sin depender de DTOs externos.
 */
public record ProductWithPrices(
		Product product,
		List<ProductPrice> precios
) {
}

