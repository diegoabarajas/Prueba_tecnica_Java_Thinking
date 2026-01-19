package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenProductoRepository extends JpaRepository<OrdenProducto, OrdenProductoId> {
	List<OrdenProducto> findById_OrdenId(Long ordenId);

	void deleteById_OrdenId(Long ordenId);
}
