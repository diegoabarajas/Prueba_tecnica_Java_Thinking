package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import java.util.List;

/**
 * Puerto de persistencia para la relación Producto <-> Categoría (M:N).
 */
public interface ProductCategoryRepositoryPort {
	/**
	 * Reemplaza la lista completa de categorías de un producto.
	 */
	void replaceProductCategories(String productoCodigo, List<Long> categoriaIds);

	/**
	 * Lista categorías asignadas a un producto.
	 */
	List<Long> findCategoriaIdsByProductoCodigo(String productoCodigo);

	/**
	 * Lista productos asociados a una categoría.
	 */
	List<String> findProductoCodigosByCategoriaId(Long categoriaId);
}

