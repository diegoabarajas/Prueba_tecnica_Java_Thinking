package com.diegoabarajas.pruebatecnica.adapters.out.persistence.categoria;

import com.diegoabarajas.pruebatecnica.core.application.categoria.Category;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.CategoryRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

	private final CategoriaRepository categoriaRepository;

	public CategoryRepositoryAdapter(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	@Override
	public List<Category> findAll() {
		return categoriaRepository.findAll().stream().map(CategoryRepositoryAdapter::toCore).toList();
	}

	@Override
	public Optional<Category> findById(Long id) {
		return categoriaRepository.findById(id).map(CategoryRepositoryAdapter::toCore);
	}

	@Override
	public boolean existsById(Long id) {
		return categoriaRepository.existsById(id);
	}

	@Override
	public Category save(Category category) {
		Categoria entity = new Categoria();
		entity.setId(category.id());
		entity.setNombre(category.nombre());
		Categoria saved = categoriaRepository.save(entity);
		return toCore(saved);
	}

	@Override
	public void deleteById(Long id) {
		categoriaRepository.deleteById(id);
	}

	private static Category toCore(Categoria c) {
		return new Category(c.getId(), c.getNombre());
	}
}

