package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.producto.Producto;
import com.diegoabarajas.pruebatecnica.producto.ProductoRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;

	public InventarioController(ProductoRepository productoRepository, EmpresaRepository empresaRepository) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
	}

	/**
	 * Inventario inicial: productos por empresa.
	 * Más adelante podemos extenderlo a una tabla inventario con cantidades, etc.
	 */
	@GetMapping
	public List<InventarioItemResponse> list(@RequestParam String empresaNit) {
		if (empresaNit == null || empresaNit.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empresaNit es requerido");
		}
		if (!empresaRepository.existsById(empresaNit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no existe: " + empresaNit);
		}
		List<Producto> productos = productoRepository.findByEmpresa_Nit(empresaNit);
		return productos.stream()
				.map(p -> new InventarioItemResponse(
						empresaNit,
						p.getCodigo(),
						p.getNombre(),
						p.getCaracteristicas()
				))
				.toList();
	}

	@GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> pdf(@RequestParam String empresaNit) {
		List<InventarioItemResponse> items = list(empresaNit);
		byte[] pdfBytes;
		try {
			pdfBytes = buildPdf(empresaNit, items);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF", e);
		}

		String filename = "inventario_" + empresaNit + ".pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfBytes);
	}

	private static byte[] buildPdf(String empresaNit, List<InventarioItemResponse> items) throws IOException {
		try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			float margin = 50;
			float leading = 14f;

			PDPage page = new PDPage(PDRectangle.LETTER);
			doc.addPage(page);
			PDPageContentStream cs = new PDPageContentStream(doc, page);

			float y = startPage(cs, page, empresaNit, margin, leading, true);

			for (InventarioItemResponse it : items) {
				if (y < margin) {
					cs.close();
					page = new PDPage(PDRectangle.LETTER);
					doc.addPage(page);
					cs = new PDPageContentStream(doc, page);
					y = startPage(cs, page, empresaNit, margin, leading, false);
				}
				String line = safe(it.productoCodigo()) + " | " + safe(it.productoNombre()) + " | " + safe(it.caracteristicas());
				writeLine(cs, margin, y, line);
				y -= leading;
			}

			cs.close();
			doc.save(baos);
			return baos.toByteArray();
		}
	}

	private static float startPage(
			PDPageContentStream cs,
			PDPage page,
			String empresaNit,
			float margin,
			float leading,
			boolean includeTitle
	) throws IOException {
		float y = page.getMediaBox().getHeight() - margin;

		if (includeTitle) {
			cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
			writeLine(cs, margin, y, "Inventario - Empresa " + empresaNit);
			y -= (leading * 2);
		}

		cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
		writeLine(cs, margin, y, "CODIGO | NOMBRE | CARACTERISTICAS");
		y -= leading;

		cs.setFont(PDType1Font.HELVETICA, 11);
		return y;
	}

	private static void writeLine(PDPageContentStream cs, float x, float y, String text) throws IOException {
		cs.beginText();
		cs.newLineAtOffset(x, y);
		cs.showText(text);
		cs.endText();
	}

	private static String safe(String s) {
		return s == null ? "" : s.replace("\n", " ").replace("\r", " ");
	}
}

