package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.categoria.Category;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia (outbound port) para categorías.
 */
public interface CategoryRepositoryPort {
	List<Category> findAll();

	Optional<Category> findById(Long id);

	boolean existsById(Long id);

	Category save(Category category);

	void deleteById(Long id);
}

