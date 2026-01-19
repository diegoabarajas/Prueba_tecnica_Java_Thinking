package com.diegoabarajas.pruebatecnica.core.ports.out.persistence;

import com.diegoabarajas.pruebatecnica.core.application.empresa.Company;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia (outbound port) para empresas.
 *
 * <p>El core depende de este contrato, y la infraestructura (JPA/PostgreSQL) lo implementa
 * mediante un adaptador.
 */
public interface CompanyRepositoryPort {
	List<Company> findAll();

	Optional<Company> findByNit(String nit);

	boolean existsByNit(String nit);

	Company save(Company company);

	void deleteByNit(String nit);
}

