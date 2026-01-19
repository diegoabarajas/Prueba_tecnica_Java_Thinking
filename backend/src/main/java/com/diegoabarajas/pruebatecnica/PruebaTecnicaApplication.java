package com.diegoabarajas.pruebatecnica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot.
 *
 * <p>Responsabilidad:
 * - Arrancar el contenedor Spring (inyección de dependencias, web server embebido, configuración, etc.).
 *
 * <p>Por diseño, esta clase no contiene lógica de negocio. Toda la lógica vive en los módulos por dominio
 * (empresa/producto/inventario/email) y se organiza por capas (Controller -> Service -> Repository).
 */
@SpringBootApplication
public class PruebaTecnicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaTecnicaApplication.class, args);
	}

}
