package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import jakarta.persistence.*;

@Entity
@Table(name = "orden_producto")
public class OrdenProducto {

	@EmbeddedId
	private OrdenProductoId id;

	@Column(name = "cantidad", nullable = false)
	private Integer cantidad;

	public OrdenProducto() {
	}

	public OrdenProducto(OrdenProductoId id, Integer cantidad) {
		this.id = id;
		this.cantidad = cantidad;
	}

	public OrdenProductoId getId() {
		return id;
	}

	public void setId(OrdenProductoId id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
}
