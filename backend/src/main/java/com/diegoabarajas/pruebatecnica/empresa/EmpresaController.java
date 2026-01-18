package com.diegoabarajas.pruebatecnica.empresa;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

	private final EmpresaRepository empresaRepository;

	public EmpresaController(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	@GetMapping
	public List<Empresa> list() {
		return empresaRepository.findAll();
	}

	@GetMapping("/{nit}")
	public Empresa get(@PathVariable String nit) {
		return empresaRepository.findById(nit)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Empresa create(@Valid @RequestBody EmpresaRequest req) {
		if (empresaRepository.existsById(req.nit())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una empresa con ese NIT");
		}
		Empresa e = new Empresa();
		e.setNit(req.nit());
		e.setNombre(req.nombre());
		e.setDireccion(req.direccion());
		e.setTelefono(req.telefono());
		return empresaRepository.save(e);
	}

	@PutMapping("/{nit}")
	public Empresa update(@PathVariable String nit, @Valid @RequestBody EmpresaRequest req) {
		if (!nit.equals(req.nit())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El NIT del path debe coincidir con el body");
		}
		Empresa e = empresaRepository.findById(nit)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
		e.setNombre(req.nombre());
		e.setDireccion(req.direccion());
		e.setTelefono(req.telefono());
		return empresaRepository.save(e);
	}

	@DeleteMapping("/{nit}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String nit) {
		if (!empresaRepository.existsById(nit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
		}
		empresaRepository.deleteById(nit);
	}
}

