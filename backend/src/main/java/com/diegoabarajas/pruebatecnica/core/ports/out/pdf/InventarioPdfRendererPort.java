package com.diegoabarajas.pruebatecnica.core.ports.out.pdf;

import com.diegoabarajas.pruebatecnica.core.application.inventario.InventoryItem;

import java.io.IOException;
import java.util.List;

/**
 * Puerto de salida (outbound port) para generar el PDF del inventario.
 *
 * <p>El core depende de este contrato, no de una implementación concreta (PDFBox, iText, etc.).
 * La implementación vive en un adaptador outbound.
 */
public interface InventarioPdfRendererPort {
	byte[] render(String empresaNit, List<InventoryItem> items) throws IOException;
}

