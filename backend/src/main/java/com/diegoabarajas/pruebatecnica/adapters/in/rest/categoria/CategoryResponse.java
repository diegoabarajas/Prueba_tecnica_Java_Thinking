package com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria;

import com.diegoabarajas.pruebatecnica.core.application.categoria.Category;

public record CategoryResponse(
		Long id,
		String nombre
) {
	public static CategoryResponse fromCore(Category c) {
		return new CategoryResponse(c.id(), c.nombre());
	}
}

