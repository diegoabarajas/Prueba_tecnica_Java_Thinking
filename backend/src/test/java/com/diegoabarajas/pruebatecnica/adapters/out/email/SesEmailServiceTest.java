package com.diegoabarajas.pruebatecnica.adapters.out.email;

// El test vive en el mismo paquete que SesEmailService para poder validar el builder MIME
// sin exponer el método (package-private) como parte de la API pública.
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SesEmailServiceTest {

	@Test
	void buildMime_containsAttachmentHeaders() {
		byte[] pdf = "%PDF-1.4\n%...".getBytes();
		String mime = SesEmailService.buildMime(
				"from@test.com",
				"to@test.com",
				"Inventario",
				"Hola",
				"inv.pdf",
				pdf,
				"BOUNDARY"
		);
		assertTrue(mime.contains("Content-Type: multipart/mixed"));
		assertTrue(mime.contains("Content-Type: application/pdf"));
		assertTrue(mime.contains("filename=\"inv.pdf\""));
		assertTrue(mime.contains("Content-Transfer-Encoding: base64"));
	}
}

