package com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria;

import com.diegoabarajas.pruebatecnica.core.application.categoria.CategoryService;
import com.diegoabarajas.pruebatecnica.core.application.categoria.UpsertCategoryCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public List<CategoryResponse> list() {
		return categoryService.list().stream().map(CategoryResponse::fromCore).toList();
	}

	@GetMapping("/{id}")
	public CategoryResponse get(@PathVariable Long id) {
		return CategoryResponse.fromCore(categoryService.get(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryResponse create(@Valid @RequestBody CategoryRequest req) {
		return CategoryResponse.fromCore(categoryService.create(new UpsertCategoryCommand(req.nombre())));
	}

	@PutMapping("/{id}")
	public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
		return CategoryResponse.fromCore(categoryService.update(id, new UpsertCategoryCommand(req.nombre())));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		categoryService.delete(id);
	}
}

