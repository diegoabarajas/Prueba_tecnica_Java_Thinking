package com.diegoabarajas.pruebatecnica.adapters.in.rest.email;

import com.diegoabarajas.pruebatecnica.adapters.out.email.SesEmailService;
import com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/email")
public class InventarioEmailController {

	private final InventarioService inventarioService;
	private final SesEmailService sesEmailService;

	public InventarioEmailController(InventarioService inventarioService, SesEmailService sesEmailService) {
		this.inventarioService = inventarioService;
		this.sesEmailService = sesEmailService;
	}

	/**
	 * Envía el PDF de inventario por email usando AWS SES.
	 * Requiere rol ADMIN (configurado en SecurityConfig).
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void send(@Valid @RequestBody EmailRequest req) {
		byte[] pdf = inventarioService.buildPdf(req.empresaNit());
		String filename = "inventario_" + req.empresaNit() + ".pdf";
		sesEmailService.sendPdf(req.toEmail(), req.subject(), req.message(), filename, pdf);
	}
}

