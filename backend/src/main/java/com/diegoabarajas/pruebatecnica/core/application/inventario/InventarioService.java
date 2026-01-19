package com.diegoabarajas.pruebatecnica.core.application.inventario;

import com.diegoabarajas.pruebatecnica.core.application.producto.Product;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CompanyRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.pdf.InventarioPdfRendererPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class InventarioService {

	private final ProductRepositoryPort productRepository;
	private final CompanyRepositoryPort companyRepository;
	private final InventarioPdfRendererPort pdfRenderer;

	public InventarioService(
			ProductRepositoryPort productRepository,
			CompanyRepositoryPort companyRepository,
			InventarioPdfRendererPort pdfRenderer
	) {
		this.productRepository = productRepository;
		this.companyRepository = companyRepository;
		this.pdfRenderer = pdfRenderer;
	}

	/**
	 * Inventario inicial: productos por empresa.
	 * Más adelante podemos extenderlo a una tabla inventario con cantidades, etc.
	 */
	@Transactional(readOnly = true)
	public List<InventoryItem> list(String empresaNit) {
		if (empresaNit == null || empresaNit.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empresaNit es requerido");
		}
		if (!companyRepository.existsByNit(empresaNit)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no existe: " + empresaNit);
		}
		List<Product> productos = productRepository.findByEmpresaNit(empresaNit);
		return productos.stream()
				.map(p -> new InventoryItem(
						empresaNit,
						p.codigo(),
						p.nombre(),
						p.caracteristicas()
				))
				.toList();
	}

	@Transactional(readOnly = true)
	public byte[] buildPdf(String empresaNit) {
		List<InventoryItem> items = list(empresaNit);
		try {
			return pdfRenderer.render(empresaNit, items);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF", e);
		}
	}
}

