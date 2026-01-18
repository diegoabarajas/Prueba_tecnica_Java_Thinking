package com.diegoabarajas.pruebatecnica.producto;

public record ProductoResponse(
		String codigo,
		String nombre,
		String caracteristicas,
		String empresaNit
) {
	public static ProductoResponse fromEntity(Producto p) {
		return new ProductoResponse(
				p.getCodigo(),
				p.getNombre(),
				p.getCaracteristicas(),
				p.getEmpresa().getNit()
		);
	}
}

