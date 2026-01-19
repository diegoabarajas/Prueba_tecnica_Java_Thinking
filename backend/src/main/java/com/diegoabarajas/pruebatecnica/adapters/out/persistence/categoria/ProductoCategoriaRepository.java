package com.diegoabarajas.pruebatecnica.adapters.out.persistence.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoCategoriaRepository extends JpaRepository<ProductoCategoria, ProductoCategoriaId> {
	List<ProductoCategoria> findByIdProductoCodigo(String productoCodigo);

	List<ProductoCategoria> findByIdCategoriaId(Long categoriaId);

	void deleteByIdProductoCodigo(String productoCodigo);
}

