package com.diegoabarajas.pruebatecnica.adapters.in.rest.categoria;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductCategoriesRequest(
		@NotNull List<Long> categoriaIds
) {
}

