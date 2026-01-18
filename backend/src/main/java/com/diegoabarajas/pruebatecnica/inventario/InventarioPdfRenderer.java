package com.diegoabarajas.pruebatecnica.inventario;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Responsabilidad única: renderizar el inventario a PDF.
 * <p>
 * Se separa de {@link InventarioService} para mantener SRP (SOLID) y facilitar pruebas unitarias.
 */
@Component
public class InventarioPdfRenderer {

	public byte[] render(String empresaNit, List<InventarioItemResponse> items) throws IOException {
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

