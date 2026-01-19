package com.diegoabarajas.pruebatecnica.core.application.inventario;

/**
 * Modelo interno (core) para representar un ítem de inventario.
 *
 * <p>Nota de arquitectura:
 * - Este tipo vive en el core para que la lógica de inventario no dependa de DTOs REST.
 * - Los adaptadores (REST/PDF) se encargan de mapear este modelo a sus representaciones externas.
 */
public record InventoryItem(
		String empresaNit,
		String productoCodigo,
		String productoNombre,
		String caracteristicas
) {
}

