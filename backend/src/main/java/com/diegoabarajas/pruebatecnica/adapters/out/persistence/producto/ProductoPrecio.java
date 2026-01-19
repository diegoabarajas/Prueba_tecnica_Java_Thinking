package com.diegoabarajas.pruebatecnica.adapters.out.persistence.producto;

import jakarta.persistence.*;

@Entity
@Table(
		name = "producto_precios",
		uniqueConstraints = @UniqueConstraint(name = "uq_producto_precios_moneda", columnNames = {"producto_codigo", "moneda"})
)
public class ProductoPrecio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "producto_codigo", nullable = false)
	private Producto producto;

	@Column(name = "moneda", nullable = false, length = 3)
	private String moneda;

	@Column(name = "precio", nullable = false)
	private Double precio;

	public Long getId() {
		return id;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}
}

