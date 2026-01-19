package com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto;

import com.diegoabarajas.pruebatecnica.core.application.producto.ProductPrice;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductPriceItem;
import com.diegoabarajas.pruebatecnica.core.ports.out.persistence.ProductPriceRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador outbound: implementa {@link ProductPriceRepositoryPort} usando Spring Data JPA.
 */
@Component
public class ProductPriceRepositoryAdapter implements ProductPriceRepositoryPort {

	private final ProductoPrecioRepository productoPrecioRepository;
	private final ProductoRepository productoRepository;

	public ProductPriceRepositoryAdapter(ProductoPrecioRepository productoPrecioRepository, ProductoRepository productoRepository) {
		this.productoPrecioRepository = productoPrecioRepository;
		this.productoRepository = productoRepository;
	}

	@Override
	public List<ProductPriceItem> findByProductoCodigos(Collection<String> codigos) {
		return productoPrecioRepository.findByProducto_CodigoInOrderByIdAsc(codigos).stream()
				.map(pp -> new ProductPriceItem(
						pp.getProducto().getCodigo(),
						pp.getMoneda(),
						pp.getPrecio()
				))
				.toList();
	}

	@Override
	public List<ProductPrice> saveAll(String productoCodigo, List<ProductPrice> precios) {
		Producto producto = productoRepository.findById(productoCodigo).orElseThrow();
		return precios.stream().map(p -> {
			ProductoPrecio entity = new ProductoPrecio();
			entity.setProducto(producto);
			entity.setMoneda(p.moneda());
			entity.setPrecio(p.precio());
			ProductoPrecio saved = productoPrecioRepository.save(entity);
			return new ProductPrice(saved.getMoneda(), saved.getPrecio());
		}).toList();
	}
}

