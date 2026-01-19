package com.diegoabarajas.pruebatecnica.adapters.out.persistence.categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoCategoriaId implements Serializable {

	@Column(name = "producto_codigo", length = 64, nullable = false)
	private String productoCodigo;

	@Column(name = "categoria_id", nullable = false)
	private Long categoriaId;

	public ProductoCategoriaId() {}

	public ProductoCategoriaId(String productoCodigo, Long categoriaId) {
		this.productoCodigo = productoCodigo;
		this.categoriaId = categoriaId;
	}

	public String getProductoCodigo() {
		return productoCodigo;
	}

	public void setProductoCodigo(String productoCodigo) {
		this.productoCodigo = productoCodigo;
	}

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductoCategoriaId that = (ProductoCategoriaId) o;
		return Objects.equals(productoCodigo, that.productoCodigo) && Objects.equals(categoriaId, that.categoriaId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productoCodigo, categoriaId);
	}
}

