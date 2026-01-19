package com.diegoabarajas.pruebatecnica.adapters.in.rest.inventario;

import com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService;
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
		return inventarioService.list(empresaNit).stream()
				.map(InventarioItemResponse::fromCore)
				.toList();
	}

	@GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> pdf(@RequestParam String empresaNit) {
		byte[] pdfBytes = inventarioService.buildPdf(empresaNit);

		String filename = "inventario_" + empresaNit + ".pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8))
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE + "; charset=utf-8")
				.body(pdfBytes);
	}
}

