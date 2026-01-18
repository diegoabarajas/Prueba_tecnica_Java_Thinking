package com.diegoabarajas.pruebatecnica.producto;

import com.diegoabarajas.pruebatecnica.empresa.Empresa;
import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@Column(name = "codigo", nullable = false, length = 64)
	private String codigo;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "caracteristicas")
	private String caracteristicas;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "empresa_nit", nullable = false)
	private Empresa empresa;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(String caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}

