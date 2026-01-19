package com.diegoabarajas.pruebatecnica.adapters.in.rest.cliente;

import com.diegoabarajas.pruebatecnica.core.application.cliente.Cliente;
import com.diegoabarajas.pruebatecnica.core.application.cliente.ClienteService;
import com.diegoabarajas.pruebatecnica.core.application.cliente.UpsertClienteCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de entrada (Inbound Adapter) para gestión de clientes vía REST.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping
	public List<ClienteResponse> list() {
		return clienteService.list().stream().map(ClienteResponse::fromCore).toList();
	}

	@GetMapping("/{id}")
	public ClienteResponse get(@PathVariable Long id) {
		return ClienteResponse.fromCore(clienteService.get(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClienteResponse create(@Valid @RequestBody ClienteRequest req) {
		Cliente cliente = clienteService.create(new UpsertClienteCommand(req.correo(), req.nombre()));
		return ClienteResponse.fromCore(cliente);
	}

	@PutMapping("/{id}")
	public ClienteResponse update(@PathVariable Long id, @Valid @RequestBody ClienteRequest req) {
		Cliente cliente = clienteService.update(id, new UpsertClienteCommand(req.correo(), req.nombre()));
		return ClienteResponse.fromCore(cliente);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		clienteService.delete(id);
	}
}
