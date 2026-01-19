package com.diegoabarajas.pruebatecnica.core.application.categoria;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CategoryRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Casos de uso de categorías (CRUD mínimo).
 */
@Service
public class CategoryService {

	private final CategoryRepositoryPort categoryRepository;

	public CategoryService(CategoryRepositoryPort categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Transactional(readOnly = true)
	public List<Category> list() {
		return categoryRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Category get(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
	}

	@Transactional
	public Category create(UpsertCategoryCommand cmd) {
		if (cmd == null || cmd.nombre() == null || cmd.nombre().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es requerido");
		}
		return categoryRepository.save(new Category(null, cmd.nombre().trim()));
	}

	@Transactional
	public Category update(Long id, UpsertCategoryCommand cmd) {
		if (!categoryRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
		}
		if (cmd == null || cmd.nombre() == null || cmd.nombre().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es requerido");
		}
		return categoryRepository.save(new Category(id, cmd.nombre().trim()));
	}

	@Transactional
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
		}
		categoryRepository.deleteById(id);
	}
}

