package com.diegoabarajas.pruebatecnica.adapters.out.pdf;

import com.diegoabarajas.pruebatecnica.core.application.inventario.InventoryItem;
import com.diegoabarajas.pruebatecnica.core.ports.out.pdf.InventarioPdfRendererPort;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsabilidad única: renderizar el inventario a PDF.
 * <p>
 * Se separa de {@link com.diegoabarajas.pruebatecnica.core.application.inventario.InventarioService}
 * para mantener SRP (SOLID) y facilitar pruebas unitarias.
 */
@Component
public class InventarioPdfRenderer implements InventarioPdfRendererPort {

	@Override
	public byte[] render(String empresaNit, List<InventoryItem> items) throws IOException {
		try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			PDPage page = new PDPage(PDRectangle.LETTER);
			doc.addPage(page);

			float margin = 50f;
			float tableTopPadding = 16f;
			float rowPaddingY = 4f;

			// Fuentes TrueType que soportan UTF-8 (caracteres especiales: á, é, í, ó, ú, ñ, etc.)
			// Intentamos cargar Arial del sistema; si falla, usamos Helvetica como fallback
			PDFont font;
			PDFont fontBold;
			try {
				font = PDType0Font.load(doc, new java.io.File("C:/Windows/Fonts/arial.ttf"));
				fontBold = PDType0Font.load(doc, new java.io.File("C:/Windows/Fonts/arialbd.ttf"));
			} catch (Exception e) {
				// Fallback a fuentes estándar si no se encuentra Arial (ej: Linux/Mac)
				font = PDType1Font.HELVETICA;
				fontBold = PDType1Font.HELVETICA_BOLD;
			}
			float fontSize = 10.5f;
			float headerFontSize = 11f;

			// Tabla con 3 columnas (código / nombre / características)
			float pageWidth = page.getMediaBox().getWidth();
			float tableX = margin;
			float tableWidth = pageWidth - (margin * 2);

			float colCode = 90f;
			float colName = 170f;
			float colFeatures = tableWidth - colCode - colName;

			String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

			PDPageContentStream cs = new PDPageContentStream(doc, page);
			float y = page.getMediaBox().getHeight() - margin;

			// Título
			cs.setFont(fontBold, 16);
			writeText(cs, tableX, y, "Inventario - Empresa " + empresaNit);
			y -= 18f;
			cs.setFont(font, 10);
			writeText(cs, tableX, y, "Generado: " + generatedAt);
			y -= (tableTopPadding);

			// Header (lo repetimos en cada página)
			y = drawHeader(cs, y, tableX, tableWidth, colCode, colName, colFeatures, headerFontSize, fontBold, rowPaddingY);

			boolean shade = false;
			for (InventoryItem it : items) {
				String code = safe(it.productoCodigo());
				String name = safe(it.productoNombre());
				String features = safe(it.caracteristicas());

				List<String> codeLines = wrap(font, fontSize, code, colCode - 8);
				List<String> nameLines = wrap(font, fontSize, name, colName - 8);
				List<String> featLines = wrap(font, fontSize, features, colFeatures - 8);

				int maxLines = Math.max(codeLines.size(), Math.max(nameLines.size(), featLines.size()));
				float lineHeight = fontSize + 2f;
				float rowHeight = (maxLines * lineHeight) + (rowPaddingY * 2);

				// Si no cabe, nueva página
				if (y - rowHeight < margin) {
					cs.close();
					page = new PDPage(PDRectangle.LETTER);
					doc.addPage(page);
					cs = new PDPageContentStream(doc, page);
					y = page.getMediaBox().getHeight() - margin;

					// En páginas siguientes, título más pequeño
					cs.setFont(fontBold, 13);
					writeText(cs, tableX, y, "Inventario - Empresa " + empresaNit);
					y -= 16f;
					cs.setFont(font, 10);
					writeText(cs, tableX, y, "Generado: " + generatedAt);
					y -= (tableTopPadding);

					y = drawHeader(cs, y, tableX, tableWidth, colCode, colName, colFeatures, headerFontSize, fontBold, rowPaddingY);
				}

				// Background alternado para legibilidad
				if (shade) {
					cs.setNonStrokingColor(new Color(245, 245, 245));
					cs.addRect(tableX, y - rowHeight, tableWidth, rowHeight);
					cs.fill();
				}
				shade = !shade;

				// Bordes
				cs.setStrokingColor(new Color(210, 210, 210));
				cs.addRect(tableX, y - rowHeight, tableWidth, rowHeight);
				// divisiones verticales
				cs.moveTo(tableX + colCode, y);
				cs.lineTo(tableX + colCode, y - rowHeight);
				cs.moveTo(tableX + colCode + colName, y);
				cs.lineTo(tableX + colCode + colName, y - rowHeight);
				cs.stroke();

				// Texto
				cs.setNonStrokingColor(Color.BLACK);
				cs.setFont(font, fontSize);
				float textY = y - rowPaddingY - fontSize;
				drawCellLines(cs, tableX + 4, textY, codeLines, lineHeight);
				drawCellLines(cs, tableX + colCode + 4, textY, nameLines, lineHeight);
				drawCellLines(cs, tableX + colCode + colName + 4, textY, featLines, lineHeight);

				y -= rowHeight;
			}

			cs.close();
			doc.save(baos);
			return baos.toByteArray();
		}
	}

	private static float drawHeader(
			PDPageContentStream cs,
			float y,
			float tableX,
			float tableWidth,
			float colCode,
			float colName,
			float colFeatures,
			float headerFontSize,
			PDFont fontBold,
			float rowPaddingY
	) throws IOException {
		float headerHeight = headerFontSize + 2f + (rowPaddingY * 2);

		cs.setNonStrokingColor(new Color(230, 230, 230));
		cs.addRect(tableX, y - headerHeight, tableWidth, headerHeight);
		cs.fill();

		cs.setStrokingColor(new Color(180, 180, 180));
		cs.addRect(tableX, y - headerHeight, tableWidth, headerHeight);
		cs.moveTo(tableX + colCode, y);
		cs.lineTo(tableX + colCode, y - headerHeight);
		cs.moveTo(tableX + colCode + colName, y);
		cs.lineTo(tableX + colCode + colName, y - headerHeight);
		cs.stroke();

		cs.setNonStrokingColor(Color.BLACK);
		cs.setFont(fontBold, headerFontSize);
		float textY = y - rowPaddingY - headerFontSize;
		writeText(cs, tableX + 4, textY, "Código");
		writeText(cs, tableX + colCode + 4, textY, "Nombre");
		writeText(cs, tableX + colCode + colName + 4, textY, "Características");

		return y - headerHeight;
	}

	private static void drawCellLines(PDPageContentStream cs, float x, float y, List<String> lines, float lineHeight) throws IOException {
		float yy = y;
		for (String line : lines) {
			writeText(cs, x, yy, line);
			yy -= lineHeight;
		}
	}

	private static void writeText(PDPageContentStream cs, float x, float y, String text) throws IOException {
		cs.beginText();
		cs.newLineAtOffset(x, y);
		cs.showText(text == null ? "" : text);
		cs.endText();
	}

	private static List<String> wrap(PDFont font, float fontSize, String text, float maxWidth) throws IOException {
		String t = text == null ? "" : text.trim();
		if (t.isEmpty()) return List.of("");

		String[] words = t.split("\\s+");
		List<String> lines = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		for (String w : words) {
			if (current.length() == 0) {
				current.append(w);
				continue;
			}
			String candidate = current + " " + w;
			if (textWidth(font, fontSize, candidate) <= maxWidth) {
				current.append(" ").append(w);
			} else {
				lines.add(truncateIfNeeded(font, fontSize, current.toString(), maxWidth));
				current.setLength(0);
				// palabra muy larga (sin espacios): la truncamos para no romper layout
				if (textWidth(font, fontSize, w) <= maxWidth) {
					current.append(w);
				} else {
					lines.add(truncateIfNeeded(font, fontSize, w, maxWidth));
				}
			}
		}
		if (current.length() > 0) {
			lines.add(truncateIfNeeded(font, fontSize, current.toString(), maxWidth));
		}
		return lines;
	}

	private static String truncateIfNeeded(PDFont font, float fontSize, String text, float maxWidth) throws IOException {
		String t = text == null ? "" : text;
		if (textWidth(font, fontSize, t) <= maxWidth) return t;

		String ellipsis = "...";
		String s = t;
		while (!s.isEmpty() && textWidth(font, fontSize, s + ellipsis) > maxWidth) {
			s = s.substring(0, s.length() - 1);
		}
		return s.isEmpty() ? ellipsis : (s + ellipsis);
	}

	private static float textWidth(PDFont font, float fontSize, String text) throws IOException {
		return (font.getStringWidth(text) / 1000f) * fontSize;
	}

	private static String safe(String s) {
		return s == null ? "" : s.replace("\n", " ").replace("\r", " ");
	}
}

