package com.diegoabarajas.pruebatecnica.config;

import com.diegoabarajas.pruebatecnica.user.Usuario;
import com.diegoabarajas.pruebatecnica.user.UsuarioRepository;
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

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// API simple para empezar: HTTP Basic + reglas por endpoint.
		// Más adelante lo cambiamos a JWT si decides seguir ese camino.
		http.csrf(csrf -> csrf.disable());

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/health").permitAll()
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
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
		return username -> {
			Usuario u = usuarioRepository.findByEmailIgnoreCase(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
			return new User(u.getEmail(), u.getPasswordHash(), List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol())));
		};
	}
}

