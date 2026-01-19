package com.diegoabarajas.pruebatecnica.adapters.out.persistence.categoria;

import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductCategoryRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ProductCategoryRepositoryAdapter implements ProductCategoryRepositoryPort {

	private final ProductoCategoriaRepository productoCategoriaRepository;

	public ProductCategoryRepositoryAdapter(ProductoCategoriaRepository productoCategoriaRepository) {
		this.productoCategoriaRepository = productoCategoriaRepository;
	}

	@Override
	@Transactional
	public void replaceProductCategories(String productoCodigo, List<Long> categoriaIds) {
		productoCategoriaRepository.deleteByIdProductoCodigo(productoCodigo);
		for (Long id : categoriaIds) {
			productoCategoriaRepository.save(new ProductoCategoria(new ProductoCategoriaId(productoCodigo, id)));
		}
	}

	@Override
	public List<Long> findCategoriaIdsByProductoCodigo(String productoCodigo) {
		return productoCategoriaRepository.findByIdProductoCodigo(productoCodigo).stream()
				.map(pc -> pc.getId().getCategoriaId())
				.toList();
	}

	@Override
	public List<String> findProductoCodigosByCategoriaId(Long categoriaId) {
		return productoCategoriaRepository.findByIdCategoriaId(categoriaId).stream()
				.map(pc -> pc.getId().getProductoCodigo())
				.toList();
	}
}

