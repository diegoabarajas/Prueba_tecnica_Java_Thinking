package com.diegoabarajas.pruebatecnica.adapters.out.persistence.categoria;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_categoria")
public class ProductoCategoria {

	@EmbeddedId
	private ProductoCategoriaId id;

	public ProductoCategoria() {}

	public ProductoCategoria(ProductoCategoriaId id) {
		this.id = id;
	}

	public ProductoCategoriaId getId() {
		return id;
	}

	public void setId(ProductoCategoriaId id) {
		this.id = id;
	}
}

