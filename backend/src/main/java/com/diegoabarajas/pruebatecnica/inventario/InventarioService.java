package com.diegoabarajas.pruebatecnica.inventario;

import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import com.diegoabarajas.pruebatecnica.producto.Producto;
import com.diegoabarajas.pruebatecnica.producto.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class InventarioService {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;
	private final InventarioPdfRenderer pdfRenderer;

	public InventarioService(
			ProductoRepository productoRepository,
			EmpresaRepository empresaRepository,
			InventarioPdfRenderer pdfRenderer
	) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
		this.pdfRenderer = pdfRenderer;
	}

	/**
	 * Inventario inicial: productos por empresa.
	 * Más adelante podemos extenderlo a una tabla inventario con cantidades, etc.
	 */
	@Transactional(readOnly = true)
	public List<InventarioItemResponse> list(String empresaNit) {
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

	@Transactional(readOnly = true)
	public byte[] buildPdf(String empresaNit) {
		List<InventarioItemResponse> items = list(empresaNit);
		try {
			return pdfRenderer.render(empresaNit, items);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF", e);
		}
	}
}

