package com.diegoabarajas.pruebatecnica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicaciÃ³n Spring Boot.
 *
 * <p>Responsabilidad:
 * - Arrancar el contenedor Spring (inyecciÃ³n de dependencias, web server embebido, configuraciÃ³n, etc.).
 *
 * <p>Por diseÃ±o, esta clase no contiene lÃ³gica de negocio. Toda la lÃ³gica vive en los mÃ³dulos por dominio
 * (empresa/producto/inventario/email) y se organiza por capas (Controller -> Service -> Repository).
 */
@SpringBootApplication
public class PruebaTecnicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaTecnicaApplication.class, args);
	}

}
