package com.diegoabarajas.pruebatecnica.adapters.out.persistence.orden;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrdenProductoId implements Serializable {

	@Column(name = "orden_id", nullable = false)
	private Long ordenId;

	@Column(name = "producto_codigo", nullable = false, length = 64)
	private String productoCodigo;

	public OrdenProductoId() {
	}

	public OrdenProductoId(Long ordenId, String productoCodigo) {
		this.ordenId = ordenId;
		this.productoCodigo = productoCodigo;
	}

	public Long getOrdenId() {
		return ordenId;
	}

	public void setOrdenId(Long ordenId) {
		this.ordenId = ordenId;
	}

	public String getProductoCodigo() {
		return productoCodigo;
	}

	public void setProductoCodigo(String productoCodigo) {
		this.productoCodigo = productoCodigo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrdenProductoId that = (OrdenProductoId) o;
		return Objects.equals(ordenId, that.ordenId) && Objects.equals(productoCodigo, that.productoCodigo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ordenId, productoCodigo);
	}
}
