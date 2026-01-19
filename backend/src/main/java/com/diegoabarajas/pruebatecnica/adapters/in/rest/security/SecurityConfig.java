package com.diegoabarajas.pruebatecnica.adapters.in.rest.security;

import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.Usuario;
import com.diegoabarajas.pruebatecnica.adapters.out.persistence.user.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * ConfiguraciÃ³n de seguridad para la API.
 *
 * <p>Objetivo del diseÃ±o:
 * - Mantener una autenticaciÃ³n simple para una prueba tÃ©cnica (HTTP Basic).
 * - Controlar permisos por rol (ADMIN/EXTERNO) mediante reglas por endpoint.
 * - Permitir que el frontend (Vite en localhost) consuma la API (CORS).
 *
 * <p>Notas:
 * - Se deshabilita CSRF porque esta es una API consumida por frontend separado (no usa cookies de sesiÃ³n).
 * - Para un entorno productivo tÃ­pico, se podrÃ­a migrar a JWT u OAuth2, pero se evitÃ³ complejidad innecesaria aquÃ­.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// API simple: HTTP Basic.
		// Regla general: lectura pÃºblica, mutaciones restringidas a ADMIN.
		http.csrf(csrf -> csrf.disable());
		http.cors(Customizer.withDefaults());

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/health").permitAll()
				// Endpoint usado por el frontend para validar credenciales y obtener el rol real.
				.requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
				// Categorías: lectura pública; mutaciones ADMIN.
				.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")
				// Asignación de categorías a producto: ADMIN.
				.requestMatchers(HttpMethod.PUT, "/api/productos/*/categorias").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/api/empresas/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/inventario/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/inventario/email/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/empresas/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/empresas/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/empresas/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
				.anyRequest().authenticated()
		);

		http.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		// BCrypt es un estÃ¡ndar seguro para hashing de contraseÃ±as (no reversible).
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
		// Adaptador: convierte nuestro Usuario (tabla usuarios) a UserDetails para Spring Security.
		// Importante: el rol se expone como "ROLE_ADMIN" o "ROLE_EXTERNO" (convenciÃ³n Spring).
		return username -> {
			Usuario u = usuarioRepository.findByEmailIgnoreCase(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
			return new User(u.getEmail(), u.getPasswordHash(), List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol())));
		};
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		// CORS habilita que el navegador permita llamadas cross-origin del frontend al backend.
		// Sin esto, el navegador bloquea requests aunque el backend responda (problema tÃ­pico "Failed to fetch").
		CorsConfiguration cfg = new CorsConfiguration();
		// Frontend Vite
		cfg.setAllowedOrigins(List.of("http://localhost:5173"));
		cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
		cfg.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cfg);
		return source;
	}
}

