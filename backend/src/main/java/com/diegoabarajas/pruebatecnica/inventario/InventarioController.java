package com.diegoabarajas.pruebatecnica.inventario;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	private final InventarioService inventarioService;

	public InventarioController(InventarioService inventarioService) {
		this.inventarioService = inventarioService;
	}

	/**
	 * Inventario inicial: productos por empresa.
	 * Más adelante podemos extenderlo a una tabla inventario con cantidades, etc.
	 */
	@GetMapping
	public List<InventarioItemResponse> list(@RequestParam String empresaNit) {
		return inventarioService.list(empresaNit);
	}

	@GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> pdf(@RequestParam String empresaNit) {
		byte[] pdfBytes = inventarioService.buildPdf(empresaNit);

		String filename = "inventario_" + empresaNit + ".pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfBytes);
	}
}

