package com.diegoabarajas.pruebatecnica.core.application.categoria;

/**
 * Comando core para crear/actualizar categorías.
 */
public record UpsertCategoryCommand(
		String nombre
) {
}

