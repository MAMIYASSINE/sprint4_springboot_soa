package com.project.voitures.sercurity;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	KeycloakRoleConverter keycloakRoleConverter;
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
					CorsConfiguration cors = new CorsConfiguration();
					cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
					cors.setAllowedMethods(Collections.singletonList("*"));
					cors.setAllowCredentials(true);
					cors.setAllowedHeaders(Collections.singletonList("*"));
					cors.setExposedHeaders(Arrays.asList("Authorization"));					
					cors.setMaxAge(360L);					
					return cors;
					}
					}).and() )
				.authorizeHttpRequests(requests -> requests
						.requestMatchers("/api/all/**").hasAnyAuthority("ADMIN","USER")//.permitAll()//.hasAnyAuthority("ADMIN","USER")
						.requestMatchers(HttpMethod.GET,"/api/getbyid/**").hasAnyAuthority("ADMIN","USER")
						//.requestMatchers(HttpMethod.POST,"/api/addvoiture/**").hasAnyAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT,"/api/updatevoiture/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE,"/api/delvoiture/**").hasAuthority("ADMIN")
						.anyRequest().authenticated())
				.oauth2ResourceServer(ors->ors.jwt(jwt-> jwt.jwtAuthenticationConverter(keycloakRoleConverter)));
				//.oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));
		return http.build();
	}
}
