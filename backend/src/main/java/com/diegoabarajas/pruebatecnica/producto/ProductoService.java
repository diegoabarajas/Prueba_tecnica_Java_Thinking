package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.empresa.Empresa;
import com.diegoabarajas.pruebatecnica.empresa.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final EmpresaRepository empresaRepository;

	public ProductoService(ProductoRepository productoRepository, EmpresaRepository empresaRepository) {
		this.productoRepository = productoRepository;
		this.empresaRepository = empresaRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductoResponse> list(String empresaNit) {
		List<Producto> productos = (empresaNit == null || empresaNit.isBlank())
				? productoRepository.findAll()
				: productoRepository.findByEmpresa_Nit(empresaNit);
		return productos.stream().map(ProductoResponse::fromEntity).toList();
	}

	@Transactional
	public ProductoResponse create(ProductoRequest req) {
		if (productoRepository.existsById(req.codigo())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
		}
		Empresa empresa = empresaRepository.findById(req.empresaNit())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa no existe: " + req.empresaNit()));

		Producto p = new Producto();
		p.setCodigo(req.codigo());
		p.setNombre(req.nombre());
		p.setCaracteristicas(req.caracteristicas());
		p.setEmpresa(empresa);
		return ProductoResponse.fromEntity(productoRepository.save(p));
	}
}

