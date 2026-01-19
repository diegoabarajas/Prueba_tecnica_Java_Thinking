package com.diegoabarajas.pruebatecnica.core.application.categoria;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductWithPrices;
import com.diegoabarajas.pruebatecnica.core.application.producto.ProductoService;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CategoryRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductCategoryRepositoryPort;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Casos de uso para la relación Producto <-> Categoría.
 */
@Service
public class ProductCategoryService {

	private final ProductRepositoryPort productRepository;
	private final CategoryRepositoryPort categoryRepository;
	private final ProductCategoryRepositoryPort productCategoryRepository;
	private final ProductoService productoService;

	public ProductCategoryService(
			ProductRepositoryPort productRepository,
			CategoryRepositoryPort categoryRepository,
			ProductCategoryRepositoryPort productCategoryRepository,
			ProductoService productoService
	) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.productoService = productoService;
	}

	@Transactional
	public void replaceProductCategories(String productoCodigo, List<Long> categoriaIds) {
		if (productoCodigo == null || productoCodigo.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productoCodigo es requerido");
		}
		if (!productRepository.existsByCodigo(productoCodigo)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + productoCodigo);
		}
		List<Long> ids = (categoriaIds == null) ? List.of() : categoriaIds.stream().distinct().toList();
		for (Long id : ids) {
			if (id == null || !categoryRepository.existsById(id)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no existe: " + id);
			}
		}
		productCategoryRepository.replaceProductCategories(productoCodigo, ids);
	}

	@Transactional(readOnly = true)
	public List<Long> listCategoriaIdsByProducto(String productoCodigo) {
		if (productoCodigo == null || productoCodigo.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productoCodigo es requerido");
		}
		if (!productRepository.existsByCodigo(productoCodigo)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + productoCodigo);
		}
		return productCategoryRepository.findCategoriaIdsByProductoCodigo(productoCodigo);
	}

	@Transactional(readOnly = true)
	public List<ProductWithPrices> listProductosByCategoria(Long categoriaId) {
		if (categoriaId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoriaId es requerido");
		}
		if (!categoryRepository.existsById(categoriaId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
		}
		// Reutilizamos ProductoService para devolver productos con precios.
		// Para filtrar por categoría, primero obtenemos códigos y luego listamos por esos códigos.
		List<String> codigos = productCategoryRepository.findProductoCodigosByCategoriaId(categoriaId);
		if (codigos.isEmpty()) return List.of();
		return productoService.listByCodigos(codigos);
	}
}

