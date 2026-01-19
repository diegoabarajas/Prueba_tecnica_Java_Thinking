package com.diegoabarajas.pruebatecnica.core.application.empresa;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmpresaService {

	private final CompanyRepositoryPort companyRepository;

	public EmpresaService(CompanyRepositoryPort companyRepository) {
		this.companyRepository = companyRepository;
	}

	@Transactional(readOnly = true)
	public List<Company> list() {
		return companyRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Company get(String nit) {
		return companyRepository.findByNit(nit)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
	}

	@Transactional
	public Company create(CompanyUpsert req) {
		if (companyRepository.existsByNit(req.nit())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una empresa con ese NIT");
		}
		return companyRepository.save(new Company(req.nit(), req.nombre(), req.direccion(), req.telefono()));
	}

	@Transactional
	public Company update(String nit, CompanyUpsert req) {
		if (!nit.equals(req.nit())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El NIT del path debe coincidir con el body");
		}
		if (!companyRepository.existsByNit(nit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
		}
		return companyRepository.save(new Company(req.nit(), req.nombre(), req.direccion(), req.telefono()));
	}

	@Transactional
	public void delete(String nit) {
		if (!companyRepository.existsByNit(nit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
		}
		companyRepository.deleteByNit(nit);
	}
}

