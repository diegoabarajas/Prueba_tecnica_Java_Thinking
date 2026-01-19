package com.diegoabarajas.pruebatecnica.adapters.out.persistence.empresa;

import com.diegoabarajas.pruebatecnica.core.application.empresa.Company;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador outbound: implementa el puerto {@link CompanyRepositoryPort} usando Spring Data JPA.
 */
@Component
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

	private final EmpresaRepository empresaRepository;

	public CompanyRepositoryAdapter(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	@Override
	public List<Company> findAll() {
		return empresaRepository.findAll().stream().map(CompanyRepositoryAdapter::toCore).toList();
	}

	@Override
	public Optional<Company> findByNit(String nit) {
		return empresaRepository.findById(nit).map(CompanyRepositoryAdapter::toCore);
	}

	@Override
	public boolean existsByNit(String nit) {
		return empresaRepository.existsById(nit);
	}

	@Override
	public Company save(Company company) {
		Empresa e = empresaRepository.findById(company.nit()).orElseGet(Empresa::new);
		e.setNit(company.nit());
		e.setNombre(company.nombre());
		e.setDireccion(company.direccion());
		e.setTelefono(company.telefono());
		return toCore(empresaRepository.save(e));
	}

	@Override
	public void deleteByNit(String nit) {
		empresaRepository.deleteById(nit);
	}

	private static Company toCore(Empresa e) {
		return new Company(e.getNit(), e.getNombre(), e.getDireccion(), e.getTelefono());
	}
}

