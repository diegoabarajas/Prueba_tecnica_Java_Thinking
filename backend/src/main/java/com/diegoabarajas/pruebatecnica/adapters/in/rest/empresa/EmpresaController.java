package com.diegoabarajas.pruebatecnica.adapters.in.rest.empresa;

import com.diegoabarajas.pruebatecnica.core.application.empresa.CompanyUpsert;
import com.diegoabarajas.pruebatecnica.core.application.empresa.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

	private final EmpresaService empresaService;

	public EmpresaController(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}

	@GetMapping
	public List<EmpresaResponse> list() {
		return empresaService.list().stream().map(EmpresaResponse::fromCore).toList();
	}

	@GetMapping("/{nit}")
	public EmpresaResponse get(@PathVariable String nit) {
		return EmpresaResponse.fromCore(empresaService.get(nit));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EmpresaResponse create(@Valid @RequestBody EmpresaRequest req) {
		CompanyUpsert cmd = new CompanyUpsert(req.nit(), req.nombre(), req.direccion(), req.telefono());
		return EmpresaResponse.fromCore(empresaService.create(cmd));
	}

	@PutMapping("/{nit}")
	public EmpresaResponse update(@PathVariable String nit, @Valid @RequestBody EmpresaRequest req) {
		CompanyUpsert cmd = new CompanyUpsert(req.nit(), req.nombre(), req.direccion(), req.telefono());
		return EmpresaResponse.fromCore(empresaService.update(nit, cmd));
	}

	@DeleteMapping("/{nit}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String nit) {
		empresaService.delete(nit);
	}
}

