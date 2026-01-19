package com.diegoabarajas.pruebatecnica.adapters.in.rest.orden;

import com.diegoabarajas.pruebatecnica.core.application.orden.CreateOrdenCommand;
import com.diegoabarajas.pruebatecnica.core.application.orden.Orden;
import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenItem;
import com.diegoabarajas.pruebatecnica.core.application.orden.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de entrada (Inbound Adapter) para gestión de órdenes vía REST.
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

	private final OrdenService ordenService;

	public OrdenController(OrdenService ordenService) {
		this.ordenService = ordenService;
	}

	@GetMapping
	public List<OrdenResponse> list(@RequestParam(required = false) Long clienteId) {
		List<Orden> ordenes = clienteId == null ? ordenService.list() : ordenService.listByCliente(clienteId);
		return ordenes.stream().map(OrdenResponse::fromCore).toList();
	}

	@GetMapping("/{id}")
	public OrdenResponse get(@PathVariable Long id) {
		return OrdenResponse.fromCore(ordenService.get(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrdenResponse create(@Valid @RequestBody OrdenRequest req) {
		List<OrdenItem> items = req.items().stream()
				.map(i -> new OrdenItem(i.productoCodigo(), i.cantidad()))
				.toList();
		Orden orden = ordenService.create(new CreateOrdenCommand(req.clienteId(), items));
		return OrdenResponse.fromCore(orden);
	}
}
