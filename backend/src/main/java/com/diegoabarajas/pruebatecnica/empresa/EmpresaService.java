package com.diegoabarajas.pruebatecnica.empresa;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmpresaService {

	private final EmpresaRepository empresaRepository;

	public EmpresaService(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	@Transactional(readOnly = true)
	public List<EmpresaResponse> list() {
		return empresaRepository.findAll().stream().map(EmpresaResponse::fromEntity).toList();
	}

	@Transactional(readOnly = true)
	public EmpresaResponse get(String nit) {
		Empresa e = empresaRepository.findById(nit)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
		return EmpresaResponse.fromEntity(e);
	}

	@Transactional
	public EmpresaResponse create(EmpresaRequest req) {
		if (empresaRepository.existsById(req.nit())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una empresa con ese NIT");
		}
		Empresa e = new Empresa();
		e.setNit(req.nit());
		e.setNombre(req.nombre());
		e.setDireccion(req.direccion());
		e.setTelefono(req.telefono());
		return EmpresaResponse.fromEntity(empresaRepository.save(e));
	}

	@Transactional
	public EmpresaResponse update(String nit, EmpresaRequest req) {
		if (!nit.equals(req.nit())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El NIT del path debe coincidir con el body");
		}
		Empresa e = empresaRepository.findById(nit)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
		e.setNombre(req.nombre());
		e.setDireccion(req.direccion());
		e.setTelefono(req.telefono());
		return EmpresaResponse.fromEntity(empresaRepository.save(e));
	}

	@Transactional
	public void delete(String nit) {
		if (!empresaRepository.existsById(nit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
		}
		empresaRepository.deleteById(nit);
	}
}

