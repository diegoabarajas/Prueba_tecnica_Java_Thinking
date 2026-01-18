package com.diegoabarajas.pruebatecnica.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
	List<Producto> findByEmpresa_Nit(String empresaNit);
}

