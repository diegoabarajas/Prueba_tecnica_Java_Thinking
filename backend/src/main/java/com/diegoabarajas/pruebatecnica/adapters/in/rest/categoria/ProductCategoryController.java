package com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria;

import com.diegoabarajas.pruebatecnica.core.application.categoria.ProductCategoryService;
import com.diegoabarajas.pruebatecnica.adapters.in.rest.producto.ProductoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductCategoryController {

	private final ProductCategoryService productCategoryService;

	public ProductCategoryController(ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}

	/**
	 * Reemplaza categorías asignadas a un producto.
	 * ADMIN-only (regla en SecurityConfig).
	 */
	@PutMapping("/api/productos/{codigo}/categorias")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void replaceProductCategories(@PathVariable String codigo, @Valid @RequestBody ProductCategoriesRequest req) {
		productCategoryService.replaceProductCategories(codigo, req.categoriaIds());
	}

	/**
	 * Lista IDs de categorías asignadas a un producto (público).
	 */
	@GetMapping("/api/productos/{codigo}/categorias")
	public List<Long> listProductCategories(@PathVariable String codigo) {
		return productCategoryService.listCategoriaIdsByProducto(codigo);
	}

	/**
	 * Lista productos asociados a una categoría (público).
	 */
	@GetMapping("/api/categorias/{id}/productos")
	public List<ProductoResponse> listProductsByCategory(@PathVariable Long id) {
		return productCategoryService.listProductosByCategoria(id).stream()
				.map(ProductoResponse::fromCore)
				.toList();
	}
}

